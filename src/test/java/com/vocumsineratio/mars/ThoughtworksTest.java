package com.vocumsineratio.mars;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ThoughtworksTest {
    @Test
    public void interviewExample() {
        assertArrayEquals(
                expected(
                        "1 3 N",
                        "5 1 E"
                ),
                Rover.output(
                        "5 5",
                        "1 2 N",
                        "LMLMLMLMM",
                        "3 3 E",
                        "MMRMMRMRRM"
                )
        );
    }

    String[] expected(String... lines) {
        return lines;
    }
}
