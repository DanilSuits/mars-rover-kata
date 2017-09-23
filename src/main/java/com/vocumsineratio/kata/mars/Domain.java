/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Domain {
    interface Input<R extends Position, P extends PlateauView<R> & Plateau<P, R>> {
        P plateau();

        Iterable<? extends Rover<R>> squad();
    }

    interface PlateauView<R extends Position> {
        boolean isOccupied(R rover);
    }

    interface Plateau<P extends Plateau<P, R>, R extends Position> {
        P roverArrived(R rover);

        P roverLeft(R rover);
    }

    interface Rover<P extends Position> {
        P position();

        Iterable<Letter> program();
    }

    interface Position<P extends Position<P>> {
        P left();

        P right();

        P move();
    }

    enum Letter {
        M, L, R
    }

    interface Instruction<P extends Position> {
        P applyTo(P currentState);
    }

    interface Heading<H extends Heading> {
        H left();

        H right();
    }

    interface Output<O extends Output<O, P>, P extends Position> {
        O add(P position);
    }

    static
    <Position extends Domain.Position<Position>,
    Plateau extends Domain.Plateau<Plateau, Position> & PlateauView<Position>,
    Output extends Domain.Output<Output, Position>,
    Input extends Domain.Input<Position, Plateau>>
    Output runSimulation(Input input, Output output) {
        Plateau plateau = input.plateau();
        
        for (Rover<Position> rover : input.squad()) {
            Position position = rover.position();
            plateau = plateau.roverArrived(position);
        }

        Map<Letter, Instruction<Position>> instructionSet = getInstructionSet();

        for (Rover<Position> rover : input.squad()) {

            Position position = rover.position();
            final Iterable<Letter> instructions = rover.program();

            plateau = plateau.roverLeft(position);
            Position startPosition = position;
            for (Letter letter : instructions) {

                Position endPosition = instructionSet.get(letter).applyTo(startPosition);

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

    private static
    <Position extends Domain.Position<Position>>
    Map<Letter, Instruction<Position>> getInstructionSet() {
        Map<Letter, Instruction<Position>> instructionSet = new EnumMap<>(Letter.class);
        instructionSet.put(Letter.M, Position::move);
        instructionSet.put(Letter.L, Position::left);
        instructionSet.put(Letter.R, Position::right);
        return instructionSet;
    }
}
