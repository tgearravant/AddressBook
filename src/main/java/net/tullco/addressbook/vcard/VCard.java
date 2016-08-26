package net.tullco.addressbook.vcard;

import java.util.ArrayList;

import ezvcard.Ezvcard;
import ezvcard.VCardVersion;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Kind;
import ezvcard.property.StructuredName;
import ezvcard.property.Uid;
import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.phone_number.PhoneNumber;
import net.tullco.addressbook.utils.Path;

public class VCard {
	public ezvcard.VCard ezVCard;
	public Contact contact;
	public ArrayList<net.tullco.addressbook.address.Address> addresses = new ArrayList<net.tullco.addressbook.address.Address>();
	public VCard(Contact c){
		ezVCard = new ezvcard.VCard();
		ezVCard.setKind(Kind.individual());
		ezVCard.addLanguage("en-US");
		this.contact = c;
		StructuredName n = new StructuredName();
		if(c.firstName()!=null)
			n.setFamily(c.lastName());
		if(c.lastName()!=null)
			n.setGiven(c.firstName());
		ezVCard.setStructuredName(n);
		if(c.nickname()!=null)
			ezVCard.setNickname(c.nickname());
		if(c.email()!=null)
			ezVCard.addEmail(c.email(), EmailType.HOME);
		if(c.birthdate()!=null){
			Birthday b = new Birthday(c.birthdate());
			ezVCard.setBirthday(b);
		}
		for(net.tullco.addressbook.address.Address a : c.addresses()){
			if(!a.active())
				continue;
			Address vca = new Address();
			vca.setCountry(a.country());
			if(a.street()!=null)
				vca.setStreetAddress(a.street());
			if(a.state()!=null)
				vca.setRegion(a.state());
			if(a.city()!=null)
				vca.setLocality(a.city());
			if(a.zipCode()!=null)
				vca.setPostalCode(a.zipCode());
			if(a.apartment()!=null)
				vca.setExtendedAddress(a.apartment());
			vca.getTypes().add(AddressType.HOME);
			ezVCard.addAddress(vca);
		}
		for(PhoneNumber pn : c.phoneNumbers()){
			if(pn.getType().equals("work"))
				ezVCard.addTelephoneNumber(pn.getNumber(), TelephoneType.WORK);
			else if(pn.getType().equals("mobile"))
				ezVCard.addTelephoneNumber(pn.getNumber(), TelephoneType.CELL);
			else if(pn.getType().equals("home"))
				ezVCard.addTelephoneNumber(pn.getNumber(), TelephoneType.HOME);
			else
				ezVCard.addTelephoneNumber(pn.getNumber(), TelephoneType.HOME);
		}
		Uid uid = new Uid(Integer.toString(c.fullName().hashCode()));
		ezVCard.setUid(uid);
	}
	public String getVCardAsString(){
		return Ezvcard.write(this.ezVCard).version(VCardVersion.V3_0).go();
	}
	@Override
	public String toString(){
		return this.getVCardAsString();
	}
}
