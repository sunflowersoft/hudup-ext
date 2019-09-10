package net.hudup.core.parser;

import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

/**
 * This utility class is used to process (read, write, etc.) JSON string, JSON archive (file).
 * JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
 * In this framework, JSON is often used to represent a Java object as a text, which means that JSON text is converted into Java object and vice versa.
 * This class uses library of Cedar Software Company, available at <a href = "https://mvnrepository.com/artifact/com.cedarsoftware/json-io">https://mvnrepository.com/artifact/com.cedarsoftware/json-io</a>. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class JsonParserImpl implements JsonParser {
	
	
	@Override
	public String toJson(Object object) {
		try {
			return JsonWriter.objectToJson(object);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Override
	public boolean toJson(Object object, Writer writer) {
		boolean result = true;
		JsonWriter jsonWriter = null;
		try {
			jsonWriter = new JsonWriter(new WriterOutputStream(writer));
			jsonWriter.write(object);
			jsonWriter.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			
		}
		
		return result;
	}

	
	@Override
	public Object parseJson(String json) {
		try {
			return JsonReader.jsonToJava(json);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Override
	public Object parseJson(Reader reader) {
		Object parsedObject = null;
		JsonReader jsonReader = null;
		try {
			jsonReader = new JsonReader(new ReaderInputStream(reader));
			parsedObject = jsonReader.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			parsedObject = null;
		}
		finally {
			
		}
		
		return parsedObject;
	}

	
//	/**
//	 * Converting specified Java object into JSON string.
//	 * This method uses the GSON library developed by Google for processing JSON format. GSON is available at <a href="https://github.com/google/gson">https://github.com/google/gson</a>.
//	 * This method is current not used.
//	 * @param object Specified Java object.
//	 * @return JSON string
//	 */
//	@SuppressWarnings("unused")
//	@Deprecated
//	private String toPlainJson(Object object) {
//		Gson gson = createBuilder().create();
//		return gson.toJson(object);
//	}
//	
//	
//	/**
//	 * Converting specified Java object into JSON content via {@link Writer}.
//	 * This method uses the GSON library developed by Google for processing JSON format. GSON is available at <a href="https://github.com/google/gson">https://github.com/google/gson</a>.
//	 * This method is current not used.
//	 * @param object Specified Java object
//	 * @param writer {@link Writer} to write JSON content.
//	 * @return whether writing successfully
//	 */
//	@SuppressWarnings("unused")
//	@Deprecated
//	private boolean toPlainJson(Object object, Writer writer) {
//		Gson gson = createBuilder().create();
//		gson.toJson(object, writer);
//		return true;
//	}
//
//	
//	/**
//	 * Converting JSON string into Java object according to specified class type.
//	 * This method uses the GSON library developed by Google for processing JSON format. GSON is available at <a href="https://github.com/google/gson">https://github.com/google/gson</a>.
//	 * This method is current not used.
//	 * @param json Specified JSON string.
//	 * @param type Specified class type.
//	 * @return Java object according specified class type.
//	 */
//	@SuppressWarnings("unused")
//	@Deprecated
//	private Object parsePlainJson(String json, Type type) {
//		try {
//			Gson gson = createBuilder().create();
//			return gson.fromJson(json, type);
//		}
//		catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	
//	/**
//	 * Converting JSON content into Java object by {@link Reader} according to specified class type.
//	 * This method uses the GSON library developed by Google for processing JSON format. GSON is available at <a href="https://github.com/google/gson">https://github.com/google/gson</a>.
//	 * This method is current not used.
//	 * @param reader {@link Reader} to read JSON content.
//	 * @param type Specified class type.
//	 * @return Parsed Java object according class type.
//	 */
//	@SuppressWarnings("unused")
//	@Deprecated
//	private Object parsePlainJson(Reader reader, Type type) {
//		try {
//			Gson gson = createBuilder().create();
//			return gson.fromJson(reader, type);
//		}
//		catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	
//	/**
//	 * Creating Google JSON builder used for all operations (converting JSON content to Java object and vice versa).
//	 * This method uses the GSON library developed by Google for processing JSON format. GSON is available at <a href="https://github.com/google/gson">https://github.com/google/gson</a>.
//	 * This method is current not used.
//	 * Such builder is represented by {@link GsonBuilder} class.
//	 * @return Google JSON builder represented by {@link GsonBuilder} class, used for JSON parsing.
//	 */
//	@Deprecated
//	private static GsonBuilder createBuilder() {
//		return new GsonBuilder().registerTypeAdapter(
//			Map.class,
//			new JsonDeserializer<Map<?, ?>>() {
//
//				@Override
//				public Map<?, ?> deserialize(JsonElement json, Type typeOfT,
//						JsonDeserializationContext context)
//						throws JsonParseException {
//					// TODO Auto-generated method stub
//					
//					Type type = new TypeToken<Map<?, ?>>() { }.getType();
//					Gson gson = new Gson();
//					return gson.fromJson(json, type);
//				}
//				
//			});
//	}
	
	
}
