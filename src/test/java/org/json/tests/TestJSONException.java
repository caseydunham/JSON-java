package org.json.tests;

import org.json.JSONException;

import junit.framework.TestCase;

public class TestJSONException extends TestCase {

	JSONException jsonexception;

	public void testConstructor_String() {
		jsonexception = new JSONException("test..test..");
		assertEquals("test..test..", jsonexception.getMessage());
	}

	public void testConstructor_Exception() {
		Exception e = new Exception();
		jsonexception = new JSONException(e);
		assertEquals(e, jsonexception.getCause());
	}
}