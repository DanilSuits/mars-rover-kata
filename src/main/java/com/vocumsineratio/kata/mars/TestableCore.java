/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.in;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {

    static class Lines {
        interface Parser<T> extends Plumbing.Parser<List<String>, T> {};

        interface Model extends Plumbing.Model<List<String>> {};

        static class Database implements Plumbing.Database<List<String>> {

            private final InputStream fromDatabase;
            private final PrintStream toDatabase;

            Database(InputStream fromDatabase, PrintStream toDatabase) {
                this.fromDatabase = fromDatabase;
                this.toDatabase = toDatabase;
            }

            @Override
            public List<String> load() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fromDatabase));
                return reader.lines().collect(Collectors.toList());
            }

            @Override
            public void store(List<String> data) {
                data
                        .stream()
                        .forEach(toDatabase::println);
            }
        }
    }

    static class LinesDataModel {

        static final Lines.Parser<Squad> TO_SQUAD = new Lines.Parser<Squad>() {
            @Override
            public Squad parse(List<String> lines) {
                return new Squad(Parser.toSquad(lines));
            }
        };

        static final class Squad extends Domain.Squad implements Lines.Model {
            Squad(List<Domain.Rover> squad) {
                super(squad);
            }

            @Override
            public List<String> toDocument() {
                return squad
                        .stream()
                        .map(rover -> rover.position)
                        .map(roverPosition -> roverPosition.location.x + " " + roverPosition.location.y + " " + roverPosition.heading.name())
                        .collect(Collectors.toList());
            }
        }

        static class Parser {

            static final Function<String, Domain.Position> parsePosition = line -> {
                final String currentLocation = line.substring(0, line.length()-2);
                String[] rawCoordinates = currentLocation.split(" ");

                int xPos = Integer.parseInt(rawCoordinates[0]);
                int yPos = Integer.parseInt(rawCoordinates[1]);
                Domain.Location roverLocation = new Domain.Location(xPos, yPos);

                final String startHeading = line.substring(line.length() - 1);
                Domain.CompassPoint currentHeading = Domain.CompassPoint.valueOf(startHeading);

                return new Domain.Position(roverLocation, currentHeading);
            };

            static Domain.Rover from(String rawPosition, String rawInstructions) {
                return new Domain.Rover(
                        parsePosition.apply(rawPosition),
                        new Domain.Program(rawInstructions)
                );
            }

            static List<Domain.Rover> toSquad(List<String> lines) {
                List<Domain.Rover> squad = new ArrayList<>();

                int POSITION_OFFSET = 0;
                int INSTRUCTION_OFFSET = 1;
                {
                    int FIRST_ROVER_OFFSET = 1;
                    int INPUT_LINES_PER_ROVER = 2;

                    for (int index = FIRST_ROVER_OFFSET; index < lines.size(); index += INPUT_LINES_PER_ROVER) {
                        String rawPosition = lines.get(POSITION_OFFSET + index);
                        final String rawInstructions = lines.get(INSTRUCTION_OFFSET + index);

                        squad.add(
                                Parser.from(rawPosition, rawInstructions)
                        );
                    }
                }

                return squad;
            }
        }
    }

    static class CompositionRoot {
        static Application create(Plumbing.Database<List<String>> db) {
            final Lines.Parser<LinesDataModel.Squad> parser = LinesDataModel.TO_SQUAD;
            Plumbing.Repository<List<String>, LinesDataModel.Squad> repo = new Plumbing.Repository(db, parser);
            return new Application(repo);
        }

        static Application create(InputStream in, PrintStream out) {
            // Composition!
            InputStream fromDatabase = in;
            PrintStream toDatabase = out;
            Lines.Database db = new Lines.Database(fromDatabase, toDatabase);

            return create(db);
        }
    }

    static void runTest(InputStream in, PrintStream out) throws IOException {
        {
            // FOR TEST CALIBRATION ONLY
            if (false) return;
        }

        Application app = CompositionRoot.create(in, out);
        app.handleCommand();

    }

    public static void main(String[] args) throws IOException {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(in, System.out);
    }
}
