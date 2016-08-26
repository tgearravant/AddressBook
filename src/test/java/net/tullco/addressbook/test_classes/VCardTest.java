package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;
import net.tullco.addressbook.vcard.VCard;

public class VCardTest {

	private static String VCARD_EXAMPLE="BEGIN:VCARD\nVERSION:3.0\nPRODID:ez-vcard 0.9.10\n"
			+ "N:Skywalker;Luke;;;\nNICKNAME:Lu\nEMAIL;TYPE=home:luke@lightside.force\nBDAY:1982-12-12\n"
			+ "TEL;TYPE=cell:8887774444\nTEL;TYPE=cell:1234567890\nUID:-955370293\nEND:VCARD\n";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SystemUtils.setTesting(true);
		SQLUtils.runMigrations();
		SQLUtils.truncateAllTables();
		TestUtils.seedTestDB();
	}

	@Test
	public void testVCardConstructor() {
		VCard vc = new VCard(Contact.contactLoader(2));
		assertEquals("Lu",vc.ezVCard.getNickname().getValues().get(0));
		assertEquals("Luke",vc.ezVCard.getStructuredName().getGiven());
		assertEquals("Skywalker",vc.ezVCard.getStructuredName().getFamily());
		assertEquals("8887774444",vc.ezVCard.getTelephoneNumbers().get(0).getText());
		assertEquals("1234567890",vc.ezVCard.getTelephoneNumbers().get(1).getText());
		assertEquals("cell",vc.ezVCard.getTelephoneNumbers().get(1).getTypes().get(0).getValue());
		assertEquals(0,vc.ezVCard.getAddresses().size());
		vc = new VCard(Contact.contactLoader(3));
		assertEquals("Hoth",vc.ezVCard.getAddresses().get(0).getRegion());
	}
	@Test
	public void vCardStringOutput() {
		VCard vc = new VCard(Contact.contactLoader(2));
		System.out.println(vc.toString());
		assertEquals(VCARD_EXAMPLE,vc.toString().replace("\r\n", "\n"));
		
	}

}
