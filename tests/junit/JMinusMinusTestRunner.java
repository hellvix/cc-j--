// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package junit;

import junit.framework.Test;
import junit.framework.TestSuite;
import pass.*;

/**
 * JUnit test suite for running the j-- programs in tests/pass.
 */

public class JMinusMinusTestRunner {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(HelloWorldTest.class);
        suite.addTestSuite(FactorialTest.class);
        suite.addTestSuite(GCDTest.class);
        suite.addTestSuite(SeriesTest.class);
        suite.addTestSuite(ClassesTest.class);
        suite.addTestSuite(DivisionTest.class);
        suite.addTestSuite(RemainderTest.class);
        suite.addTestSuite(SignedShiftLeftTest.class);
        suite.addTestSuite(SignedShiftRightTest.class);
        suite.addTestSuite(UnaryComplementTest.class);
        suite.addTestSuite(BitwiseOrTest.class);
        suite.addTestSuite(UnaryPlusTest.class);
        suite.addTestSuite(USignedRightShiftTest.class);
        suite.addTestSuite(BitwiseAndTest.class);
        suite.addTestSuite(ExclusiveOrTest.class);
        suite.addTestSuite(CommentTest.class);
        suite.addTestSuite(MinusAssignTest.class);
        suite.addTestSuite(StarAssignTest.class);
        suite.addTestSuite(SlashAssignTest.class);
        suite.addTestSuite(ModuloAssignTest.class);
        suite.addTestSuite(TryTest.class);
        suite.addTestSuite(InitBlockTest.class);
        suite.addTestSuite(ConditionalExpressTest.class);
        suite.addTestSuite(InterfacesTest.class);
        suite.addTestSuite(DoublesTest.class);
        return suite;
    }

    /**
     * Runs the test suite using the textual runner.
     */

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
