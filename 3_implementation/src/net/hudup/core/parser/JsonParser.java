/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import java.io.Reader;
import java.io.Writer;

/**
 * This utility class is used to process (read, write, etc.) JSON string, JSON archive (file).
 * JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
 * In this framework, JSON is often used to represent a Java object as a text, which means that JSON text is converted into Java object and vice versa.
 * This interface defines method to process JSON format.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface JsonParser {
	
	
	/**
	 * Converting Java object into JSON string.
	 * @param object Specified Java object.
	 * @return JSON string.
	 */
	String toJson(Object object);
	
	
	/**
	 * Writing Java object to storage according JSON format by {@link Writer}.
	 * @param object Java object.
	 * @param writer {@link Writer} for writing.
	 * @return whether writing successfully
	 */
	boolean toJson(Object object, Writer writer);

	
	/**
	 * Converting specified JSON string into Java object.
	 * @param json Specified JSON string.
	 * @return Converted Java object.
	 */
	Object parseJson(String json);
	
	
	/**
	 * Parsing JSON-format content into Java object by {@link Reader}.
	 * @param reader {@link Reader} of JSON-format content.
	 * @return Parsed Java object.
	 */
	Object parseJson(Reader reader);


}
