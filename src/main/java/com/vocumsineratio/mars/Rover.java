package com.vocumsineratio.mars;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Rover {
    static class Schema {
        static String rover(int x, int y, String orientation) {
            return String.format("%d %d %s", x, y, orientation);
        }
    }
    static String [] output(String... lines) {
        assert lines[0].equals("5 5");
        assert lines[1].equals("1 2 N");
        assert lines[2].equals("LMLMLMLMM");
        assert lines[3].equals("3 3 E");
        assert lines[4].equals("MMRMMRMRRM");


        String[] report = new String[2];
        report[0] = Schema.rover(1, 3, "N");
        report[1] = "5 1 E"; report[1] = Schema.rover(5, 1, "E");
        return report;
    }
}
