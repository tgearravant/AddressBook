package net.tullco.addressbook.test_suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.tullco.addressbook.test_classes.APIAuthTest;
import net.tullco.addressbook.test_classes.APIDataSyncTest;

@RunWith(Suite.class)
@SuiteClasses({ APIDataSyncTest.class, APIAuthTest.class})
public class APITests {

}
