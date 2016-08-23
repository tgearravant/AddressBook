package net.tullco.addressbook.test_suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.tullco.addressbook.test_classes.GetPageLoadTest;
import net.tullco.addressbook.test_classes.PostPageTest;

@RunWith(Suite.class)
@SuiteClasses({ GetPageLoadTest.class, PostPageTest.class })
public class IntegrationTests {

}
