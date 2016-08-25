package net.tullco.addressbook.test_suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.tullco.addressbook.test_classes.AddressTest;
import net.tullco.addressbook.test_classes.ContactTest;
import net.tullco.addressbook.test_classes.PhoneNumberTest;
import net.tullco.addressbook.test_classes.UserTest;

@RunWith(Suite.class)
@SuiteClasses({ ContactTest.class, AddressTest.class, UserTest.class, PhoneNumberTest.class })
public class ModelTests {

}
