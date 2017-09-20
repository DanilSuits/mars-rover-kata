/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    public static void main(String[] args) throws IOException {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(System.in, System.out);
    }

    static void runTest(InputStream in, PrintStream out) throws IOException {

        Input input = Console.readFrom(in);

        Output output = Model.runSimulation(input);

        Console.writeTo(out, output);
    }


}
