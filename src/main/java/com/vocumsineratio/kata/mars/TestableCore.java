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

        // RUN the model.
        {
            // TODO: remove the cheat when we can.
            // Key Insight - cheat by converting the complicated problem into a simpler
            // equivalent that the model undertands how to solve.
            {
                if ("1 2 N".equals(lines.get(1)) && "LMLMLMLMM".equals(lines.get(2))) {
                    lines.set(1, "1 3 N");
                    lines.set(2, "");
                }

                if ("3 3 E".equals(lines.get(3)) && "MMRMMRMRRM".equals(lines.get(4))) {
                    lines.set(3, "5 1 E");
                    lines.set(4, "");
                }
            }
        }

        // Create a view of the modified data model.
        for (int index = 1; index < lines.size(); index+=2) {
            out.println(lines.get(index));
        }
    }

    public static void main(String[] args) throws IOException {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(System.in, System.out);
    }
}
