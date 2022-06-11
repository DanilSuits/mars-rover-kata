package com.vocumsineratio.calibration.junit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test is used to ensure that JUnit is wired up
 * correctly, that it can find this class, identify the
 * test within it, and run that test, find the test subject
 * and report the result.
 *
 * @author Danil Suits (danil@vast.com)
 */
public class JUnitCalibrationTest {
    static class SUT {
        static <T> T testResult(T pass, T fail) {
            // Switch this from pass to fail to ensure that
            // test failures are being reported as intended.
            return pass;
        }
    }
    // The motivation for this enum is simply to get
    // nice error messages if the production component
    // doesn't return the answer that we want.
    enum Result { PASS, FAIL }

    @Test
    public void the_test_subject_should_return_the_first_argument() {
        assertEquals(
                Result.PASS,
                SUT.testResult(
                        Result.PASS,
                        Result.FAIL
                )
        );
    }
}
