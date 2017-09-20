/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.testng.Assert.fail;

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

        String [] input = {"5 5", initialState, instructions};
        String [] expectedReport = { expectedState };

        checkSimulation(input, expectedReport);
    }

    private void checkSimulation(String[] input, String[] expectedReport) {
        final String NEWLINE = "\n";
        StringBuilder inputBuilder = new StringBuilder();
        for (String line : input) {
            inputBuilder.append(line).append(NEWLINE);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBuilder.toString().getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);


        try {
            TestableCore.runTest(inputStream, out);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        String [] actualReport = new String(baos.toString()).split(NEWLINE);

        Assert.assertEquals(actualReport, expectedReport);
    }

    @Test
    public void testTwoRovers () {
        String [] simulation = toArray("5 5", "2 0 N", "M", "1 0 E", "MM");
        String [] expectedReport = toArray("2 1 N", "3 0 E");

        checkSimulation(simulation, expectedReport);
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

    @Test
    public void testCollisionAvoided () {
        String [] simulation = toArray("5 5", "2 0 E", "M", "2 1 S", "M");
        String [] expectedReport = toArray("3 0 E", "2 0 S");

        checkCollision(simulation, expectedReport);

    }

    @Test
    public void testGridBoundaries () {
        // The first line of input is the upper-right coordinates of the plateau,
        // the lower-left coordinates are assumed to be 0,0.

        String [] simulation = toArray("5 5", "5 5 E", "");
        String [] expectedReport = toArray("5 5 E");

        checkCollision(simulation, expectedReport);
    }

    private void checkCollision(String[] simulation, String[] expectedReport) {
        checkSimulation(simulation, expectedReport);
    }

    private String [] toArray(String ... items) {
        return items;
    }
}
