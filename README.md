# perceptualdiff
A Java port of the perceptualdiff image comparison (pdiff.sourceforge.net)

This port of the perceptualdiff software is intended to be used as a library, hence the lack of command line processing in the main program class.  Refer to the JUnit tests for illustration of how to invoke the library.

This port was validated against the original C++ perceptualdiff program using three test cases: fish.png, alpha.png and Aqsis_vase.png from the test folder of the C++ project.  When run on Java 8 and Ubuntu Linux 14.04, the Java program gave the same result as the C++ program when using the default parameter set.  These test cases and parameters are also used by the JUnit tests.
