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
            Plateau extends Domain.Plateau<Plateau, Position> & PlateauView<Position>,
            Report extends Domain.Report<Report, Position>,
            Simulation extends Domain.Simulation<Position, Plateau>>
    Report runSimulation(Plateau grid, Simulation simulationDefinition, Report reportBuilder) {

        for (Rover<Position> rover : simulationDefinition.rovers()) {
            Position position = rover.position();
            grid = grid.roverArrived(position);
        }

        for (Rover<Position> rover : simulationDefinition.rovers()) {

            Position position = rover.position();
            final Iterable<Instruction<Position>> instructions = rover.program();

            grid = grid.roverLeft(position);
            Position startPosition = position;
            for (Instruction<Position> currentInstruction : instructions) {
                Position endPosition = currentInstruction.applyTo(startPosition);

                if (grid.isOccupied(endPosition)) {
                    break;
                }
                startPosition = endPosition;
            }
            Position finalState = startPosition;
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
