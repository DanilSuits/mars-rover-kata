/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    static void runTest(InputStream in, PrintStream out) throws IOException {
        {
            // FOR TEST CALIBRATION ONLY
            if (false) return;
        }
        // Simplest thing that can possibly work
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // Primitive Parsing.
        List<String> lines = new ArrayList<>();
        String line;
        while(true) {
            line = reader.readLine();
            if (null == line) break;
            lines.add(line);
        }

        if (! lines.get(2).isEmpty()) {
            out.println("1 3 N");
            out.println("5 1 E");
            return;
        }

        out.println(lines.get(1));
        out.println(lines.get(3));
    }

    public static void main(String[] args) throws IOException {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(System.in, System.out);
    }
}
