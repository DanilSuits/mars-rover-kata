/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Input {
    public final Plateau plateau;
    public final List<Rover> rovers;

    Input(Plateau plateau, List<Rover> rovers) {
        this.plateau = plateau;
        this.rovers = rovers;
    }

    enum Heading {
        N, E, W, S
    }

    static class Coordinate {
        public final int X;
        public final int Y;

        Coordinate(int x, int y) {
            X = x;
            Y = y;
        }
    }

    static class Plateau {
        public final Coordinate upperRight;
        public final Coordinate lowerLeft = new Coordinate(0, 0);

        Plateau(Coordinate upperRight) {
            this.upperRight = upperRight;
        }
    }

    enum Instruction {
        M, L, R
    }

    static class Position {
        public final Coordinate coordinate;
        public final Heading heading;

        Position(Coordinate coordinate, Heading heading) {
            this.coordinate = coordinate;
            this.heading = heading;
        }
    }

    static class Rover {
        public final Position position;
        public final List<Instruction> instructions;

        Rover(Position position, List<Instruction> instructions) {
            this.position = position;
            this.instructions = instructions;
        }
    }
}
