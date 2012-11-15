package org.json.tests;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import junit.framework.TestCase;

public class TestJSONObject extends TestCase {

	final static double eps = 2.220446049250313e-16;

	public class GoodJsonString implements JSONString {
		@Override
		public String toJSONString() {
			return "jsonstring";
		}
	}

	public class NullJsonString implements JSONString {
		@Override
		public String toJSONString() {
			return null;
		}
	}

	public class BadJsonString implements JSONString {
		@Override
		public String toJSONString() {
			String[] arString = new String[]
					{
							"abc"
					};
			return arString[1];
		}
	}

	public class ObjectWithPrimitives {

		public int i;
		private String nullString;
		private String j;
		private double k;
		private long l;
		public boolean m;

		public ObjectWithPrimitives() {
			i = 3;
			j = "3";
			k = 10.03;
			l = 5748548957230984584L;
			m = true;
			nullString = null;
		}

		public int getI() { return i; }
		public String getJ() { return j; }
		public double getK() { return k; }
		public long getL() { return l; }
		public boolean getM() { return m; }
		public boolean getM(Boolean test) { return m; }
		public String getNullString() { return nullString; }
		public int getZERO() { return 0; }
		public int getone() { return 1; }

		public boolean isBig() { return false; }
		private boolean isSmall() { return true; }
	}

	public class ObjectWithPrimitivesExtension extends ObjectWithPrimitives {
	}

	public void testNull() throws Exception {
		JSONObject jso = new JSONObject("{\"message\":\"null\"}");
		assertFalse(jso.isNull("message"));
		assertEquals("null", jso.getString("message"));

		jso = new JSONObject("{\"message\":null}");
		assertTrue(jso.isNull("message"));
	}

	public void testConstructor_DuplicateKey() {
		try {
			String string = "{\"koda\": true, \"koda\": true}";
			JSONObject jso = new JSONObject(string);
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("Duplicate key \"koda\"", ex.getMessage());
		}
	}

	public void testConstructor_NullKey() {
		try {
			new JSONObject().put(null, "howard");
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("Null key.", ex.getMessage());
		}
	}

	public void testGetDouble_InvalidKeyHoward() {
		try {
			new JSONObject().getDouble("howard");
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"howard\"] not found.", ex.getMessage());
		}
	}

	public void testGetDouble_InvalidKeyStooge() {
		JSONObject jso = new JSONObject();
		try {
			jso.getDouble("stooge");
			fail("expecting JSONException here.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"stooge\"] not found.", ex.getMessage());
		}
	}

	public void testIsNull() {
		try {
			JSONObject jso = new JSONObject();
			Object object = null;
			jso.put("booga", object);
			jso.put("wooga", JSONObject.NULL);
			assertEquals("{\"wooga\":null}", jso.toString());
			assertTrue(jso.isNull("booga"));
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testIncrement_NewKey() {
		try {
			JSONObject jso = new JSONObject();
			jso.increment("two");
			jso.increment("two");
			assertEquals("{\"two\":2}", jso.toString());
			assertEquals(2, jso.getInt("two"));
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testToString_ListofLists() {
		try {
			String string = "{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }";
			JSONObject jso = new JSONObject(string);
			assertEquals("{\"list of lists\": [\n" + "    [\n" + "        1,\n"
					+ "        2,\n" + "        3\n" + "    ],\n" + "    [\n"
					+ "        4,\n" + "        5,\n" + "        6\n"
					+ "    ]\n" + "]}", jso.toString(4));
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testToString_Indentation() {
		try {
			String string = "{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }";
			JSONObject jso = new JSONObject(string);
			assertEquals(
					"{\"entity\": {\n  \"id\": 12336,\n  \"averageRating\": null,\n  \"ratingCount\": null,\n  \"name\": \"IXXXXXXXXXXXXX\",\n  \"imageURL\": \"\"\n}}",
					jso.toString(2));
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testMultipleThings() {
		try {
			JSONObject jso = new JSONObject(
					"{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001,"
							+ " .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"], "
							+ "  to   : null, op : 'Good',"
							+ "ten:10} postfix comment");
			jso.put("String", "98.6");
			jso.put("JSONObject", new JSONObject());
			jso.put("JSONArray", new JSONArray());
			jso.put("int", 57);
			jso.put("double", 123456789012345678901234567890.);
			jso.put("true", true);
			jso.put("false", false);
			jso.put("null", JSONObject.NULL);
			jso.put("bool", "true");
			jso.put("zero", -0.0);
			jso.put("\\u2028", "\u2028");
			jso.put("\\u2029", "\u2029");

			JSONArray jsonarray = jso.getJSONArray("foo");
			jsonarray.put(666);
			jsonarray.put(2001.99);
			jsonarray.put("so \"fine\".");
			jsonarray.put("so <fine>.");
			jsonarray.put(true);
			jsonarray.put(false);
			jsonarray.put(new JSONArray());
			jsonarray.put(new JSONObject());
			jso.put("keys", JSONObject.getNames(jso));
			assertEquals(
					"{\n    \"to\": null,\n    \"ten\": 10,\n    \"JSONObject\": {},\n    \"JSONArray\": [],\n    \"op\": \"Good\",\n    \"keys\": [\n        \"to\",\n        \"ten\",\n        \"JSONObject\",\n        \"JSONArray\",\n        \"op\",\n        \"int\",\n        \"true\",\n        \"foo\",\n        \"zero\",\n        \"double\",\n        \"String\",\n        \"false\",\n        \"bool\",\n        \"\\\\u2028\",\n        \"\\\\u2029\",\n        \"null\"\n    ],\n    \"int\": 57,\n    \"true\": true,\n    \"foo\": [\n        true,\n        false,\n        9876543210,\n        0,\n        1.00000001,\n        1.000000000001,\n        1,\n        1.0E-17,\n        2,\n        0.1,\n        2.0E100,\n        -32,\n        [],\n        {},\n        \"string\",\n        666,\n        2001.99,\n        \"so \\\"fine\\\".\",\n        \"so <fine>.\",\n        true,\n        false,\n        [],\n        {}\n    ],\n    \"zero\": -0,\n    \"double\": 1.2345678901234568E29,\n    \"String\": \"98.6\",\n    \"false\": false,\n    \"bool\": \"true\",\n    \"\\\\u2028\": \"\\u2028\",\n    \"\\\\u2029\": \"\\u2029\",\n    \"null\": null\n}",
					jso.toString(4));
			assertEquals(98.6d, jso.getDouble("String"), eps);
			assertTrue(jso.getBoolean("bool"));
			assertEquals(
					"[true,false,9876543210,0,1.00000001,1.000000000001,1,1.0E-17,2,0.1,2.0E100,-32,[],{},\"string\",666,2001.99,\"so \\\"fine\\\".\",\"so <fine>.\",true,false,[],{}]",
					jso.getJSONArray("foo").toString());
			assertEquals("Good", jso.getString("op"));
			assertEquals(10, jso.getInt("ten"));
			assertFalse(jso.optBoolean("oops"));
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testMultipleThings2() {
		try {
			JSONObject jso = new JSONObject(
					"{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
			assertEquals(
					"{\n \"int\": 2147483647,\n \"string\": \"98.6\",\n \"longer\": 9223372036854775807,\n \"double\": \"9223372036854775808\",\n \"long\": 2147483648\n}",
					jso.toString(1));

			assertEquals(2147483647, jso.getInt("int"));
			assertEquals(-2147483648, jso.getInt("long"));
			assertEquals(-1, jso.getInt("longer"));
			try {
				jso.getInt("double");
				fail("should fail with - JSONObject[\"double\"] is not an int.");
			} catch (JSONException expected) {
				assertEquals("JSONObject[\"double\"] is not an int.",
						expected.getMessage());
			}
			try {
				jso.getInt("string");
				fail("should fail with - JSONObject[\"string\"] is not an int.");
			} catch (JSONException expected) {
				assertEquals("JSONObject[\"string\"] is not an int.",
						expected.getMessage());
			}

			assertEquals(2147483647, jso.getLong("int"));
			assertEquals(2147483648l, jso.getLong("long"));
			assertEquals(9223372036854775807l, jso.getLong("longer"));
			try {
				jso.getLong("double");
				fail("should fail with - JSONObject[\"double\"] is not a long.");
			} catch (JSONException expected) {
				assertEquals("JSONObject[\"double\"] is not a long.",
						expected.getMessage());
			}
			try {
				jso.getLong("string");
				fail("should fail with - JSONObject[\"string\"] is not a long.");
			} catch (JSONException expected) {
				assertEquals("JSONObject[\"string\"] is not a long.",
						expected.getMessage());
			}

			assertEquals(2.147483647E9, jso.getDouble("int"), eps);
			assertEquals(2.147483648E9, jso.getDouble("long"), eps);
			assertEquals(9.223372036854776E18, jso.getDouble("longer"),
					eps);
			assertEquals(9223372036854775808d, jso.getDouble("double"),
					eps);
			assertEquals(98.6, jso.getDouble("string"), eps);

			jso.put("good sized", 9223372036854775807L);
			assertEquals(
					"{\n \"int\": 2147483647,\n \"string\": \"98.6\",\n \"longer\": 9223372036854775807,\n \"good sized\": 9223372036854775807,\n \"double\": \"9223372036854775808\",\n \"long\": 2147483648\n}",
					jso.toString(1));

			JSONArray jsonarray = new JSONArray(
					"[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");
			assertEquals(
					"[\n 2147483647,\n 2147483648,\n 9223372036854775807,\n \"9223372036854775808\"\n]",
					jsonarray.toString(1));

			List<String> expectedKeys = new ArrayList<String>(6);
			expectedKeys.add("int");
			expectedKeys.add("string");
			expectedKeys.add("longer");
			expectedKeys.add("good sized");
			expectedKeys.add("double");
			expectedKeys.add("long");

			Iterator iterator = jso.keys();
			while (iterator.hasNext()) {
				String string = (String) iterator.next();
				assertTrue(expectedKeys.remove(string));
			}
			assertEquals(0, expectedKeys.size());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testPut_CollectionAndMap() {
		try {
			String string = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";
			JSONObject jso = new JSONObject(string);
			assertEquals(
					"{\"AnimalColors\":{\"worm\":\"pink\",\"lamb\":\"black\",\"pig\":\"pink\"},\"plist\":\"Apple\",\"AnimalSounds\":{\"worm\":\"baa\",\"Lisa\":\"Why is the worm talking like a lamb?\",\"lamb\":\"baa\",\"pig\":\"oink\"},\"AnimalSmells\":{\"worm\":\"wormy\",\"lamb\":\"lambish\",\"pig\":\"piggish\"}}",
					jso.toString());

			Collection<Object> collection = null;
			Map<String, Object> map = null;

			jso = new JSONObject(map);

			JSONArray jsonarray = new JSONArray(collection);
			jso.append("stooge", "Joe DeRita");
			jso.append("stooge", "Shemp");
			jso.accumulate("stooges", "Curly");
			jso.accumulate("stooges", "Larry");
			jso.accumulate("stooges", "Moe");
			jso.accumulate("stoogearray", jso.get("stooges"));
			jso.put("map", map);
			jso.put("collection", collection);
			jso.put("array", jsonarray);
			jsonarray.put(map);
			jsonarray.put(collection);
			assertEquals(
					"{\"stooge\":[\"Joe DeRita\",\"Shemp\"],\"map\":{},\"stooges\":[\"Curly\",\"Larry\",\"Moe\"],\"collection\":[],\"stoogearray\":[[\"Curly\",\"Larry\",\"Moe\"]],\"array\":[{},[]]}",
					jso.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testAccumulate() {
		try {
			JSONObject jso = new JSONObject();
			jso.accumulate("stooge", "Curly");
			jso.accumulate("stooge", "Larry");
			jso.accumulate("stooge", "Moe");

			JSONArray jsonarray = jso.getJSONArray("stooge");
			jsonarray.put(5, "Shemp");
			assertEquals("{\"stooge\": [\n" + "    \"Curly\",\n"
					+ "    \"Larry\",\n" + "    \"Moe\",\n" + "    null,\n"
					+ "    null,\n" + "    \"Shemp\"\n" + "]}",
					jso.toString(4));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testWrite() {
		try {
			JSONObject jso = new JSONObject();
			jso.accumulate("stooge", "Curly");
			jso.accumulate("stooge", "Larry");
			jso.accumulate("stooge", "Moe");

			JSONArray jsonarray = jso.getJSONArray("stooge");
			jsonarray.put(5, "Shemp");
			assertEquals(
					"{\"stooge\":[\"Curly\",\"Larry\",\"Moe\",null,null,\"Shemp\"]}",
					jso.write(new StringWriter()).toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testToString_Html() {
		try {
			JSONObject jso = new JSONObject(
					"{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");
			assertEquals(
					"{\"script\":\"It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers<\\/script>so we insert a backslash before the /\"}",
					jso.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testToString_MultipleTestCases() {
		try {
			JSONObject jso = new JSONObject(
					"{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
			assertEquals(
					"{\n \"noh\": \"0x0x\",\n \"one\": [[1]],\n \"o\": 999,\n \"+\": 6.0E66,\n \"true\": true,\n \"forgiving\": \"This package can be used to parse formats that are similar to but not stricting conforming to JSON\",\n \"fun\": \"with non-standard forms\",\n \"double\": 0.666,\n \"uno\": [[{\"1\": 1}]],\n \"dec\": 666,\n \"oct\": 666,\n \"hex\": \"0x666\",\n \"string\": \"o. k.\",\n \"empty\": \"\",\n \"false\": false,\n \"[true]\": [[\n  \"!\",\n  \"@\",\n  \"*\"\n ]],\n \"pluses\": \"+++\",\n \"why\": \"To make it easier to migrate existing data to JSON\",\n \"null\": null\n}",
					jso.toString(1));
			assertTrue(jso.getBoolean("true"));
			assertFalse(jso.getBoolean("false"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testConstructor_PriativeClass() {
		JSONObject jso = new JSONObject(new ObjectWithPrimitives());
		assertEquals(
				"{\"l\":5748548957230984584,\"m\":true,\"big\":false,\"j\":\"3\",\"k\":10.03,\"ZERO\":0,\"i\":3}",
				jso.toString());
	}

	public void testConstructor_SubClass() {
		ObjectWithPrimitives ob = new ObjectWithPrimitivesExtension();
		JSONObject jso = new JSONObject(ob);
		assertEquals(
				"{\"l\":5748548957230984584,\"m\":true,\"big\":false,\"j\":\"3\",\"k\":10.03,\"ZERO\":0,\"i\":3}",
				jso.toString());
	}

	public void testConstructor_PrivateClass() {
		class PrivateObject {
			private int i;

			public PrivateObject() {
				i = 3;
			}

			public int getI() { return i; }
		}

		JSONObject jso = new JSONObject(new PrivateObject());
		assertEquals("{}", jso.toString());
	}

	public void testConstructor_ArrayList() {
		List<String> ar = new ArrayList<String>();
		ar.add("test1");
		ar.add("test2");

		JSONObject jso = new JSONObject(ar);
		assertEquals("{\"empty\":false}", jso.toString());
	}

	public void testConstructor_ClassClass() {
		try {
			JSONObject jso = new JSONObject(this.getClass());
			assertEquals("class junit.framework.TestCase", jso.get("genericSuperclass").toString());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	public static void testConstructor_FrenchResourceBundle() {
		try {
			Locale currentLocale = new Locale("fr", "CA", "UNIX");
			assertEquals(
					"{\"ASCII\":\"Number that represent chraracters\",\"JSON\":\"What are we testing?\",\"JAVA\":\"The language you are running to see this\"}",
					new JSONObject("org.json.tests.SampleResourceBundle",
							currentLocale).toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public static void testConstructor_UsResourceBundle() {
		try {
			Locale currentLocale = new Locale("en", "US");
			assertEquals(
					"{\"ASCII\":\"American Standard Code for Information Interchange\",\"JSON\":\"JavaScript Object Notation\",\"JAVA\":{\"desc\":\"Just Another Vague Acronym\",\"data\":\"Sweet language\"}}",
					new JSONObject("org.json.tests.SampleResourceBundle",
							currentLocale).toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testConstructor_ObjectWithStringArray() {
		assertEquals("{\"m\":true,\"i\":3}", new JSONObject(
				new ObjectWithPrimitives(), new String[]
				{
						"i", "m", "k"
				}).toString());
	}

	public void testOpt() {
		try {
			JSONObject jso = new JSONObject("{\"a\":2}");
			assertEquals(2, jso.opt("a"));
			assertEquals(null, jso.opt("b"));
			assertEquals(null, jso.opt(null));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public static void testStringToValue() {
		assertEquals("", JSONObject.stringToValue(""));
		assertEquals(true, JSONObject.stringToValue("true"));
		assertEquals(false, JSONObject.stringToValue("false"));
		assertEquals(JSONObject.NULL, JSONObject.stringToValue("null"));
		assertEquals(10, JSONObject.stringToValue("10"));
		assertEquals(10000.0, JSONObject.stringToValue("10e3"));
		assertEquals(10000.0, JSONObject.stringToValue("10E3"));
		assertEquals("10E3000000000", JSONObject.stringToValue("10E3000000000"));
	}

	public static void testQuote() {
		assertEquals("\"\"", JSONObject.quote(""));
		assertEquals("\"\"", JSONObject.quote(null));
		assertEquals("\"true\"", JSONObject.quote("true"));
		assertEquals("\"10\"", JSONObject.quote("10"));
		assertEquals("\"\\b\\t\\n\\f\\r\"", JSONObject.quote("\b\t\n\f\r"));
		assertEquals("\"\\u0012\\u0085\\u2086\u2286\"", JSONObject.quote("\u0012\u0085\u2086\u2286"));
	}

	public void testGetNames() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("a", "123");
			jso.put("b", "123");
			jso.put("c", "123");

			String[] names = JSONObject.getNames(jso);
			assertEquals(3, names.length);
			assertEquals("b", names[0]);
			assertEquals("c", names[1]);
			assertEquals("a", names[2]);
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetNames_EmptyJsonObject() {
		JSONObject jso = new JSONObject();
		assertEquals(null, JSONObject.getNames(jso));
	}

	public void testGetNames_ObjectWithPrimatives() {
		String[] names = JSONObject.getNames(new ObjectWithPrimitives());
		assertEquals(2, names.length);
		assertEquals("i", names[0]);
		assertEquals("m", names[1]);
	}

	public static void testGetNames_EmptyObject() {
		class EmptyObject {
		}
		assertEquals(null, JSONObject.getNames(new EmptyObject()));
	}

	public static void testGetNames_Null() {
		ObjectWithPrimitives owp = null;
		assertEquals(null, JSONObject.getNames(owp));
	}

	public void testGetLong_Long() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "98765432");
			jso.put("123", 98765432);
			assertEquals(98765432, jso.getLong("abc"));
			assertEquals(98765432, jso.getLong("123"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetJsonObject_JsonObject() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", new JSONObject());
			assertEquals("{}", jso.getJSONObject("abc").toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetJsonObject_Int() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", 45);
			jso.getJSONObject("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] is not a JSONObject.", ex.getMessage());
		}
	}

	public void testGetJsonObject_InvalidKey() {
		try {
			JSONObject jso = new JSONObject();
			jso.getJSONObject("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] not found.", ex.getMessage());
		}
	}

	public void testGetJsonArray_JsonArray() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", new JSONArray());
			assertEquals("[]", jso.getJSONArray("abc").toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetJsonArray_Int() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", 45);
			jso.getJSONArray("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] is not a JSONArray.", ex.getMessage());
		}
	}

	public void testGetJsonArray_InvalidKey() {
		try {
			JSONObject jso = new JSONObject();
			jso.getJSONArray("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] not found.", ex.getMessage());
		}
	}

	public void testGetInt_Int() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", 45);
			assertEquals(45, jso.getInt("abc"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetInt_IntString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "45");
			assertEquals(45, jso.getInt("abc"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetInt_LetterString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "def");
			jso.getInt("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] is not an int.", ex.getMessage());
		}
	}

	public void testGetInt_InvalidKey() {
		try {
			JSONObject jso = new JSONObject();
			jso.getInt("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] not found.", ex.getMessage());
		}
	}

	public void testGetDouble_Double() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", 45.10);
			assertEquals(45.10, jso.getDouble("abc"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetDouble_DoubleString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "45.20");
			assertEquals(45.20, jso.getDouble("abc"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetDouble_LetterString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "def");
			jso.getDouble("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] is not a number.", ex.getMessage());
		}
	}

	public void testGetDouble_InvalidKey() {
		try {
			JSONObject jso = new JSONObject();
			jso.getDouble("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] not found.", ex.getMessage());
		}
	}

	public void testGetBoolean_Boolean() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", true);
			jso.put("123", false);
			assertTrue(jso.getBoolean("abc"));
			assertFalse(jso.getBoolean("123"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetBoolean_BooleanString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "true");
			jso.put("123", "false");
			jso.put("ABC", "TRUE");
			jso.put("456", "FALSE");
			assertTrue(jso.getBoolean("abc"));
			assertFalse(jso.getBoolean("123"));
			assertTrue(jso.getBoolean("ABC"));
			assertFalse(jso.getBoolean("456"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetBoolean_LetterString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "def");
			jso.getBoolean("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] is not a Boolean.", ex.getMessage());
		}
	}

	public void testGetBoolean_Int() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", 45);
			jso.getBoolean("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] is not a Boolean.", ex.getMessage());
		}
	}

	public void testGetBoolean_InvalidKey() {
		try {
			JSONObject jso = new JSONObject();
			jso.getBoolean("abc");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] not found.", ex.getMessage());
		}
	}

	public void testGet_NullKey() {
		try {
			JSONObject jso = new JSONObject();
			jso.get(null);
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("Null key.", ex.getMessage());
		}
	}

	public void testToString_Indents() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			jso.put("1235", new JSONObject().put("abc", "123"));
			jso.put("1234", "abc");
			jso.put(
					"1239",
					new JSONObject().put("1fd23", new JSONObject()).put(
							"12gfdgfg3",
							new JSONObject().put("f123", "123").put("12fgfg3",
									"abc")));
			assertEquals(
					"{\n    \"1234\": \"abc\",\n    \"1235\": {\"abc\": \"123\"},\n    \"123\": \"123\",\n    \"1239\": {\n        \"1fd23\": {},\n        \"12gfdgfg3\": {\n            \"f123\": \"123\",\n            \"12fgfg3\": \"abc\"\n        }\n    }\n}",
					jso.toString(4));
			assertEquals("{\"1gg23\": \"123\"}",
					new JSONObject().put("1gg23", "123").toString(4));
			assertEquals("{}", new JSONObject().toString(4));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testToString_Exception() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", new BadJsonString());
			assertEquals(null, jso.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public static void testNumberToString() {
		try {
			assertEquals("30.7", JSONObject.numberToString(30.70));
			assertEquals("3.0E71", JSONObject.numberToString(30e70));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public static void testNumberToString_Null() {
		try {
			JSONObject.numberToString(null);
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("Null pointer", ex.getMessage());
		}
	}

	public void testWrap() {
		try {
			assertEquals(true, JSONObject.wrap(true));
			assertEquals(35, JSONObject.wrap(35));
			assertEquals(35.5, JSONObject.wrap(35.50));
			assertEquals(56456456L, JSONObject.wrap(56456456L));
			assertEquals(JSONObject.NULL, JSONObject.wrap(null));
			assertEquals(JSONObject.NULL, JSONObject.wrap(JSONObject.NULL));
			BadJsonString a = new BadJsonString();
			assertEquals(a, JSONObject.wrap(a));
			NullJsonString q = new NullJsonString();
			assertEquals(q, JSONObject.wrap(q));
			assertEquals((short) 12, JSONObject.wrap((short) 12));
			assertEquals('a', JSONObject.wrap('a'));
			assertEquals((byte) 15, JSONObject.wrap((byte) 15));
			assertEquals((float) 15.2, JSONObject.wrap((float) 15.2));
			JSONObject b;
			b = new JSONObject().put("abc", "123");
			assertEquals(b, JSONObject.wrap(b));
			JSONArray c = new JSONArray().put("abc");
			assertEquals(c, JSONObject.wrap(c));
			Collection<String> stringCol = new Stack<String>();
			stringCol.add("string1");
			assertEquals("[\"string1\"]", JSONObject.wrap(stringCol).toString());
			assertEquals("[\"abc\",\"123\"]", JSONObject.wrap(new String[]
					{
							"abc", "123"
					}).toString());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("abc", "123");
			assertEquals("{\"abc\":\"123\"}", JSONObject.wrap(map).toString());
			assertEquals("java.io.IOException",
					JSONObject.wrap(new java.io.IOException()));
			Class<?> d = this.getClass();
			assertEquals("class org.json.tests.TestJSONObject",
					JSONObject.wrap(d));
			class testClass {
			}
			assertEquals("{}", JSONObject.wrap(new testClass()).toString());

			class BadCollection<E> implements Collection<E> {

				@Override
				public int size() {
					return 0;
				}

				@Override
				public boolean isEmpty() {
					return false;
				}

				@Override
				public boolean contains(Object o) {
					return false;
				}

				@Override
				public Iterator<E> iterator() {
					Iterator<E>[] i = new Iterator[0];
					return i[1];
				}

				@Override
				public Object[] toArray() {
					return null;
				}

				@SuppressWarnings("unchecked")
				@Override
				public Object[] toArray(Object[] Type) {
					return null;
				}

				@Override
				public boolean add(Object ex) {
					return false;
				}

				@Override
				public boolean remove(Object o) {
					return false;
				}

				@Override
				public boolean containsAll(Collection<?> collection) {
					return false;
				}

				@Override
				public boolean addAll(Collection<? extends E> collection) {
					return false;
				}

				@Override
				public boolean removeAll(Collection<?> collection) {
					return false;
				}

				@Override
				public boolean retainAll(Collection<?> collection) {
					return false;
				}

				@Override
				public void clear() {
				}

			}

			try {
				JSONObject.wrap(new BadCollection<String>()).toString();
				fail("Should have thrown exception.");
			} catch (Exception ex) {
				assertEquals(null, ex.getMessage());
			}
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testWriter() {
		try {
			StringWriter sw = new StringWriter();
			JSONObject jso = new JSONObject();
			jso.put("1ghr23", "123");
			jso.put("1ss23", "-12");
			jso.put("1tr23", 45);
			jso.put("1trt23", -98);
			jso.put("1hh23", new JSONObject().put("123", "abc"));
			jso.put("1er23", "-12");
			jso.put("1re23", "abc");
			jso.put("1fde23", "123");
			jso.put("1fd23", "123");
			jso.put("1ffdsf23", new JSONObject().put("abc", "123"));
			jso.put("fd123", "abc");
			jso.put("12fdsf3", "123");
			jso.write(sw);
			assertEquals(
					"{\"1tr23\":45,\"1ss23\":\"-12\",\"1fd23\":\"123\",\"1trt23\":-98,\"1ffdsf23\":{\"abc\":\"123\"},\"1ghr23\":\"123\",\"1fde23\":\"123\",\"fd123\":\"abc\",\"12fdsf3\":\"123\",\"1hh23\":{\"123\":\"abc\"},\"1re23\":\"abc\",\"1er23\":\"-12\"}",
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
			JSONObject jso = new JSONObject();
			jso.put("1ghr23", "123");
			jso.put("1ss23", "-12");
			jso.put("1tr23", 45);
			jso.put("1trt23", -98);
			jso.put("1hh23", new JSONObject().put("123", "abc"));
			jso.put("1er23", "-12");
			jso.put("1re23", "abc");
			jso.put("1fde23", "123");
			jso.put("1fd23", "123");
			jso.put("1ffdsf23", new JSONObject().put("abc", "123"));
			jso.put("fd123", "abc");
			jso.put("12fdsf3", "123");
			jso.write(sw);
		} catch (JSONException ex) {
			assertEquals("Test Message From Bad Writer", ex.getMessage());
		}
	}

	public void testIncrement() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("1ghr23", 30.56);
			map.put("1ss23", -12.22);
			map.put("1tr23", 45);
			map.put("1trt23", -98);
			map.put("1hh23", (float) 12.6);
			map.put("1er23", (float) -456.255);
			map.put("1re23", 5543L);
			map.put("1fde23", -46546546L);
			JSONObject jso = new JSONObject(map);
			jso.increment("1ghr23");
			jso.increment("1ss23");
			jso.increment("1tr23");
			jso.increment("1trt23");
			jso.increment("1hh23");
			jso.increment("1er23");
			jso.increment("1re23");
			jso.increment("1fde23");
			assertEquals(31.56, jso.get("1ghr23"));
			assertEquals(-11.22, jso.get("1ss23"));
			assertEquals(46, jso.get("1tr23"));
			assertEquals(-97, jso.get("1trt23"));
			assertEquals(13.6f, (Double) jso.get("1hh23"), eps);
			assertEquals(-455.255f, (Double) jso.get("1er23"), eps);
			assertEquals(5544L, jso.get("1re23"));
			assertEquals(-46546545L, jso.get("1fde23"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testIncrement_String() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "abc");
			jso.increment("123");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("Unable to increment [\"123\"].", ex.getMessage());
		}
	}

	public void testGet_InvalidIndex() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("554", "123");
			jso.get("abc");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"abc\"] not found.", ex.getMessage());
		}
	}

	public void testGet_ValidIndex() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "123");
			assertEquals("123", jso.get("abc"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetBoolean() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("1gg23", "true");
			jso.put("1dd23", "false");
			jso.put("1ff23", true);
			jso.put("1rr23", false);
			jso.put("1hh23", "TRUE");
			jso.put("1hhhgf23", "FALSE");
			assertTrue(jso.getBoolean("1gg23"));
			assertFalse(jso.getBoolean("1dd23"));
			assertTrue(jso.getBoolean("1ff23"));
			assertFalse(jso.getBoolean("1rr23"));
			assertTrue(jso.getBoolean("1hh23"));
			assertFalse(jso.getBoolean("1hhhgf23"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetBoolean_NonBoolean() {
		try {
		JSONObject jso = new JSONObject();
			jso.put("123", "123");
			jso.getBoolean("123");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"123\"] is not a Boolean.", ex.getMessage());
		}
	}

	public void testOptBoolean() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("1gg23", "true");
			jso.put("1dd23", "false");
			jso.put("1ff23", true);
			jso.put("1rr23", false);
			jso.put("1hh23", "TRUE");
			jso.put("1hhhgf23", "FALSE");
			assertTrue(jso.optBoolean("1gg23"));
			assertFalse(jso.optBoolean("1dd23"));
			assertTrue(jso.optBoolean("1ff23"));
			assertFalse(jso.optBoolean("1rr23"));
			assertTrue(jso.optBoolean("1hh23"));
			assertFalse(jso.optBoolean("1hhhgf23"));
			assertFalse(jso.optBoolean("chicken"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetInt() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("1gg23", "123");
			jso.put("1g23", "-12");
			jso.put("123", 45);
			jso.put("1rr23", -98);
			assertEquals(123, jso.getInt("1gg23"));
			assertEquals(-12, jso.getInt("1g23"));
			assertEquals(45, jso.getInt("123"));
			assertEquals(-98, jso.getInt("1rr23"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetInt_NonInteger() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "abc");
			jso.getInt("123");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"123\"] is not an int.", ex.getMessage());
		}
	}

	public void testOptInt() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("1gg23", "123");
			jso.put("1g23", "-12");
			jso.put("123", 45);
			jso.put("1rr23", -98);
			assertEquals(123, jso.optInt("1gg23"));
			assertEquals(-12, jso.optInt("1g23"));
			assertEquals(45, jso.optInt("123"));
			assertEquals(-98, jso.optInt("1rr23"));
			assertEquals(0, jso.optInt("catfish"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetDouble() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			jso.put("1fd23", "-12");
			jso.put("1gfd23", 45);
			jso.put("1gg23", -98);
			jso.put("1f23", "123.5");
			jso.put("1ss23", "-12.87");
			jso.put("1ew23", 45.22);
			jso.put("1tt23", -98.18);
			assertEquals(123.0, jso.getDouble("123"));
			assertEquals(-12.0, jso.getDouble("1fd23"));
			assertEquals(45.0, jso.getDouble("1gfd23"));
			assertEquals(-98.0, jso.getDouble("1gg23"));
			assertEquals(123.5, jso.getDouble("1f23"));
			assertEquals(-12.87, jso.getDouble("1ss23"));
			assertEquals(45.22, jso.getDouble("1ew23"));
			assertEquals(-98.18, jso.getDouble("1tt23"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetDouble_NonDouble() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "abc");
			jso.getDouble("123");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"123\"] is not a number.", ex.getMessage());
		}
	}

	public void testOptDouble() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			jso.put("1fd23", "-12");
			jso.put("1gfd23", 45);
			jso.put("1gg23", -98);
			jso.put("1f23", "123.5");
			jso.put("1ss23", "-12.87");
			jso.put("1ew23", 45.22);
			jso.put("1tt23", -98.18);
			assertEquals(123.0, jso.optDouble("123"));
			assertEquals(-12.0, jso.optDouble("1fd23"));
			assertEquals(45.0, jso.optDouble("1gfd23"));
			assertEquals(-98.0, jso.optDouble("1gg23"));
			assertEquals(123.5, jso.optDouble("1f23"));
			assertEquals(-12.87, jso.optDouble("1ss23"));
			assertEquals(45.22, jso.optDouble("1ew23"));
			assertEquals(-98.18, jso.optDouble("1tt23"));
			assertEquals(Double.NaN, jso.optDouble("rabbit"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetLong() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			jso.put("12gh3", "-12");
			jso.put("1f23", 45L);
			jso.put("1re23", -98L);
			assertEquals(123, jso.getLong("123"));
			assertEquals(-12, jso.getLong("12gh3"));
			assertEquals(45, jso.getLong("1f23"));
			assertEquals(-98, jso.getLong("1re23"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetLong_NonLong() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "abc");
			jso.getLong("123");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"123\"] is not a long.", ex.getMessage());
		}
	}

	public void testOptLong() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			jso.put("12gh3", "-12");
			jso.put("1f23", 45L);
			jso.put("1re23", -98L);
			assertEquals(123, jso.optLong("123"));
			assertEquals(-12, jso.optLong("12gh3"));
			assertEquals(45, jso.optLong("1f23"));
			assertEquals(-98, jso.optLong("1re23"));
			assertEquals(0, jso.optLong("randomString"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			jso.put("12gf3", "-12");
			jso.put("1fg23", "abc");
			jso.put("1d23", "123");
			assertEquals("123", jso.getString("123"));
			assertEquals("-12", jso.getString("12gf3"));
			assertEquals("abc", jso.getString("1fg23"));
			assertEquals("123", jso.getString("1d23"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetString_NonString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", 123);
			jso.getString("123");
		} catch (JSONException ex) {
			assertEquals("JSONObject[\"123\"] not a string.", ex.getMessage());
		}
	}

	public void testOptString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			jso.put("12gf3", "-12");
			jso.put("1fg23", "abc");
			jso.put("1d23", "123");
			assertEquals("123", jso.optString("123"));
			assertEquals("-12", jso.optString("12gf3"));
			assertEquals("abc", jso.optString("1fg23"));
			assertEquals("123", jso.optString("1d23"));
			assertEquals("", jso.optString("pandora"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testOptJSONObject_SimpleObject() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", new JSONObject().put("abc", "123"));
			assertEquals("{\"abc\":\"123\"}", jso.optJSONObject("123").toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testOptJSONObject_NonJsonObject() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			assertEquals(null, jso.optJSONObject("123"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testOptJSONArray() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", new JSONArray().put("abc"));
			assertEquals("[\"abc\"]", jso.optJSONArray("123").toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testOptJSONArray_NonJsonArray() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			assertEquals(null, jso.optJSONArray("123"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testHas() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("123", "123");
			assertTrue(jso.has("123"));
			assertFalse(jso.has("124"));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testAppend() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("string", "123");
			jso.put("jsonarray", new JSONArray().put("abc"));
			jso.append("jsonarray", "123");
			jso.append("george", "def");
			try {
				jso.append("string", "abc");
				fail("Should have thrown exception.");
			} catch (JSONException ex) {
				assertEquals("JSONObject[string] is not a JSONArray.", ex.getMessage());
			}
			assertEquals(
					"{\"george\":[\"def\"],\"string\":\"123\",\"jsonarray\":[\"abc\",\"123\"]}",
					jso.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testConstuctor_Strings() {
		try {
			new JSONObject("123");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("A JSONObject text must begin with '{' at 1 [character 2 line 1]", ex.getMessage());
		}

		try {
			new JSONObject("{\"123\":34");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("Expected a ',' or '}' at 10 [character 11 line 1]", ex.getMessage());
		}

		try {
			new JSONObject("{\"123\",34}");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("Expected a ':' after a key at 7 [character 8 line 1]", ex.getMessage());
		}

		try {
			new JSONObject("{");
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("A JSONObject text must end with '}' at 2 [character 3 line 1]", ex.getMessage());
		}
	}

	public static void testTestValidity() {
		try {
			JSONObject.testValidity(null);
			JSONObject.testValidity(50.4);
			JSONObject.testValidity(70.8);
			JSONObject.testValidity(50.4f);
			JSONObject.testValidity(70.8f);

			try {
				JSONObject.testValidity(Double.NaN);
				fail("Should have throw exception.");
			} catch (JSONException ex) {
				assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
			}

			try {
				JSONObject.testValidity(Double.NEGATIVE_INFINITY);
				fail("Should have throw exception.");
			} catch (JSONException ex) {
				assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
			}

			try {
				JSONObject.testValidity(Double.POSITIVE_INFINITY);
				fail("Should have throw exception.");
			} catch (JSONException ex) {
				assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
			}

			try {
				JSONObject.testValidity(Float.NaN);
				fail("Should have throw exception.");
			} catch (JSONException ex) {
				assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
			}

			try {
				JSONObject.testValidity(Float.NEGATIVE_INFINITY);
				fail("Should have throw exception.");
			} catch (JSONException ex) {
				assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
			}

			try {
				JSONObject.testValidity(Float.POSITIVE_INFINITY);
				fail("Should have throw exception.");
			} catch (JSONException ex) {
				assertEquals("JSON does not allow non-finite numbers.", ex.getMessage());
			}
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testNames() {
		try {
			JSONObject jso = new JSONObject();
			assertEquals(null, jso.names());
			jso.put("abc", "123");
			assertEquals("[\"abc\"]", jso.names().toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testConstructor_CopySubset() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "123");
			jso.put("abcd", "1234");
			jso.put("abcde", "12345");
			jso.put("abcdef", "123456");
			assertEquals("{\"abc\":\"123\",\"abcde\":\"12345\"}",
					new JSONObject(jso, new String[]
							{
									"abc", "abc", "abcde"
							}).toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testPutOnce() {
		try {
			JSONObject jso = new JSONObject();
			jso.putOnce("abc", "123").putOnce("abcd", "1234").putOnce(null, "12345").putOnce("abcdef", null);
			assertEquals("{\"abc\":\"123\",\"abcd\":\"1234\"}", jso.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testToJsonArray() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "123");
			jso.put("abcd", "1234");
			jso.put("abcde", "12345");
			jso.put("abcdef", "123456");
			assertEquals("[\"123\",\"123\",\"12345\"]",
					jso.toJSONArray(new JSONArray(new String[]
							{
									"abc", "abc", "abcde"
							})).toString());
			assertEquals(null, jso.toJSONArray(new JSONArray()));
			assertEquals(null, jso.toJSONArray(null));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testValueToString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("abc", "123");
			jso.put("abcd", "1234");
			jso.put("acd", new GoodJsonString());
			NullJsonString q = new NullJsonString();
			jso.put("zzz", q);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("abc", "123");
			Collection<String> stringCol = new Stack<String>();
			stringCol.add("string1");
			jso.put("de", map);
			jso.put("ex", stringCol);
			jso.put("abcde", new JSONArray().put("123"));
			jso.put("abcdef", new JSONObject().put("123", "123456"));
			JSONObject nulljo = null;
			JSONArray nullja = null;
			jso.put("bcde", nulljo);
			jso.put("bcdef", nullja);
			assertEquals("{\n" +
					"    \"de\": {\"abc\": \"123\"},\n" +
					"    \"ex\": [\"string1\"],\n" +
					"    \"abc\": \"123\",\n" +
					"    \"zzz\": \"" + q.toString() + "\",\n" +
					"    \"abcdef\": {\"123\": \"123456\"},\n" +
					"    \"abcde\": [\"123\"],\n" +
					"    \"acd\": jsonstring,\n" +
					"    \"abcd\": \"1234\"\n" +
					"}", jso.toString(4));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public void testValueToString_Object() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("abcd", "1234");
			Collection<String> stringCol = new Stack<String>();
			stringCol.add("string1");
			JSONObject jso = new JSONObject();
			jso.put("abc", "123");
			assertEquals("{\"abc\":\"123\"}", JSONObject.valueToString(jso));
			assertEquals("{\"abcd\":\"1234\"}", JSONObject.valueToString(map));
			assertEquals("[\"string1\"]", JSONObject.valueToString(stringCol));

			try {
				JSONObject.valueToString(new BadJsonString());
				fail("Should have thrown exception.");
			} catch (JSONException ex) {
				assertEquals("1", ex.getMessage());
			}

			try {
				JSONObject.valueToString(new NullJsonString());
				fail("Should have thrown exception.");
			} catch (JSONException ex) {
				assertEquals("Bad value from toJSONString: null", ex.getMessage());
			}

			assertEquals("jsonstring", JSONObject.valueToString(new GoodJsonString()));
			assertEquals("null", JSONObject.valueToString(null));
			assertEquals("null", JSONObject.valueToString(JSONObject.NULL));
			assertEquals("[\"abc\",\"123\"]",
					JSONObject.valueToString(new String[]
							{
									"abc", "123"
							}));
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}

	public static void testDoubleToString() {
		assertEquals("10.66", JSONObject.doubleToString(10.66));
		assertEquals("10", JSONObject.doubleToString(10));
		assertEquals("null", JSONObject.doubleToString(Double.NaN));
		assertEquals("null", JSONObject.doubleToString(Double.NEGATIVE_INFINITY));
		assertEquals("null", JSONObject.doubleToString(Double.POSITIVE_INFINITY));
		assertEquals("1.0E89", JSONObject.doubleToString(10e88));
		assertEquals("1.0E89", JSONObject.doubleToString(10E88));
	}
}