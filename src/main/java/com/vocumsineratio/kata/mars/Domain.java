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

    interface Rover<R extends Rover> {
        R left();

        R right();

        R move();
    }

    interface PlateauView<R extends Rover> {
        boolean isOccupied(R rover);
    }

    interface Plateau<P extends Plateau<P, R>, R extends Rover<R>> {
        P roverArrived(R rover);

        P roverLeft(R rover);
    }

    interface Report<T extends Report<T, R>, R extends Rover<R>> {
        T add(R rover);
    }

    interface Instruction<R extends Rover<R>> {
        R applyTo(R currentState);
    }

    static <Rover extends Domain.Rover<Rover>,
            Program extends Iterable<Instruction<Rover>>,
            Plateau extends PlateauView<Rover>>
    Rover runProgram(Plateau grid, Rover currentRover, Program program) {
        for (Instruction<Rover> currentInstruction : program) {
            Rover roverAfterInstruction = currentInstruction.applyTo(currentRover);

            if (grid.isOccupied(roverAfterInstruction)) {
                break;
            }
            currentRover = roverAfterInstruction;
        }
        return currentRover;
    }

    static <Rover extends Domain.Rover<Rover>,
            Plateau extends Domain.Plateau<Plateau, Rover> & PlateauView<Rover>,
            Report extends Domain.Report<Report, Rover>,
            Simulation extends Domain.Simulation<Rover, Plateau>>
    Report runSimulation(Plateau grid, Simulation simulationDefinition, Report reportBuilder) {

        for (Entry<Rover> entry : simulationDefinition.entries()) {
            Rover state = entry.rover();
            grid = grid.roverArrived(state);
        }

        for (Entry<Rover> entry : simulationDefinition.entries()) {

            Rover currentRover = entry.rover();
            final Iterable<Instruction<Rover>> instructions = entry.program();

            grid = grid.roverLeft(currentRover);
            Rover finalState = Domain.runProgram(grid, currentRover, instructions);
            grid = grid.roverArrived(finalState);

            reportBuilder = reportBuilder.add(finalState);
        }

        return reportBuilder;
    }

    // TODO: Name?
    interface Entry<R extends Rover<R>> {
        R rover();

        Iterable<Instruction<R>> program();
    }

    interface Simulation<R extends Rover<R>, P extends PlateauView<R> & Plateau<P, R>> {
        P plateau();

        Iterable<? extends Entry<R>> entries();
    }
}
