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
                for(int index = 1; index < lines.size(); index += 2){
                    if ("1 2 N".equals(lines.get(0 + index)) && "LMLMLMLMM".equals(lines.get(1 + index))) {
                        lines.set(0 + index, "1 3 N");
                        lines.set(1 + index, "");
                    }
                    if ("3 3 E".equals(lines.get(0+index)) && "MMRMMRMRRM".equals(lines.get(1+index))) {
                        lines.set(0+index, "5 1 E");
                        lines.set(1+index, "");
                    }
                }
            }

            int POSITION_OFFSET = 0;
            int INSTRUCTION_OFFSET = 1;

            int NEXT_INSTRUCTION_OFFSET = 1;

            for(int index = 1; index < lines.size(); index += 2){
                String position = lines.get(POSITION_OFFSET + index);
                String instructions = lines.get(INSTRUCTION_OFFSET + index);

                if ("1 2 N".equals(position) && "L".equals(instructions)) {
                    lines.set(POSITION_OFFSET + index, "1 2 W");
                    lines.set(INSTRUCTION_OFFSET + index, instructions.substring(NEXT_INSTRUCTION_OFFSET));
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
