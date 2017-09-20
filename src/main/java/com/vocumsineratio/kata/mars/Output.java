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
class Output {
    public final List<Rover> rovers;

    Output(List<Rover> rovers) {
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

    static class Rover {
        public final Coordinate coordinate;
        public final Heading heading;

        Rover(Coordinate coordinate, Heading heading) {
            this.coordinate = coordinate;
            this.heading = heading;
        }
    }
}
