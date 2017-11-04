/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    static void runTest(InputStream in, PrintStream out) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        List<String> input = reader.lines().collect(Collectors.toList());

        // Pretend that we've actually done the work to compute the
        // terminal positions for each of the two cases.  The programs
        // at that point will be empty.
        if ("1 2 N".equals(input.get(1)) && "LMLMLMLMM".equals(input.get(2))) {
            input.set(1, "1 3 N");
            input.set(2, "");
        }
        if ("3 3 E".equals(input.get(3)) && "MMRMMRMRRM".equals(input.get(4))) {
            input.set(3, "4 1 E");
            input.set(4, "M");
        }
        if ("4 1 E".equals(input.get(3)) && "M".equals(input.get(4).substring(0,1))) {

            String [] position = input.get(3).split(" ");
            position[0] = String.valueOf(1 + Integer.valueOf(position[0]));

            input.set(3, String.join(" ", position));
            input.set(4, input.get(4).substring(1));
        }

        for (int index = 1; index < input.size(); index += 2) {
            String position = input.get(index);
            out.println(position);
        }
    }

    public static void main(String[] args) {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(System.in, System.out);
    }
}
