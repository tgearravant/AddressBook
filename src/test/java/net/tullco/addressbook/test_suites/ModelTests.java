package net.tullco.addressbook.test_suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.tullco.addressbook.test_classes.AddressTest;
import net.tullco.addressbook.test_classes.ContactTest;

@RunWith(Suite.class)
@SuiteClasses({ ContactTest.class, AddressTest.class })
public class ModelTests {

}
