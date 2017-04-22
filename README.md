# perceptualdiff
A Java port of the perceptualdiff image comparison (https://github.com/myint/perceptualdiff)

This port of the perceptualdiff software is intended to be used as a library, hence the lack of command line processing in the main program class.  Refer to the JUnit tests for illustration of how to invoke the library.

This port was validated against the C++ perceptualdiff program using three test cases: fish.png, alpha.png and Aqsis_vase.png from the test folder of the C++ project.  When run on Java 1.8.0_51 and Ubuntu Linux 14.04, the Java program gave the same results as the C++ program when using the default parameter set.  These test cases and parameters are used by the JUnit tests.

The down-sampling and scaling functions of the C++ program have not been ported to the Java.

Note that MetricImpl imports "net.jafama.FastMath" in order to access the FastMath methods as profiling showed that the performance bottleneck is the math processing.  It is easy to revert to the JDK math methods by removing this import and editting the class to remove the "Fast" prefix on the relevant method calls.
