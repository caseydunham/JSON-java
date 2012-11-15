package org.json.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
	TestJSONArray.class,
	TestJSONException.class,
	TestJSONObject.class,
	TestJSONStringer.class,
	TestJSONTokener.class,
TestJSONWriter.class })
public class TestSuite {
	// nothing
}