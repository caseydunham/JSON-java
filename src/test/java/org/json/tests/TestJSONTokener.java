package org.json.tests;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import junit.framework.TestCase;

public class TestJSONTokener extends TestCase {

	class MockInputStreamThrowsExceptionOnFourthRead extends InputStream {

		int position = 0;

		@Override
		public int read() throws IOException {
			if (position < 3)
				position++;
			else
				throw new IOException("Mock IOException thrown from read");
			return 'a';
		}
	}

	class MockInputStreamThrowsExceptionOnReset extends BufferedReader {

		public MockInputStreamThrowsExceptionOnReset(Reader in) {
			super(in);
		}

		@Override
		public int read() throws IOException {
			return 0;
		}

		@Override
		public void reset() throws IOException {
			throw new IOException("Mock IOException thrown from reset");
		}
	}

	public void testConstructor_InputStream() {
		byte[] buf;
		String string = "{\"abc\":\"123\"}";
		buf = string.getBytes();
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		try {
			JSONTokener jsontokener = new JSONTokener(is);
			assertEquals('{', jsontokener.next());
			assertEquals("abc", jsontokener.nextValue());
			assertEquals(':', jsontokener.next());
			assertEquals("123", jsontokener.nextValue());
			assertEquals('}', jsontokener.next());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testBack() {
		byte[] buf;
		String string = "{\"abc\":\"123\"}";
		buf = string.getBytes();
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		try {
			JSONTokener jsontokener = new JSONTokener(is);
			assertEquals('{', jsontokener.next());
			assertEquals("abc", jsontokener.nextValue());
			assertEquals(':', jsontokener.next());
			jsontokener.back();
			assertEquals(':', jsontokener.next());
			assertEquals("123", jsontokener.nextValue());
			assertEquals('}', jsontokener.next());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testBack_FailsIfUsedTwice() {
		byte[] buf;
		String string = "{\"abc\":\"123\"}";
		buf = string.getBytes();
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		try {
			JSONTokener jsontokener = new JSONTokener(is);
			assertEquals('{', jsontokener.next());
			assertEquals("abc", jsontokener.nextValue());
			assertEquals(':', jsontokener.next());
			jsontokener.back();
			jsontokener.back();
		} catch (JSONException ex) {
			assertEquals("Stepping back two steps is not supported", ex.getMessage());
		}
	}

	public void testNext_FakeInputStreamToTestIoexception() {
		try {
			JSONTokener jsontokener = new JSONTokener(new MockInputStreamThrowsExceptionOnFourthRead());
			assertEquals('a', jsontokener.next());
			assertEquals('a', jsontokener.next());
			assertEquals('a', jsontokener.next());
			assertEquals('a', jsontokener.next());
			assertEquals('a', jsontokener.next());
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("Mock IOException thrown from read", ex.getMessage());
		}
	}

	public void testNext_EmptyStream() {
		byte[] buf;
		String string = "";
		buf = string.getBytes();
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		try {
			JSONTokener jsontokener = new JSONTokener(is);
			assertEquals(0, jsontokener.next());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNext_LineIncrementsOnNewLine() {
		JSONTokener jsontokener = new JSONTokener("abc\n123");
		try {
			jsontokener.next();//a
			jsontokener.next();//b
			jsontokener.next();//c
			assertEquals(" at 3 [character 4 line 1]", jsontokener.toString());
			jsontokener.next();//\n
			assertEquals(" at 4 [character 0 line 2]", jsontokener.toString());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNext_LineIncrementsOnCarriageReturn() {
		JSONTokener jsontokener = new JSONTokener("abc\r123");
		try {
			jsontokener.next();//a
			jsontokener.next();//b
			jsontokener.next();//c
			assertEquals(" at 3 [character 4 line 1]", jsontokener.toString());
			jsontokener.next();//\r
			jsontokener.next();//1
			assertEquals(" at 5 [character 1 line 2]", jsontokener.toString());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNext_LineIncrementsOnCarriageReturnAndNewLine() {
		JSONTokener jsontokener = new JSONTokener("abc\r\n123");
		try {
			jsontokener.next();//a
			jsontokener.next();//b
			jsontokener.next();//c
			assertEquals(" at 3 [character 4 line 1]", jsontokener.toString());
			jsontokener.next();//\r
			jsontokener.next();//\n
			assertEquals(" at 5 [character 0 line 2]", jsontokener.toString());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testSkipTo() {
		byte[] buf;
		String string = "{\"abc\":\"123\",\"wer\":\"rty\"}";
		buf = string.getBytes();
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		try {
			JSONTokener jsontokener = new JSONTokener(is);
			assertEquals('{', jsontokener.next());
			assertEquals("abc", jsontokener.nextValue());
			assertEquals(':', jsontokener.next());
			assertEquals("123", jsontokener.nextValue());
			assertEquals(',', jsontokener.next());
			assertEquals(0, jsontokener.skipTo('g'));
			assertEquals('"', jsontokener.next());
			assertEquals('t', jsontokener.skipTo('t'));
			assertEquals('t', jsontokener.next());
			assertEquals('y', jsontokener.next());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testSkipTo_FakeInputStreamToTestIoexception() {
		JSONTokener jsontokener = new JSONTokener(new MockInputStreamThrowsExceptionOnReset(new StringReader("123")));
		try {
			jsontokener.skipTo('l');
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("Mock IOException thrown from reset", ex.getMessage());
		}
	}

	public void testEnd() {
		JSONTokener jsontokener = new JSONTokener("a");
		try {
			assertFalse(jsontokener.end());
			jsontokener.next();
			jsontokener.next();
			assertTrue(jsontokener.end());
			jsontokener.back();
			assertFalse(jsontokener.end());
			jsontokener.next();
			jsontokener.next();
			assertTrue(jsontokener.end());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testMore() {
		JSONTokener jsontokener = new JSONTokener("a");
		try {
			assertTrue(jsontokener.more());
			jsontokener.next();
			assertFalse(jsontokener.more());
			jsontokener.back();
			assertTrue(jsontokener.more());
			jsontokener.next();
			assertFalse(jsontokener.more());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextValue_NiceString() {
		JSONTokener jsontokener = new JSONTokener("abc");
		try {
			assertEquals("abc", jsontokener.nextValue());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextValue_StringWithNewLine() {
		JSONTokener jsontokener = new JSONTokener("abc\n123");
		try {
			assertEquals("abc", jsontokener.nextValue());
			assertEquals(123, jsontokener.nextValue());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextValue_JsonObjectString() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("abc", "123");
			JSONTokener jsontokener = new JSONTokener(jo.toString());
			assertEquals(jo.toString(), jsontokener.nextValue().toString());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextValue_IndentedJsonObjectString() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("abc", "123");
			JSONTokener jsontokener = new JSONTokener(jo.toString(4));
			assertEquals(jo.toString(), jsontokener.nextValue().toString());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextValue_JsonArrayString() {
		JSONArray ja = new JSONArray();
		try {
			ja.put("abc");
			ja.put("123");
			JSONTokener jsontokener = new JSONTokener(ja.toString());
			assertEquals(ja.toString(), jsontokener.nextValue().toString());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextValue_IndentedJsonArrayString() {
		JSONArray ja = new JSONArray();
		try {
			ja.put("abc");
			ja.put("123");
			JSONTokener jsontokener = new JSONTokener(ja.toString(4));
			assertEquals(ja.toString(), jsontokener.nextValue().toString());
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextValue_EmptyString() {
		JSONTokener jsontokener = new JSONTokener("");
		try {
			jsontokener.nextValue();
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("Missing value at 0 [character 1 line 1]", ex.getMessage());
		}
	}

	public void testNext_ExpectedChar() {
		JSONTokener jsontokener = new JSONTokener("abc");
		char expectedA = 0;
		try {
			expectedA = jsontokener.next('a');
			jsontokener.next('c');
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals('a', expectedA);
			assertEquals("Expected 'c' and instead saw 'b' at 2 [character 3 line 1]", ex.getMessage());
		}
	}

	public void testNext_ExpectedNumberOfCharacters() {
		JSONTokener jsontokener = new JSONTokener("abc123");
		String expectedAbc = "";
		String expectedBlank = "";
		try {
			expectedAbc = jsontokener.next(3);
			expectedBlank = jsontokener.next(0);
			jsontokener.next(7);
			fail("Should have thrown exception.");
		} catch (JSONException ex) {
			assertEquals("abc", expectedAbc);
			assertEquals("", expectedBlank);
			assertEquals("Substring bounds error at 7 [character 8 line 1]", ex.getMessage());
		}
	}

	public void testNextTo_Character() {
		JSONTokener jsontokener = new JSONTokener("abc123,test\ntestString1\rsecondString\r\nthird String");
		try {
			assertEquals("abc123", jsontokener.nextTo(','));
			jsontokener.next(',');
			assertEquals("test", jsontokener.nextTo(','));
			jsontokener.next('\n');
			assertEquals("testString1", jsontokener.nextTo(','));
			jsontokener.next('\r');
			assertEquals("secondString", jsontokener.nextTo(','));
			jsontokener.next('\r');
			jsontokener.next('\n');
			assertEquals("third String", jsontokener.nextTo(','));
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextTo_String() {
		JSONTokener jsontokener = new JSONTokener("abc123,test\ntestString1\rsecondString\r\nthird String");
		try {
			assertEquals("abc", jsontokener.nextTo("1,"));
			assertEquals("123", jsontokener.nextTo("abc,"));
			jsontokener.next(',');
			assertEquals("te", jsontokener.nextTo("sabc"));
			assertEquals("st", jsontokener.nextTo("ring"));
			jsontokener.next('\n');
			assertEquals("testSt", jsontokener.nextTo("r"));
			assertEquals("ring1", jsontokener.nextTo("qw"));
			jsontokener.next('\r');
			assertEquals("second", jsontokener.nextTo("bhS"));
			assertEquals("String", jsontokener.nextTo("dbh"));
			jsontokener.next('\r');
			jsontokener.next('\n');
			assertEquals("third", jsontokener.nextTo(" ng"));
			assertEquals("String", jsontokener.nextTo("qwhdab"));
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextString() {
		JSONTokener jsontokener = new JSONTokener("'abc'\"1\\\"2\\\"3\"'a\\u1111b\\fc\\trhd\\bdd\\r\\ngghhj'\"hghghgjfjf\"");
		try {
			jsontokener.next('\'');
			assertEquals("abc", jsontokener.nextString('\''));
			jsontokener.next('"');
			assertEquals("1\"2\"3", jsontokener.nextString('"'));
			jsontokener.next('\'');
			assertEquals("a\u1111b\fc\trhd\bdd\r\ngghhj", jsontokener.nextString('\''));
			jsontokener.next('"');
			assertEquals("hghghgjfjf", jsontokener.nextString('"'));
		} catch (JSONException ex) {
			fail(ex.toString());
		}
	}

	public void testNextString_IllegalEscape() {
		JSONTokener jsontokener = new JSONTokener("'ab\\\tc'");
		try {
			jsontokener.next('\'');
			jsontokener.nextString('\'');
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("Illegal escape. at 5 [character 6 line 1]", ex.getMessage());
		}
	}

	public void testNextString_UnterminatedString() {
		JSONTokener jsontokener = new JSONTokener("'abc");
		try {
			jsontokener.next('\'');
			jsontokener.nextString('\'');
			fail("Should have thrown exception");
		} catch (JSONException ex) {
			assertEquals("Unterminated string at 5 [character 6 line 1]", ex.getMessage());
		}
	}

	public static void testDehexChar() {
		char i = '0';
		int j = 0;
		while (i <= '9') {
			assertEquals(j, JSONTokener.dehexchar(i));
			i++;
			j++;
		}
		i = 'A';
		while (i <= 'F') {
			assertEquals(j, JSONTokener.dehexchar(i));
			i++;
			j++;
		}
		j = 10;
		i = 'a';
		while (i <= 'f') {
			assertEquals(j, JSONTokener.dehexchar(i));
			i++;
			j++;
		}
		assertEquals(-1, JSONTokener.dehexchar('$'));
		assertEquals(-1, JSONTokener.dehexchar('g'));
		assertEquals(-1, JSONTokener.dehexchar('G'));
		assertEquals(-1, JSONTokener.dehexchar('z'));
		assertEquals(-1, JSONTokener.dehexchar('Z'));
	}

	public void testMultipleThings() {
		try {
			JSONTokener jsontokener = new JSONTokener(
					"{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
			JSONObject jsonobject = new JSONObject(jsontokener);
			assertEquals("{\"to\":\"session\",\"op\":\"test\",\"pre\":1}", jsonobject.toString());
			assertEquals(1, jsonobject.optInt("pre"));
			int i = jsontokener.skipTo('{');
			assertEquals(123, i);
			jsonobject = new JSONObject(jsontokener);
			assertEquals("{\"to\":\"session\",\"op\":\"test\",\"pre\":2}", jsonobject.toString());
		} catch (JSONException ex) {
			fail(ex.getMessage());
		}
	}
}