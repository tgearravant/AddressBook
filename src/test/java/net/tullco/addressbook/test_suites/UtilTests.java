package net.tullco.addressbook.test_suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.tullco.addressbook.test_classes.SQLUtilTest;

@RunWith(Suite.class)
@SuiteClasses({ SQLUtilTest.class })
public class UtilTests {

}
