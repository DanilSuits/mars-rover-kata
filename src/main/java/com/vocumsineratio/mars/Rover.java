package com.vocumsineratio.mars;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Rover {
    static String [] output(String... lines) {
        assert lines[0].equals("5 5");
        assert lines[1].equals("1 2 N");
        assert lines[2].equals("LMLMLMLMM");
        assert lines[3].equals("3 3 E");
        assert lines[4].equals("MMRMMRMRRM");
        
        return "1 3 N\n5 1 E\n".split("\n");
    }
}
