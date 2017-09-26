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
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.in;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    enum CompassPoint {
        N, W, S, E
    }

    static final EnumMap<CompassPoint, CompassPoint> LEFT = new EnumMap<>(CompassPoint.class);
    static {
        LEFT.put(CompassPoint.N, CompassPoint.W);
        LEFT.put(CompassPoint.W, CompassPoint.S);
        LEFT.put(CompassPoint.S, CompassPoint.E);
        LEFT.put(CompassPoint.E, CompassPoint.N);
    }

    static final EnumMap<CompassPoint, CompassPoint> RIGHT = new EnumMap<>(CompassPoint.class);
    static {
        RIGHT.put(CompassPoint.E, CompassPoint.S);
        RIGHT.put(CompassPoint.S, CompassPoint.W);
        RIGHT.put(CompassPoint.W, CompassPoint.N);
        RIGHT.put(CompassPoint.N, CompassPoint.E);
    }

    static final EnumMap<CompassPoint, int []> MOVE = new EnumMap<>(CompassPoint.class);
    static {
        MOVE.put(CompassPoint.N, new int[]{0, 1});
        MOVE.put(CompassPoint.S, new int[]{0, -1});
        MOVE.put(CompassPoint.E, new int[]{1, 0});
        MOVE.put(CompassPoint.W, new int[]{-1, 0});
    }

    static class Location {
        int x;
        int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Position {
        Location location;
        CompassPoint heading;

        Position(Location location, CompassPoint heading) {
            this.location = location;
            this.heading = heading;
        }

        void left() {
            heading = LEFT.get(heading);
        }

        void right() {
            heading = RIGHT.get(heading);
        }

        void move() {
            int[] moves = MOVE.get(heading);
            location.x += moves[0];
            location.y += moves[1];
        }
    }

    static class Rover {
        Position position;
        Program instructions;

        Rover(Position position, Program instructions) {
            this.position = position;
            this.instructions = instructions;
        }
    }

    static class Program {
        String remainingInstructions;

        Program(String remainingInstructions) {
            this.remainingInstructions = remainingInstructions;
        }

        boolean hasCurrent() {
            return ! remainingInstructions.isEmpty();
        }

        String current () {
            return remainingInstructions.substring(FIRST_INSTRUCTION_OFFSET, NEXT_INSTRUCTION_OFFSET);
        }

        void next() {
            remainingInstructions = remainingInstructions.substring(NEXT_INSTRUCTION_OFFSET);
        }

        static final int FIRST_INSTRUCTION_OFFSET = 0;
        static final int NEXT_INSTRUCTION_OFFSET = 1;
    }

    static class Parser {

        static final Function<String, Position> parsePosition = line -> {
            final String currentLocation = line.substring(0, line.length()-2);
            String[] rawCoordinates = currentLocation.split(" ");

            int xPos = Integer.parseInt(rawCoordinates[0]);
            int yPos = Integer.parseInt(rawCoordinates[1]);
            Location roverLocation = new Location(xPos, yPos);

            final String startHeading = line.substring(line.length() - 1);
            CompassPoint currentHeading = CompassPoint.valueOf(startHeading);

            return new Position(roverLocation, currentHeading);
        };

        static Rover from(String rawPosition, String rawInstructions) {
            return new Rover(
                    parsePosition.apply(rawPosition),
                    new Program(rawInstructions)
            );
        }

        static List<Rover> toSquad(List<String> lines) {
            List<Rover> squad = new ArrayList<>();

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

    static class API {
        interface Repository<T> {
            T get();
            void save(T theThing);
        }

        interface Squad {
            void run();
        }
    }

    static class Domain {
        static class Squad implements API.Squad {
            List<Rover> squad;

            Squad(List<Rover> squad) {
                this.squad = squad;
            }

            @Override
            public void run() {
                for (Rover rover : squad) {

                    Position roverPosition = rover.position;
                    Program instructions = rover.instructions;

                    while (instructions.hasCurrent()) {

                        final String currentInstruction = instructions.current();

                        // PROCESS INSTRUCTIONS
                        {
                            if ("L".equals(currentInstruction)) {
                                roverPosition.left();
                            }

                            if ("R".equals(currentInstruction)) {
                                roverPosition.right();
                            }

                            if ("M".equals(currentInstruction)) {
                                roverPosition.move();
                            }
                        }

                        instructions.next();
                    }
                }
            }
        }
    }

    static class Plumbing {
        interface Database<T> {
            T load();
            void store(T thing);
        }

        interface Factory<Document, Model> {
            Model create(Document source);
        }

        interface Model<Document> {
            Document toDocument();
        }
    }

    static class Lines {
        interface Factory<T> extends Plumbing.Factory<List<String>, T> {};

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

        static class Repository<Document, M extends Plumbing.Model<Document>> implements API.Repository<M> {
            private final Plumbing.Database<Document> database;
            Plumbing.Factory<Document, M> factory;

            Repository(Plumbing.Database<Document> database, Plumbing.Factory<Document, M> factory) {
                this.database = database;
                this.factory = factory;
            }

            @Override
            public M get() {
                Document lines = database.load();
                return factory.create(lines);
            }

            @Override
            public void save(M squad) {
                Document data = squad.toDocument();
                database.store(data);
            }
        }
    }

    static class LinesDataModel {
        static Lines.Repository connect(Lines.Database db) {
            Lines.Repository repo = new Lines.Repository(db, LinesDataModel.TO_SQUAD);
            return repo;
        }

        static final Lines.Factory<Squad> TO_SQUAD = new Lines.Factory<Squad>() {
            @Override
            public Squad create(List<String> lines) {
                return new Squad(Parser.toSquad(lines));
            }
        };

        static class Squad extends Domain.Squad implements Lines.Model {
            Squad(List<Rover> squad) {
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

    }

    static class Application<Squad extends API.Squad> {
        private final API.Repository<Squad> repo;

        Application(API.Repository<Squad> repo) {
            this.repo = repo;
        }

        void handleCommand () {
            Squad squad = repo.get();

            squad.run();

            repo.save(squad);
        }
    }

    static class CompositionRoot {
        static Application create(InputStream in, PrintStream out) {
            // Composition!
            InputStream fromDatabase = in;
            PrintStream toDatabase = out;
            Lines.Database db = new Lines.Database(fromDatabase, toDatabase);

            Lines.Repository repo = LinesDataModel.connect(db);
            return new Application(repo);
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
