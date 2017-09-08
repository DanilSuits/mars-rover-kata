/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import org.testng.Assert;
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
}
