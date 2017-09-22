/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Domain {
    interface Heading<H extends Heading> {
        H left();

        H right();
    }

    interface Position<R extends Position> {
        R left();

        R right();

        R move();
    }

    interface PlateauView<R extends Position> {
        boolean isOccupied(R rover);
    }

    interface Plateau<P extends Plateau<P, R>, R extends Position> {
        P roverArrived(R rover);

        P roverLeft(R rover);
    }

    interface Report<T extends Report<T, R>, R extends Position> {
        T add(R rover);
    }

    interface Instruction<R extends Position> {
        R applyTo(R currentState);
    }

    static <Position extends Domain.Position,
            Program extends Iterable<Instruction<Position>>,
            Plateau extends PlateauView<Position>>
    Position runProgram(Plateau grid, Position startPosition, Program program) {
        for (Instruction<Position> currentInstruction : program) {
            Position endPosition = currentInstruction.applyTo(startPosition);

            if (grid.isOccupied(endPosition)) {
                break;
            }
            startPosition = endPosition;
        }
        return startPosition;
    }

    static <Position extends Domain.Position,
            Plateau extends Domain.Plateau<Plateau, Position> & PlateauView<Position>,
            Report extends Domain.Report<Report, Position>,
            Simulation extends Domain.Simulation<Position, Plateau>>
    Report runSimulation(Plateau grid, Simulation simulationDefinition, Report reportBuilder) {

        for (Rover<Position> entry : simulationDefinition.rovers()) {
            Position position = entry.position();
            grid = grid.roverArrived(position);
        }

        for (Rover<Position> entry : simulationDefinition.rovers()) {

            Position position = entry.position();
            final Iterable<Instruction<Position>> instructions = entry.program();

            grid = grid.roverLeft(position);
            Position finalState = Domain.runProgram(grid, position, instructions);
            grid = grid.roverArrived(finalState);

            reportBuilder = reportBuilder.add(finalState);
        }

        return reportBuilder;
    }

    // TODO: Name?
    interface Rover<P extends Position> {
        P position();

        Iterable<Instruction<P>> program();
    }

    interface Simulation<R extends Position, P extends PlateauView<R> & Plateau<P, R>> {
        P plateau();

        Iterable<? extends Rover<R>> rovers();
    }
}
