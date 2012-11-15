package org.json.tests;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import junit.framework.TestCase;

public class TestJSONArray extends TestCase {

	public class TestObject {
	}

	public static void testJsonArray_IntWithLeadingZeros() {
		try {
			String s = "[001122334455]";
			JSONArray jsa = new JSONArray(s);
			assertEquals("[1122334455]", jsa.toString());
		} catch (Exception ex) {
			fail(ex.toString());
		}
	}

	public static void testJsonArray_ScientificNotation() {
		try {
			String s = "[666e666]";
			JSONArray jsa = new JSONArray(s);
			assertEquals("[\"666e666\"]", jsa.toString());
		} catch (Exception ex) {
			fail(ex.toString());
		}
	}

	public static void testJsonArray_DoubleWithLeadingAndTrailingZeros() {
		try {
			String s = "[00.10]";
			JSONArray jsa = new JSONArray(s);
			assertEquals("[0.1]", jsa.toString());
		} catch (Exception ex) {
			fail(ex.toString());
		}
	}

	public void testConstructor_MissingValue() {
		try {
			new JSONArray("[\n\r\n\r}");
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("Missing value at 5 [character 0 line 4]", ex.getMessage());
		}
	}

	public void testConstructor_Nan() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(Double.NaN);
			jsa.toString();
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
		}
	}

	public void testConstructor_NegativeInfinity() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(Double.NEGATIVE_INFINITY);
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
		}
	}

	public void testConstructor_PositiveInfinity() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(Double.POSITIVE_INFINITY);
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
		}
	}

	public void testPut_PositiveInfinity() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(Double.POSITIVE_INFINITY);
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
		}
	}

	public void testGetDouble_EmptyArray() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.getDouble(0);
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSONArray[0] not found.", ex.getMessage());
		}
	}

	public void testGet_NegativeIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.get(-1);
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSONArray[-1] not found.", ex.getMessage());
		}
	}

	public void testPut_Nan() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(Double.NaN);
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
		}
	}

	public void testConstructor_Object() {
		try {
			new JSONArray(new Object());
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals( "JSONArray initial value should be a string or collection or array.", ex.getMessage());
		}
	}

	public void testConstructor_BadJson() {
		try {
			String s = "[)";
			new JSONArray(s);
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("Expected a ',' or ']' at 3 [character 4 line 1]", ex.getMessage());
		}
	}

	public void testToString_Locations() {
		try {
			String s = " [\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\"]";
			JSONArray jsa = new JSONArray(s);
			assertEquals( "[\"San Francisco\",\"New York\",\"Seoul\",\"London\",\"Seattle\",\"Shanghai\"]", jsa.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testConstructor_Collection() {
		Collection<String> stringCol = new Stack<String>();
		stringCol.add("string1");
		stringCol.add("string2");
		stringCol.add("string3");
		stringCol.add("string4");
		JSONArray jsa = new JSONArray(stringCol);
		assertEquals("[\"string1\",\"string2\",\"string3\",\"string4\"]", jsa.toString());
	}

	public void testConstructor_NullCollection() {
		Collection<String> stringCol = null;
		JSONArray jsa = new JSONArray(stringCol);
		assertEquals("[]", jsa.toString());
	}

	public void testConstructor_StringArray() {
		try {
			JSONArray jsa = new JSONArray(new String[]
					{
							"string1", "string2"
					});
			assertEquals("[\"string1\",\"string2\"]", jsa.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testOpt() {
		try {
			JSONArray jsa = new JSONArray(new String[]
					{
							"string1", "string2"
					});
			assertEquals("string1", jsa.opt(0));
			assertEquals("string2", jsa.opt(1));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testToString_Exception() {
		class BadJsonString implements JSONString {

			@Override
			public String toJSONString()
			{
				String[] arString = new String[]
						{
								"abc"
						};
				return arString[1];
			}
		}

		JSONArray jsa = new JSONArray();
		jsa.put(new BadJsonString());
		assertEquals(null, jsa.toString());
	}

	public void testToString_Indents() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.put(new JSONObject().put("abc", "123"));
			jsa.put("abc");
			jsa.put(new JSONArray().put(new JSONArray()).put(
					new JSONArray().put("123").put("abc")));
			assertEquals(
					"[\n    \"123\",\n    {\"abc\": \"123\"},\n    \"abc\",\n    [\n        [],\n        [\n            \"123\",\n            \"abc\"\n        ]\n    ]\n]",
					jsa.toString(4));
			assertEquals("[\"123\"]", new JSONArray().put("123").toString(4));
			assertEquals("[]", new JSONArray().toString(4));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGet_InvalidIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.get(1);
		} catch (JSONException ex) {
			assertEquals("JSONArray[1] not found.", ex.getMessage());
		}
	}

	public void testGet_ValidIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			assertEquals("123", jsa.get(0));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetBoolean() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("true");
			jsa.put("false");
			jsa.put(true);
			jsa.put(false);
			jsa.put("TRUE");
			jsa.put("FALSE");
			assertTrue(jsa.getBoolean(0));
			assertFalse(jsa.getBoolean(1));
			assertTrue(jsa.getBoolean(2));
			assertFalse(jsa.getBoolean(3));
			assertTrue(jsa.getBoolean(4));
			assertFalse(jsa.getBoolean(5));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetBoolean_NonBoolean() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.getBoolean(0);
		} catch (JSONException ex) {
			assertEquals("JSONArray[0] is not a boolean.", ex.getMessage());
		}
	}

	public void testOptBoolean() {
		JSONArray jsa = new JSONArray();
		jsa.put("true");
		jsa.put("false");
		jsa.put(true);
		jsa.put(false);
		jsa.put("TRUE");
		jsa.put("FALSE");
		jsa.put("grass");
		assertTrue(jsa.optBoolean(0));
		assertFalse(jsa.optBoolean(1));
		assertTrue(jsa.optBoolean(2));
		assertFalse(jsa.optBoolean(3));
		assertTrue(jsa.optBoolean(4));
		assertFalse(jsa.optBoolean(5));
		assertFalse(jsa.optBoolean(6));
	}

	public void testGetInt() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.put("-12");
			jsa.put(45);
			jsa.put(-98);
			assertEquals(123, jsa.getInt(0));
			assertEquals(-12, jsa.getInt(1));
			assertEquals(45, jsa.getInt(2));
			assertEquals(-98, jsa.getInt(3));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetInt_NonInteger() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("abc");
			jsa.getInt(0);
		} catch (JSONException ex) {
			assertEquals("JSONArray[0] is not a number.", ex.getMessage());
		}
	}

	public void testOptInt() {
		JSONArray jsa = new JSONArray();
		jsa.put("123");
		jsa.put("-12");
		jsa.put(45);
		jsa.put(-98);
		jsa.put("abc");
		assertEquals(123, jsa.optInt(0));
		assertEquals(-12, jsa.optInt(1));
		assertEquals(45, jsa.optInt(2));
		assertEquals(-98, jsa.optInt(3));
		assertEquals(0, jsa.optInt(4));
	}

	public void testGetDouble() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.put("-12");
			jsa.put(45);
			jsa.put(-98);
			jsa.put("123.5");
			jsa.put("-12.87");
			jsa.put(45.22);
			jsa.put(-98.18);
			assertEquals(123.0, jsa.getDouble(0));
			assertEquals(-12.0, jsa.getDouble(1));
			assertEquals(45.0, jsa.getDouble(2));
			assertEquals(-98.0, jsa.getDouble(3));
			assertEquals(123.5, jsa.getDouble(4));
			assertEquals(-12.87, jsa.getDouble(5));
			assertEquals(45.22, jsa.getDouble(6));
			assertEquals(-98.18, jsa.getDouble(7));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetDouble_NonDouble() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("abc");
			jsa.getDouble(0);
		} catch (JSONException ex) {
			assertEquals("JSONArray[0] is not a number.", ex.getMessage());
		}
	}

	public void testOptDouble() {
		JSONArray jsa = new JSONArray();
		jsa.put("123");
		jsa.put("-12");
		jsa.put(45);
		jsa.put(-98);
		jsa.put("123.5");
		jsa.put("-12.87");

		try {
			jsa.put(45.22);
			jsa.put(-98.18);
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}

		assertEquals(123.0, jsa.optDouble(0));
		assertEquals(-12.0, jsa.optDouble(1));
		assertEquals(45.0, jsa.optDouble(2));
		assertEquals(-98.0, jsa.optDouble(3));
		assertEquals(123.5, jsa.optDouble(4));
		assertEquals(-12.87, jsa.optDouble(5));
		assertEquals(45.22, jsa.optDouble(6));
		assertEquals(-98.18, jsa.optDouble(7));
		assertEquals(Double.NaN, jsa.optDouble(8));
	}

	public void testGetLong() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.put("-12");
			jsa.put(45L);
			jsa.put(-98L);
			assertEquals(123, jsa.getLong(0));
			assertEquals(-12, jsa.getLong(1));
			assertEquals(45, jsa.getLong(2));
			assertEquals(-98, jsa.getLong(3));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetLong_NonLong() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("abc");
			jsa.getLong(0);
		} catch (JSONException ex) {
			assertEquals("JSONArray[0] is not a number.", ex.getMessage());
		}
	}

	public void testOptLong() {
		JSONArray jsa = new JSONArray();
		jsa.put("123");
		jsa.put("-12");
		jsa.put(45L);
		jsa.put(-98L);
		assertEquals(123, jsa.optLong(0));
		assertEquals(-12, jsa.optLong(1));
		assertEquals(45, jsa.optLong(2));
		assertEquals(-98, jsa.optLong(3));
		assertEquals(0, jsa.optLong(8));
	}

	public void testGetString() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.put("-12");
			jsa.put("abc");
			jsa.put("123");
			assertEquals("123", jsa.getString(0));
			assertEquals("-12", jsa.getString(1));
			assertEquals("abc", jsa.getString(2));
			assertEquals("123", jsa.getString(3));
		} catch (JSONException ex)
		{
			fail(ex.getMessage());
		}
	}

	public void testGetString_NonString() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(123);
			jsa.getString(0);
		} catch (JSONException ex) {
			assertEquals("JSONArray[0] not a string.", ex.getMessage());
		}
	}

	public void testOptString() {
		JSONArray jsa = new JSONArray();
		jsa.put("123");
		jsa.put("-12");
		jsa.put("abc");
		jsa.put("123");
		assertEquals("123", jsa.optString(0));
		assertEquals("-12", jsa.optString(1));
		assertEquals("abc", jsa.optString(2));
		assertEquals("123", jsa.optString(3));
		assertEquals("", jsa.optString(4));
	}

	public void testOptJSONObject() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(new JSONObject().put("abc", "123"));
			assertEquals("{\"abc\":\"123\"}", jsa.optJSONObject(0).toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testOptJSONObject_NonJsonObject() {
		JSONArray jsa = new JSONArray();
		jsa.put("123");
		assertEquals(null, jsa.optJSONObject(0));
	}

	public void testOptJSONArray() {
		JSONArray jsa = new JSONArray();
		jsa.put(new JSONArray().put("abc"));
		assertEquals("[\"abc\"]", jsa.optJSONArray(0).toString());
	}

	public void testOptJSONArray_NonJsonArray() {
		JSONArray jsa = new JSONArray();
		jsa.put("123");
		assertEquals(null, jsa.optJSONArray(0));
	}

	public void testIsNull() {
		JSONArray jsa = new JSONArray();
		jsa.put(JSONObject.NULL);
		jsa.put("null");
		assertTrue(jsa.isNull(0));
		assertFalse(jsa.isNull(1));
	}

	public void testWriter() {
		try {
			StringWriter sw = new StringWriter();
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.put("-12");
			jsa.put(45);
			jsa.put(-98);
			jsa.put(new JSONArray().put("abc"));
			jsa.put("-12");
			jsa.put("abc");
			jsa.put("123");
			jsa.put("123");
			jsa.put(new JSONObject().put("abc", "123"));
			jsa.put("abc");
			jsa.put("123");
			jsa.write(sw);
			assertEquals(
					"[\"123\",\"-12\",45,-98,[\"abc\"],\"-12\",\"abc\",\"123\",\"123\",{\"abc\":\"123\"},\"abc\",\"123\"]",
					sw.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testWriter_BadWriter() {

		class BadWriter extends Writer {

			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				throw new IOException("Test Message From Bad Writer");
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void close() throws IOException {
			}
		}

		try {
			BadWriter sw = new BadWriter();
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.put("-12");
			jsa.put(45);
			jsa.put(-98);
			jsa.put(new JSONArray().put("abc"));
			jsa.put("-12");
			jsa.put("abc");
			jsa.put("123");
			jsa.put("123");
			jsa.put(new JSONObject().put("abc", "123"));
			jsa.put("abc");
			jsa.put("123");
			jsa.write(sw);
		} catch (JSONException ex) {
			assertEquals("Test Message From Bad Writer", ex.getMessage());
		}
	}

	public void testPut_ObjectAndSpecificIndex() {
		try {
			TestObject a = new TestObject();
			TestObject b = new TestObject();
			TestObject c = new TestObject();
			TestObject d = new TestObject();
			JSONArray jsa = new JSONArray();
			jsa.put(0, a);
			jsa.put(1, b);
			assertEquals(a, jsa.get(0));
			assertEquals(b, jsa.get(1));
			jsa.put(0, c);
			assertEquals(c, jsa.get(0));
			assertEquals(b, jsa.get(1));
			jsa.put(8, d);
			assertEquals(d, jsa.get(8));
			assertEquals(JSONObject.NULL, jsa.get(2));
			assertEquals(JSONObject.NULL, jsa.get(3));
			assertEquals(JSONObject.NULL, jsa.get(4));
			assertEquals(JSONObject.NULL, jsa.get(5));
			assertEquals(JSONObject.NULL, jsa.get(6));
			assertEquals(JSONObject.NULL, jsa.get(7));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPut_ObjectAndNegativeIndex() {
		try {
			TestObject a = new TestObject();
			JSONArray jsa = new JSONArray();
			jsa.put(-1, a);
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("JSONArray[-1] not found.", ex.getMessage());
		}
	}

	public void testToJSONObject() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.put("-12");
			jsa.put(45);
			jsa.put(-98);
			jsa.put(new JSONArray().put("abc"));
			jsa.put(new JSONObject().put("abc", "123"));
			JSONArray names = new JSONArray(new String[]
					{
							"bdd", "fdsa", "fds", "ewre", "rer", "gfs"
					});
			assertEquals(
					"{\"gfs\":{\"abc\":\"123\"},\"fdsa\":\"-12\",\"bdd\":\"123\",\"ewre\":-98,\"rer\":[\"abc\"],\"fds\":45}",
					jsa.toJSONObject(names).toString());
			assertEquals(null, jsa.toJSONObject(new JSONArray()));
			assertEquals(null, jsa.toJSONObject(null));
			assertEquals(null, new JSONArray().toJSONObject(names));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetJSONObject() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(new JSONObject().put("abc", "123"));
			assertEquals("{\"abc\":\"123\"}", jsa.getJSONObject(0).toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetJSONObject_NonJsonObject() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.getJSONObject(0);
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONArray[0] is not a JSONObject.", ex.getMessage());
		}
	}

	public void testGetJSONArray() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(new JSONArray().put("abc"));
			assertEquals("[\"abc\"]", jsa.getJSONArray(0).toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetJSONArray_NonJsonArray() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put("123");
			jsa.getJSONArray(0);
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONArray[0] is not a JSONArray.", ex.getMessage());
		}
	}

	public void testPut_Map() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("abc", "123");
		JSONArray jsa = new JSONArray();
		jsa.put(map);
		assertEquals("[{\"abc\":\"123\"}]", jsa.toString());
	}

	public void testConstructor_BadJsonArray() {
		try {
			new JSONArray("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("A JSONArray text must start with '[' at 1 [character 2 line 1]", ex.getMessage());
		}
	}

	public void testConstructor() {
		try {
			JSONArray jsa = new JSONArray("[]");
			assertEquals("[]", jsa.toString());
			jsa = new JSONArray("[\"abc\"]");
			assertEquals("[\"abc\"]", jsa.toString());
			jsa = new JSONArray("[\"abc\",\"123\"]");
			assertEquals("[\"abc\",\"123\"]", jsa.toString());
			jsa = new JSONArray("[123,{}]");
			assertEquals("[123,{}]", jsa.toString());
			jsa = new JSONArray("[123,,{}]");
			assertEquals("[123,null,{}]", jsa.toString());
			jsa = new JSONArray("[123,,{},]");
			assertEquals("[123,null,{}]", jsa.toString());
			jsa = new JSONArray("[123,,{};]");
			assertEquals("[123,null,{}]", jsa.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPut_Collection() {
		Collection<Object> stringCol = new Stack<Object>();
		stringCol.add("string1");
		stringCol.add("string2");
		stringCol.add("string3");
		stringCol.add("string4");
		JSONArray jsa = new JSONArray();
		jsa.put(stringCol);
		assertEquals("[[\"string1\",\"string2\",\"string3\",\"string4\"]]", jsa.toString());
	}

	public void testPut_BooleanAndSpecificIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(0, true);
			jsa.put(1, true);
			assertEquals(true, jsa.get(0));
			assertEquals(true, jsa.get(1));
			jsa.put(0, false);
			assertEquals(false, jsa.get(0));
			assertEquals(true, jsa.get(1));
			jsa.put(8, false);
			assertEquals(false, jsa.get(8));
			assertEquals(JSONObject.NULL, jsa.get(2));
			assertEquals(JSONObject.NULL, jsa.get(3));
			assertEquals(JSONObject.NULL, jsa.get(4));
			assertEquals(JSONObject.NULL, jsa.get(5));
			assertEquals(JSONObject.NULL, jsa.get(6));
			assertEquals(JSONObject.NULL, jsa.get(7));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPut_BooleanAndNegativeIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(-1, true);
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("JSONArray[-1] not found.", ex.getMessage());
		}
	}

	public void testPut_CollectionAndSpecificIndex() {
		try {
			Collection<Object> a = new Stack<Object>();
			a.add("string1");
			a.add("string4");
			Collection<Object> b = new Stack<Object>();
			b.add("string2");
			b.add("string3");
			Collection<Object> c = new Stack<Object>();
			c.add("string3");
			c.add("string4");
			Collection<Object> d = new Stack<Object>();
			d.add("string1");
			d.add("string2");
			JSONArray jsa = new JSONArray();
			jsa.put(0, a);
			jsa.put(1, b);
			assertEquals(new JSONArray(a).toString(), jsa.get(0).toString());
			assertEquals(new JSONArray(b).toString(), jsa.get(1).toString());
			jsa.put(0, c);
			assertEquals(new JSONArray(c).toString(), jsa.get(0).toString());
			assertEquals(new JSONArray(b).toString(), jsa.get(1).toString());
			jsa.put(8, d);
			assertEquals(new JSONArray(d).toString(), jsa.get(8).toString());
			assertEquals(JSONObject.NULL, jsa.get(2));
			assertEquals(JSONObject.NULL, jsa.get(3));
			assertEquals(JSONObject.NULL, jsa.get(4));
			assertEquals(JSONObject.NULL, jsa.get(5));
			assertEquals(JSONObject.NULL, jsa.get(6));
			assertEquals(JSONObject.NULL, jsa.get(7));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPut_CollectionAndNegativeIndex() {
		try {
			Collection<Object> a = new Stack<Object>();
			a.add("string1");
			a.add("string4");
			JSONArray jsa = new JSONArray();
			jsa.put(-1, a);
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("JSONArray[-1] not found.", ex.getMessage());
		}
	}

	public void testPut_DoubleAndSpecificIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(0, 10.0);
			jsa.put(1, 30.2);
			assertEquals(10.0, jsa.get(0));
			assertEquals(30.2, jsa.get(1));
			jsa.put(0, 52.64);
			assertEquals(52.64, jsa.get(0));
			assertEquals(30.2, jsa.get(1));
			jsa.put(8, 14.23);
			assertEquals(14.23, jsa.get(8));
			assertEquals(JSONObject.NULL, jsa.get(2));
			assertEquals(JSONObject.NULL, jsa.get(3));
			assertEquals(JSONObject.NULL, jsa.get(4));
			assertEquals(JSONObject.NULL, jsa.get(5));
			assertEquals(JSONObject.NULL, jsa.get(6));
			assertEquals(JSONObject.NULL, jsa.get(7));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPut_DoubleAndNegativeIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(-1, 30.65);
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("JSONArray[-1] not found.", ex.getMessage());
		}
	}

	public void testPut_IntAndSpecificIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(0, 54);
			jsa.put(1, 82);
			assertEquals(54, jsa.get(0));
			assertEquals(82, jsa.get(1));
			jsa.put(0, 36);
			assertEquals(36, jsa.get(0));
			assertEquals(82, jsa.get(1));
			jsa.put(8, 67);
			assertEquals(67, jsa.get(8));
			assertEquals(JSONObject.NULL, jsa.get(2));
			assertEquals(JSONObject.NULL, jsa.get(3));
			assertEquals(JSONObject.NULL, jsa.get(4));
			assertEquals(JSONObject.NULL, jsa.get(5));
			assertEquals(JSONObject.NULL, jsa.get(6));
			assertEquals(JSONObject.NULL, jsa.get(7));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPut_IntAndNegativeIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(-1, 3);
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("JSONArray[-1] not found.", ex.getMessage());
		}
	}

	public void testPut_LongAndSpecificIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(0, 54L);
			jsa.put(1, 456789123L);
			assertEquals(54L, jsa.get(0));
			assertEquals(456789123L, jsa.get(1));
			jsa.put(0, 72887L);
			assertEquals(72887L, jsa.get(0));
			assertEquals(456789123L, jsa.get(1));
			jsa.put(8, 39397L);
			assertEquals(39397L, jsa.get(8));
			assertEquals(JSONObject.NULL, jsa.get(2));
			assertEquals(JSONObject.NULL, jsa.get(3));
			assertEquals(JSONObject.NULL, jsa.get(4));
			assertEquals(JSONObject.NULL, jsa.get(5));
			assertEquals(JSONObject.NULL, jsa.get(6));
			assertEquals(JSONObject.NULL, jsa.get(7));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPut_LongAndNegativeIndex() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(-1, 456486794L);
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("JSONArray[-1] not found.", ex.getMessage());
		}
	}

	public void testPut_MapAndSpecificIndex() {
		try {
			Map<String, Object> a = new HashMap<String, Object>();
			a.put("abc", "123");
			Map<String, Object> b = new HashMap<String, Object>();
			b.put("abffc", "1253");
			Map<String, Object> c = new HashMap<String, Object>();
			c.put("addbc", "145623");
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("abffdc", "122623");
			JSONArray jsa = new JSONArray();
			jsa.put(0, a);
			jsa.put(1, b);
			assertEquals(new JSONObject(a).toString(), jsa.get(0).toString());
			assertEquals(new JSONObject(b).toString(), jsa.get(1).toString());
			jsa.put(0, c);
			assertEquals(new JSONObject(c).toString(), jsa.get(0).toString());
			assertEquals(new JSONObject(b).toString(), jsa.get(1).toString());
			jsa.put(8, d);
			assertEquals(new JSONObject(d).toString(), jsa.get(8).toString());
			assertEquals(JSONObject.NULL, jsa.get(2));
			assertEquals(JSONObject.NULL, jsa.get(3));
			assertEquals(JSONObject.NULL, jsa.get(4));
			assertEquals(JSONObject.NULL, jsa.get(5));
			assertEquals(JSONObject.NULL, jsa.get(6));
			assertEquals(JSONObject.NULL, jsa.get(7));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPut_MapAndNegativeIndex() {
		try {
			Map<String, Object> a = new HashMap<String, Object>();
			a.put("abc", "123");
			JSONArray jsa = new JSONArray();
			jsa.put(-1, a);
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("JSONArray[-1] not found.", ex.getMessage());
		}
	}

	public void testRemove() {
		try {
			JSONArray jsa = new JSONArray();
			jsa.put(0, 54);
			jsa.put(1, 456789123);
			jsa.put(2, 72887);
			jsa.remove(1);
			assertEquals(54, jsa.get(0));
			assertEquals(72887, jsa.get(1));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}
}