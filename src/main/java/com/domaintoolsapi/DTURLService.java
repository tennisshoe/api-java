package com.domaintoolsapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

/**
 * This class build URL with all parameters
 * @author Julien SOSIN 
 */
public class DTURLService {

	private static String string_url;
	private static URL url;

	/**
	 * Build and return an URL
	 * Add URI, Parameters, Signature and Format
	 * @param domainToolsRequest
	 * @return the URL's request
	 */	
	public static URL buildURL(DTRequest domainToolsRequest){
		String uri = getURI(domainToolsRequest);
		string_url = DTConstants.SCHEME+DTConstants.HOST+DTConstants.PATH+"/"+uri;
		//If the user want to use the signed authentication
		if(domainToolsRequest.getdomainTools().issigned()) addSignature(domainToolsRequest, uri);
		else addUserNameAndKey(domainToolsRequest);
		addParameters(domainToolsRequest);
		addResponseFormat(domainToolsRequest);
		try {
			url = new URL(string_url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * All products haven't the same URL's structure
	 * So we have to adapt the URL to the request
	 * @param domainToolsRequest
	 * @return the URI (like "/yourdomain.com/whois")
	 */	
	public static String getURI(DTRequest domainToolsRequest){
		String uri;
		if(domainToolsRequest.getproduct().equals("whois") || domainToolsRequest.getproduct().equals("hosting-history") ||
				domainToolsRequest.getproduct().equals("reverse-ip") || domainToolsRequest.getproduct().equals("name-server-domains"))
			uri = domainToolsRequest.getdomain()+"/"+domainToolsRequest.getproduct();
		else uri = domainToolsRequest.getproduct()+"/"+domainToolsRequest.getdomain();
		return uri;
	}

	/**
	 * Add parameters to current URL
	 * @param domainToolsRequest
	 */
	private static void addParameters(DTRequest domainToolsRequest){
		boolean isFirstParameter = true;
		//String parameters
		if(!domainToolsRequest.getparameters().isEmpty())
			string_url = string_url.concat("&"+domainToolsRequest.getparameters());
		//Map parameters
		if(domainToolsRequest.getparameters_map().size() > 0){
			//If parameters is already in the url
			Set<String> keys = domainToolsRequest.getparameters_map().keySet();
			Iterator<String> it = keys.iterator();
			while(it.hasNext()){
				if(!isFirstParameter) string_url = string_url.concat("&");
				isFirstParameter = false;
				String key = it.next();
				String value = (String) domainToolsRequest.getparameters_map().get(key);
				string_url = string_url.concat(key+"="+value);
			}
		}
	}
	
	/**
	 * Add username and key to the URL.<br/>
	 * It allow to do request without using hashed message authentication
	 * @param domainToolsRequest
	 */
	private static void addUserNameAndKey(DTRequest domainToolsRequest) {
		string_url = string_url.concat("?api_username="+domainToolsRequest.getdomainTools().getapi_username()+"&api_key="+domainToolsRequest.getdomainTools().getapi_key());
	}

	/**
	 * Add hashed message authentication code to the URL's request
	 * @param domainToolsRequest
	 * @param uri
	 */
	private static void addSignature(DTRequest domainToolsRequest, String uri){
		try {
			DTSigner signer = new DTSigner(domainToolsRequest.getdomainTools().getapi_username(), 
					domainToolsRequest.getdomainTools().getapi_key());
			String timestamp = signer.timestamp();
			String signature = signer.sign(timestamp, uri);
			string_url = string_url.concat("?api_username="+domainToolsRequest.getdomainTools().getapi_username()+
					"&signature="+signature+"&timestamp="+timestamp);
		} catch(Exception e) {
			System.out.println("Error trying to sign query");
		}
	}

	/**
	 * Add response's format to the URL's request
	 * @param domainToolsRequest
	 */
	private static void addResponseFormat(DTRequest domainToolsRequest){
		if(domainToolsRequest.getformat().equals(DTConstants.XML)) 
			string_url=string_url.concat("&"+DTConstants.FORMAT_XML);
		else if(domainToolsRequest.getformat().equals(DTConstants.HTML)) 
			string_url=string_url.concat("&"+DTConstants.FORMAT_HTML);
	}
}