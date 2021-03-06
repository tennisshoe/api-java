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

	/**
	 * Build and return an URL
	 * Add URI, Parameters, Signature and Format
	 * @param domainToolsRequest
	 * @return the URL's request
	 */	
	public static URL buildURL(DTRequest domainToolsRequest){
		String uri = getURI(domainToolsRequest);
		uri = DTConstants.PATH+"/"+uri;
		String stringUrl = "";
		Boolean parameters = false;
		if(domainToolsRequest.getDomainTools().isUseFreeAPI()) stringUrl = DTConstants.SCHEME+DTConstants.HOSTFREE+uri;
		else stringUrl = DTConstants.SCHEME+DTConstants.HOST+uri;		
		stringUrl.concat("?");
		//If the user want to use the signed authentication
		if(domainToolsRequest.getDomainTools().isSigned()) stringUrl = addSignature(domainToolsRequest, uri, stringUrl);
		else stringUrl = addUserNameAndKey(domainToolsRequest, stringUrl);
		stringUrl = addParameters(domainToolsRequest, stringUrl);
		stringUrl = addResponseFormat(domainToolsRequest, stringUrl);
		URL url = null;
		try {
			url  = new URL(stringUrl);
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
		if(domainToolsRequest.getProduct().isEmpty()) uri = domainToolsRequest.getDomain();
		else{
			if(domainToolsRequest.getProduct().equals("whois") || domainToolsRequest.getProduct().equals("whois/live") || domainToolsRequest.getProduct().equals("whois/history") ||
					domainToolsRequest.getProduct().equals("hosting-history") || domainToolsRequest.getProduct().equals("reverse-ip") ||
					domainToolsRequest.getProduct().equals("name-server-domains"))
				uri = domainToolsRequest.getDomain()+"/"+domainToolsRequest.getProduct();
			else uri = domainToolsRequest.getProduct();
		}
		return uri;
	}

	/**
	 * Add parameters to current URL
	 * @param domainToolsRequest
	 * @return 
	 */
	private static String addParameters(DTRequest domainToolsRequest, String stringUrl){
		//String parameters
		if(!domainToolsRequest.getParameters().isEmpty())
			stringUrl = stringUrl.concat("&"+domainToolsRequest.getParameters());
		//Map parameters
		if(domainToolsRequest.getParametersMap().size() > 0){
			//If parameters is already in the url
			Set<String> keys = domainToolsRequest.getParametersMap().keySet();
			Iterator<String> it = keys.iterator();
			while(it.hasNext()){
				String key = it.next();
				String value = (String) domainToolsRequest.getParametersMap().get(key);
				stringUrl = stringUrl.concat("&"+key+"="+value);
			}
		}
		return stringUrl;
	}

	/**
	 * Add username and key to the URL.<br/>
	 * It allow to do request without using hashed message authentication
	 * @param domainToolsRequest
	 * @return 
	 */
	private static String addUserNameAndKey(DTRequest domainToolsRequest, String stringUrl) {
		return stringUrl.concat("?api_username="+domainToolsRequest.getDomainTools().getApiUsername()+"&api_key="+domainToolsRequest.getDomainTools().getApiKey());
	}

	/**
	 * Add hashed message authentication code to the URL's request
	 * @param domainToolsRequest
	 * @param uri
	 * @return 
	 */
	private static String addSignature(DTRequest domainToolsRequest, String uri, String stringUrl){
		try {
			DTSigner signer = new DTSigner(domainToolsRequest.getDomainTools().getApiUsername(), 
					domainToolsRequest.getDomainTools().getApiKey());
			String timestamp = signer.timestamp();
			String signature = signer.sign(timestamp, uri);
			stringUrl = stringUrl.concat("?api_username="+domainToolsRequest.getDomainTools().getApiUsername()+
					"&timestamp="+timestamp+"&signature="+signature);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return stringUrl;
	}

	/**
	 * Add response's format to the URL's request
	 * By default, we use the json format
	 * @param domainToolsRequest
	 * @return 
	 */
	private static String addResponseFormat(DTRequest domainToolsRequest, String stringUrl){
		if(domainToolsRequest.getFormat().equals(DTConstants.XML)) 
			stringUrl=stringUrl.concat("&"+DTConstants.FORMAT_XML);
		else if(domainToolsRequest.getFormat().equals(DTConstants.HTML)) 
			stringUrl=stringUrl.concat("&"+DTConstants.FORMAT_HTML);
		return stringUrl;
	}

}