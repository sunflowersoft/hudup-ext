/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.client.Protocol;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;

/**
 * This utility class provides utility methods for operators relevant to HTTP protocol such as processing HTTP request/response and creating HTTP header.
 * Note, HTTP, which is the abbreviation of Hypertext Transfer Protocol, is the popular network protocol. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class HttpUtil {

	
	/**
	 * Extracting parameters from the specified request in text form. Such parameters are stored in the returned map.
	 * @param requestText specified request in text form.
	 * @return map of parameters.
	 * Header of an sample request in text form &quot;http://www.hudup.net/project/recommend?parameter1=value1&amp;parameter2=value2&quot; is shown as follow:
	 * <br><br>
	 * <code>
	 * GET /project/recommend?parameter1=value1&amp;parameter2=value2 HTTP/1.1<br>
	 * Host: www.hudup.net
	 * </code>
	 * <br><br>
	 * In this example, this method will returns the map having two entries: (parameter1, value1) and (parameter2, value2).
	 */
	public static Map<String, String> getParameters(String requestText) {
		try {
			String content = getContent(requestText);
			if (content == null)
				return null;
			
			int mark = content.indexOf('?');
			if (mark == -1)
				return Util.newMap();
			
			return xURI.parseParameterText(content.substring(mark + 1));
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			return Util.newMap();
		}
	}
	
	
	/**
	 * Getting the action of specified request in text form.
	 * @param requestText specified request in text form.
	 * @return action of specified request in text form.
	 * Header of an sample request in text form &quot;http://www.hudup.net/project/recommend?parameter1=value1&amp;parameter2=value2&quot; is shown as follow:
	 * <br><br>
	 * <code>
	 * GET /project/recommend?parameter1=value1&amp;parameter2=value2 HTTP/1.1<br>
	 * Host: www.hudup.net
	 * </code>
	 * <br><br>
	 * In this example, the action returned by this method is &quot;recommend&quot;.
	 */
	public static String getAction(String requestText) {
		String action = null;
		try {
			String path = getPath(requestText);
			if (path == null)
				return null;
			
			int index = path.lastIndexOf("/");
			if (index == -1)
				action = path;
			else
				action = path.substring(index + 1);
			
			action = action.trim();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			return null;
		}
		
		if (action == null || action.isEmpty())
			return null;
		return action;
	}
	
	
	/**
	 * Getting the path of specified request in text form.
	 * @param requestText specified request in text form.
	 * @return path of specified request in text form.
	 * Header of an sample request in text form &quot;http://www.hudup.net/project/recommend?parameter1=value1&amp;parameter2=value2&quot; is shown as follow:
	 * <br><br>
	 * <code>
	 * GET /project/recommend?parameter1=value1&amp;parameter2=value2 HTTP/1.1<br>
	 * Host: www.hudup.net
	 * </code>
	 * <br><br>
	 * In this example, the path return by this method is &quot;project/recommend&quot;.
	 */
	public static String getPath(String requestText) {
		String content = getContent(requestText);
		if (content == null)
			return null;
		
		try {
			int mark = content.indexOf('?');
			
			String path = null;
			if (mark == -1)
				path = content;
			else
				path = content.substring(0, mark).trim();
			
			if (path.startsWith("/"))
				path = path.substring(1);
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			
			path = path.trim();
			if (path.isEmpty())
				return null;
			else 
				return path;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			return null;
		}
	}
	
	
	/**
	 * Getting the content of specified request in text form.
	 * @param requestText specified request in text form.
	 * @return content of specified request in text form.
	 * Header of an sample request in text form &quot;http://www.hudup.net/project/recommend?parameter1=value1&amp;parameter2=value2&quot; is shown as follow:
	 * <br><br>
	 * <code>
	 * GET /project/recommend?parameter1=value1&amp;parameter2=value2 HTTP/1.1<br>
	 * Host: www.hudup.net
	 * </code>
	 * <br><br>
	 * In this example, the content returned by this method is &quot;/project/recommend?parameter1=value1&amp;parameter2=value2&quot;.
	 */
	public static String getContent(String requestText) {
		if (!requestText.toUpperCase().startsWith("GET"))
			return null;
		
		try {
			int start = requestText.indexOf(' ');
			int end = requestText.indexOf(' ', start + 1);
			String content = requestText.substring(start + 1, end);
			content = content.trim();
			
			if (content.isEmpty())
				return null;
			else 
				return content;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			return null;
		}
	}
	
	
	/**
	 * Creating the HTTP header of the response for the previous request.
	 * @param return_code specified returned code such as 200 (successful response), 400 (bad request), 403 (forbidden), 404 (not found), 500 (Internal Server Error), 501 (not implemented).
	 * @param file_type type of the file which is returned to client, for example: application/json, text/html, image/jpeg, image/gif, text/plain, application/x-zip-compressed.
	 * @return HTTP header of the response.
	 * Following is an example of response header:
	 * <br><br>
	 * <code>
	 * HTTP/1.0 200 OK<br>
	 * Connection: close<br>
	 * Server: Hudup listener v6<br>
	 * Content-Type: application/json
	 * </code>
	 */
	public static String createResponseHeader(int return_code, int file_type) {
		String header_s = "HTTP/1.0 ";
		String error_code = null;
		switch (return_code) {
			case 200:
				header_s = header_s + "200 OK";
				break;
			case 400:
				header_s = header_s + "400 Bad Request";
				error_code = "error.code.400";
				break;
			case 403:
				header_s = header_s + "403 Forbidden";
				error_code = "error.code.403";
				break;
			case 404:
				header_s = header_s + "404 Not Found";
				error_code = "error.code.404";
				break;
			case 500:
				header_s = header_s + "500 Internal Server Error";
				error_code = "error.code.500";
				break;
			case 501:
				header_s = header_s + "501 Not Implemented";
				error_code = "error.code.501";
				break;
		}
		
		header_s = header_s + "\r\n"; //other header fields,
		header_s = header_s + "Connection: close\r\n"; //we can't handle persistent connections
		header_s = header_s + "Server: Hudup listener v6\r\n"; //server name

		switch (file_type) {
			case Protocol.UNKNOWN_FILE_TYPE:
				header_s = header_s + "Content-Type: application/json\r\n";
				break;
			case Protocol.HTML_FILE_TYPE:
				header_s = header_s + "Content-Type: text/html\r\n";
				break;
			case Protocol.JPEG_FILE_TYPE:
				header_s = header_s + "Content-Type: image/jpeg\r\n";
				break;
			case Protocol.GIF_FILE_TYPE:
				header_s = header_s + "Content-Type: image/gif\r\n";
				break;
			case Protocol.TEXTPLAIN_FILE_TYPE:
				header_s = header_s + "Content-Type: text/plain\r\n";
				break;
			case Protocol.JSON_FILE_TYPE:
				header_s = header_s + "Content-Type: application/json\r\n";
				break;
			case Protocol.XZIP_FILE_TYPE:
				header_s = header_s + "Content-Type: application/x-zip-compressed\r\n";
				break;
			default:
				header_s = header_s + "Content-Type: text/html\r\n";
				break;
		}

		header_s = header_s + "\r\n"; //this marks the end of the http header
		
		if(error_code != null)
			header_s = header_s + error_code;
		return header_s;
	}
	
	
	/**
	 * Getting the file type inside the request in text form.
	 * @param requestText specified request in text form.
	 * @return file type inside the request in text form.
	 * Header of an sample request in text form &quot;http://www.hudup.net/project/recommend/recommended-list.json&quot; is shown as follow:
	 * <br><br>
	 * <code>
	 * GET /project/recommend/recommended-list.json HTTP/1.1<br>
	 * Host: www.hudup.net
	 * </code>
	 * <br><br>
	 * In this example, the file type returned by this method &quot;json&quot;.
	 */
	public static int getFileType(String requestText) {
		int type_is = Protocol.HTML_FILE_TYPE;
		String path = HttpUtil.getPath(requestText);
		
		if (path.indexOf('.') == -1) {
			type_is = Protocol.UNKNOWN_FILE_TYPE;
		}
		else if (path.endsWith(".htm") || path.endsWith(".html") || path.endsWith(".xhtml"))
			type_is = Protocol.HTML_FILE_TYPE;
		
		else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
			type_is = Protocol.JPEG_FILE_TYPE;
		
		else if (path.endsWith(".gif"))
			type_is = Protocol.GIF_FILE_TYPE;
		
		else if (path.endsWith(".txt"))
			type_is = Protocol.TEXTPLAIN_FILE_TYPE;
		
		else if (path.endsWith(".json"))
			type_is = Protocol.JSON_FILE_TYPE;
		
		else if (path.endsWith(".exe") || path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar"))
			type_is = Protocol.XZIP_FILE_TYPE;
		
		return type_is;
	}
	

}
