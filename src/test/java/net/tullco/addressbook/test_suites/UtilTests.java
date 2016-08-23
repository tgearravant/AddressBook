package net.tullco.addressbook.test_suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.tullco.addressbook.test_classes.SQLUtilTest;
import net.tullco.addressbook.test_classes.SystemUtilTest;

@RunWith(Suite.class)
@SuiteClasses({ SQLUtilTest.class, SystemUtilTest.class })
public class UtilTests {

}
