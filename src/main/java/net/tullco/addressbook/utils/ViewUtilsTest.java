package net.tullco.addressbook.utils;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class ViewUtilsTest {
	@Test
	public void testPostBodyDecoder() {
		String body = "jedi=returning&sith=revenging&hope=new";
		Map<String,String> values = ViewUtils.postBodyDecoder(body);
		assertEquals("returning",values.get("jedi"));
		assertEquals("revenging",values.get("sith"));
		assertEquals("new",values.get("hope"));
	}

}
