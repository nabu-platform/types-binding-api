package be.nabu.libs.types.binding;

import junit.framework.TestCase;

public class TestBindingUtils extends TestCase {
	public void testCamelcase() {
		// in a previous bug, this returned an empty string
		assertEquals("c", BindingUtils.camelCaseCharacter("c", '-'));
	}
}
