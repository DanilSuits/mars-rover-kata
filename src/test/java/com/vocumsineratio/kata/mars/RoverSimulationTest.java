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

        checkSimulation(initialState, trivialInstructions, initialState);
    }

    @Test
    public void testLeftRotation () {
        String initialState = "1 2 N";
        String trivialInstructions = "L";

        checkSimulation(initialState, trivialInstructions, "1 2 W");

    }

    @Test
    public void testRightRotation () {
        String initialState = "1 2 N";
        String trivialInstructions = "R";

        checkSimulation(initialState, trivialInstructions, "1 2 E");

    }

    @DataProvider(name = "invariantRotations")
    Object[][] invariantRotations() {
        Object [][] programs = {
                { "" }
                , {"LLLL"}
                , {"RL"}
                , {"LLRRLR"}
                , {"LMLLML"}
                , {"RMLLMR"}
                , {"MRRMLL"}
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

        checkSimulation(initialState, instructions, initialState);

    }

    @Test
    public void testMove() {
        String initialState = "1 2 W";
        String instructions = "M";
        String expectedState = "0 2 W";

        checkSimulation(initialState, instructions, expectedState);

    }

    @Test
    public void testMoveEast() {
        String initialState = "1 2 E";
        String instructions = "M";
        String expectedState = "2 2 E";

        checkSimulation(initialState, instructions, expectedState);
    }

    private void checkSimulation(String initialState, String instructions, String expectedState) {
        String finalState = TestableCore.simulateRover(initialState, instructions);
        Assert.assertEquals(finalState, expectedState);
    }

    @Test
    public void testTwoRovers () {
        String [] simulation = toArray("5 5", "2 0 N", "M", "1 0 E", "MM");
        String [] expectedReport = toArray("2 1 N", "3 0 E");

        String [] actualReport = TestableCore.runSimulation(simulation);
        Assert.assertEquals(actualReport, expectedReport);
    }

    @Test
    public void testCollision () {
        String [] simulation = toArray("5 5", "1 0 E", "MM", "2 0 N", "M");
        String [] expectedReport = toArray("1 0 E", "2 1 N");

        checkCollision(simulation, expectedReport);
    }

    @Test
    public void testCollisionAfterMove () {
        String [] simulation = toArray("5 5", "1 0 E", "M", "2 1 S", "M");
        String [] expectedReport = toArray("2 0 E", "2 1 S");

        checkCollision(simulation, expectedReport);
    }

    private void checkCollision(String[] simulation, String[] expectedReport) {
        String [] actualReport = TestableCore.simulateCollision(simulation);
        Assert.assertEquals(actualReport, expectedReport);
    }

    private String [] toArray(String ... items) {
        return items;
    }
}
