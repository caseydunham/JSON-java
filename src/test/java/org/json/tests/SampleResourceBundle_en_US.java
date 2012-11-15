package org.json.tests;

import java.util.*;

public class SampleResourceBundle_en_US extends ListResourceBundle {

	@Override
	public Object[][] getContents() {
		return contents;
	}

	/** The contents. */
	private Object[][] contents = {
			{ "ASCII", "American Standard Code for Information Interchange" },
			{ "JAVA.desc", "Just Another Vague Acronym" },
			{ "JAVA.data", "Sweet language" },
			{ "JSON", "JavaScript Object Notation" },
	};
}