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

    interface Instruction<P extends Position> {
        P applyTo(P currentState);
    }

    interface Rover<P extends Position> {
        P position();

        Iterable<Instruction<P>> program();
    }

    enum Letter {
        M, L, R
    }

    interface Input<R extends Position, P extends PlateauView<R> & Plateau<P, R>> {
        P plateau();

        Iterable<? extends Rover<R>> rovers();
    }

    interface Output<O extends Output<O, R>, R extends Position> {
        O add(R rover);
    }

    static
    <Position extends Domain.Position,
    Plateau extends Domain.Plateau<Plateau, Position> & PlateauView<Position>,
    Output extends Domain.Output<Output, Position>,
    Input extends Domain.Input<Position, Plateau>>
    Output runSimulation(Input input, Output output) {
        Plateau plateau = input.plateau();
        
        for (Rover<Position> rover : input.rovers()) {
            Position position = rover.position();
            plateau = plateau.roverArrived(position);
        }

        for (Rover<Position> rover : input.rovers()) {

            Position position = rover.position();
            final Iterable<Instruction<Position>> instructions = rover.program();

            plateau = plateau.roverLeft(position);
            Position startPosition = position;
            for (Instruction<Position> currentInstruction : instructions) {
                Position endPosition = currentInstruction.applyTo(startPosition);

                if (plateau.isOccupied(endPosition)) {
                    break;
                }
                startPosition = endPosition;
            }
            Position finalState = startPosition;
            plateau = plateau.roverArrived(finalState);

            output = output.add(finalState);
        }

        return output;
    }
}
