# Using Imported Stormpath API Keys with Okta

This example is for a specific use case wherein:

1. You've imported Stormpath API Keys into Okta
2. You're using one of the integrations from the Stormpath Java SDK against Okta (Spring Boot, Spring or Servlet)

*Note*: This is intended as a stop-gap for Stormpath customers migrating to Okta. 
After August 17th, the Okta version of the Stormpath Java SDK will no longer be supported or have any future releases.

## Setup

In order to use the Okta version of the Stormpath Java SDK, you'll need to depend on version: `2.0.0-okta`. For example:

```
...
	<dependencies>
		<dependency>
			<groupId>com.stormpath.spring</groupId>
			<artifactId>stormpath-default-spring-boot-starter</artifactId>
		</dependency>
	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.stormpath.sdk</groupId>
				<artifactId>stormpath-bom</artifactId>
				<version>2.0.0-okta</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
...
```  

You'll also need to have the following properties set:

| property                 | description                                                          |
|--------------------------|----------------------------------------------------------------------|
| STORMPATH_CLIENT_BASEURL | URL of your OKTA tenant                                              |
| OKTA_API_TOKEN           | API Token from the Okta Admin console; Security -> API -> Tokens tab |
| OKTA_APPLICATION_ID      | Application ID from Okta Admin console                               |

## Running the Example

This example is a Spring Boot with Spring Security application. You can build and run it like so:

```
mvn clean install

STORMPATH_CLIENT_BASEURL=<okta tenant url> \
OKTA_API_TOKEN=<okta api token> \
OKTA_APPLICATION_ID=<okta oidc app id> \
java -jar target/client-credentials-example-0.0.1-SNAPSHOT.jar
```

## Use

Okta does not currently have support for API Keys. The workaround for existing Stormpath API keys imported into Okta is to do an 
OAuth2 client credentials flow. You'll use an api key pair in the basic authorization header and get back an
access token. The access token can then be used to hit protected resources.

Behind the scenes, the Java SDK does a search using the Okta API against profile schema attributes
`stormpathApiKey_1`..`stormpathApiKey_10`. If a match is found, an access token is generated.

Here's an example interaction using [HTTPie](httpie.org):

```
http --auth <Stormpath API Key ID>:<Stormpath API Key Secret> \
-f POST localhost:8080/oauth/token \
grant_type=client_credentials
```

The response will look something like this:

```
HTTP/1.1 200
...
{
    "access_token": "eyJncmFudFR5cGUiOiJzcF9jbGllbnRfY3JlZGVudGlhbHMiLCJhbGciOiJIUzUxMiJ9...",
    "expires_in": 3600,
    "id_token": "sp_client_credentials:MDk2NDhlMzAtZjdmMi00ZmFmLWJhMWEtYTkxZmYzYTI2NGZl",
    "token_type": "Bearer"
}
```

The returned access token can then be used as a bearer token to hit protected endpoints. In this example, the `/info` endpoint is protected:

```
http localhost:8080/info \
Authorization:"Bearer eyJncmFudFR5cGUiOiJzcF9jbGllbnRfY3JlZGVudGlhbHMiLCJhbGciOiJIUzUxMiJ9..."
```

The response looks like:

```
HTTP/1.1 200
...
"Micah Silverman" <micah.silverman@okta.com>
```