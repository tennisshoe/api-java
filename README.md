# DomainToolsAPI Java Wrapper #

## Presentation ##

The DomainToolsAPI Java Wrapper is a simple connector to access all webservices of [domaintools.com](http://domaintools.com "domaintools.com").

## Getting started ##

To use the DomainTools API, you need to have a DomainTools Account. [Create and account](https://secure.domaintools.com/join/).

## Installation ###

[Download the jar](http://domain.com/api-java/domaintools.api.client-1.0.0.jar) and add it in your Library.

## Usage ##

1. Visit [domaintools.com](http://www.domaintools.com/api/docs/products/) to know the list of available services.

Beware, certains services (like *reverse-whois*) don't need domains to work, but require parameters to work. (Example *terms=Seattle*). 

Please read attentively how to use each services.

1. When you need to use a DomainTools service :

1. Add import com.domaintoolsapi.*; 

### Create a DomainTools object ### 

```java
DomainTools domainTools = new DomainTools("your_username", "your_key");
```

You can switch to the Free API 
```java
domainTools.setUseFreeAPI(true);
```

### Create a DomainTools request (DTRequest) ###

To execute a request on DomainTools, you need to set the product which you want to use. Visit [domaintools.com](http://www.domaintools.com/api/docs/products/) to know the list of available services.

#### Set a **product** with the method **use** ####

To set a product, call the method **use** with a **String** representation of the the product name 
```java
DTRequest dtRequest = domainTools.use("whois");
```

#### Set a **domain** with the method **on** ####

To set a domain, call the method **on** with a **String** representation of the domain name
```java
dtRequest.on("domaintools.com")
```

#### Set a **format** ####

DomainTools can return responses in diffents formats :
* JSON with **toJSON()**
* XML with **toXML()**
* A Java Object **toObject()** 

#### Set **parameters** ####

Some products need parameters to be executed. To set parameters, call the method **where**.
**where** can use **String** or **Map** argument.
If you use a String argument, keep in my mind you must replace space character by **%20**

```java 
dtRequest.where("limit=10");
```

In this example, we limit to 10 the number of domain names's responses.
We can also use a Map to declare some parameters :

```java 
Map<String, String> parameters = new HashMap<String, String>();
parameters.put("limit", "10");
dtRequest.where(parameters);
```

You can use several time where to add multiples parameters :

```java
dtRequest.where("query=domain%20tools").where("max_length=2").toXML();
```

or 

```java
HashMap<String, String> params = new HashMap<String, String>();
params.put("query", "domain%20tools");
dtRequest.where(params).where("max_length=2");
```

Or use the character "&"

```java
dtRequest.where("query=domain%20tools&max_length=2");
```

But you can't use multples arguments in a single method.

#### Reuse a request ####

If you want to reuse a request with different parameters, you can use **resetParameters()** to erase current parameters.

```java 
dtRequest.resetParameters();
```

If you also want to erase product, domain and format, but not username and key, use **clear()**.

```java 
dtRequest.clear();
```

#### Refresh your results ####

You can refresh your results by using the method **refresh**

```java 
dtRequest.refresh();
```

### Signed Authentication ###

The DomainToolsAPI Java Wrapper allows to use the HMAC (or hashed message authentication code) to protect the API Key.

To use HMAC, add **signed(true)** on your request.

Example :

```java 
domainTools.use("domain-suggestions").where("query=domain%20tools").signed(true).toXML()
```

We recommande to use this authentication scheme to protecting your API key.


### Method chaining ###

The domaintoolsAPI Java Wrapper is a fluent API implemented by using method chaining.
You can combine methods to specify return type, options, etc.:

Example :

```java
domainTools.use("reverse-whois").where("terms=DomainTools%20LLC|Seattle").where("mode=purchase").signed(true).toXML();
```
This request use the reverse-whois services and add terms and mode parameters in signed mode and require a XML format.

### Traversing an Object ###

You can traverse a response object by using the method "get". It returns the child node specified by the parameter name.

Example :

```java 
DTRequest dtRequest = domainTools.use("reverse-ip");
dtRequest.on("nameintel.com").where("limit=2").toJSON();
JsonNode jsonNode = dtRequest.getObject();
Iterator<JsonNode> it = jsonNode.get("response").get("ip_addresses").get("domain_names").getElements();
while(it.hasNext()){
	System.out.println(it.next());
}

```

This example shows how to get all the domain names contains in the response.

## Changelog ##

See the CHANGELOG.md file for details.

## License ##

Copyright (C) 2011 by domaintools.com, DomaintoolsAPI PHP Wrapper is released under the MIT license.

See the LICENSE.md file for details.


