/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class RoverSimulationTest {
    @Test
    public void testTrivalInstructions () {
        String initialState = "1 2 N";
        String trivialInstructions = "";

        String finalState = TestableCore.simulateRover(initialState, trivialInstructions);
        Assert.assertEquals(finalState, initialState);
    }

    @Test
    public void testLeftRotation () {
        String initialState = "1 2 N";
        String trivialInstructions = "L";

        String finalState = TestableCore.simulateRover(initialState, trivialInstructions);
        Assert.assertEquals(finalState, "1 2 W");

    }

    @Test
    public void testRightRotation () {
        String initialState = "1 2 N";
        String trivialInstructions = "R";

        String finalState = TestableCore.simulateRover(initialState, trivialInstructions);
        Assert.assertEquals(finalState, "1 2 E");

    }

    @DataProvider(name = "invariantRotations")
    Object[][] invariantRotations() {
        Object [][] programs = {
                { "" }
                , {"LLLL"}
                , {"RL"}
                , {"LLRRLR"}
        } ;

        return programs;
    }

    @DataProvider(name = "invariantPrograms")
    Object [][] invariantPrograms() {
        return invariantRotations();
    }

    @Test(dataProvider = "invariantPrograms")
    public void testInvariantPrograms(String instructions) {
        String initialState = "1 2 N";

        String finalState = TestableCore.simulateRover(initialState, instructions);
        Assert.assertEquals(finalState, initialState);

    }

    @Test
    public void testMove() {
        String initialState = "1 2 W";
        String instructions = "M";
        String expectedState = "0 2 N";

        String finalState = TestableCore.simulateRover(initialState, instructions);
        Assert.assertEquals(finalState, expectedState);

    }
}
