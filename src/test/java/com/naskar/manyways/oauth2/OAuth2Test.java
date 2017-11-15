package com.naskar.manyways.oauth2;

import java.util.UUID;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.naskar.manyways.base.EmbeddedServerTestBase;

// This tests need the user interaction
public class OAuth2Test extends EmbeddedServerTestBase {

	//@Test
	public void oauthGoogle() throws Exception {
		createServlet("/app/valid", "OK");
		createServlet("/app/autorized", (req, res) -> {

			try {
				OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(req);
				String code = oar.getCode();

				OAuthClientRequest request = OAuthClientRequest
	                .tokenLocation("https://www.googleapis.com/oauth2/v4/token")
	                .setGrantType(GrantType.AUTHORIZATION_CODE)
	                .setClientId("530011801167-8n1afrtec2q7mac6o8po5mq782f4ve84.apps.googleusercontent.com")
	                .setClientSecret("OMx6F9amqqrLtDuhcy96mOuE")
	                .setRedirectURI(getServerUrl() + "/app/autorized")
	                .setCode(code)
	                .setParameter("access_type", "offline")
	                .buildBodyMessage()
	                ;
				
				OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
				 
				OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);
	 
	            String accessToken = oAuthResponse.getAccessToken();
	            Long expiresIn = oAuthResponse.getExpiresIn();

				System.out.println("OK");

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		});
		createServlet("/auth", (req, res) -> {

			try {
				OAuthClientRequest request = OAuthClientRequest
						.authorizationLocation("https://accounts.google.com/o/oauth2/v2/auth")
						.setClientId("530011801167-8n1afrtec2q7mac6o8po5mq782f4ve84.apps.googleusercontent.com")
						.setRedirectURI(getServerUrl() + "/app/autorized")
						.setResponseType(OAuth.OAUTH_CODE)
						.setScope("https://www.googleapis.com/auth/drive.metadata.readonly")
						.setState(UUID.randomUUID().toString())
						.setParameter("include_granted_scopes", "true")
						.buildQueryMessage();

				res.sendRedirect(request.getLocationUri());

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		});

		getEmbeddedServer().start();
		getEmbeddedServer().getServer().await();
	}
	
	//@Test
	public void oauthFacebook() throws Exception {
		createServlet("/app/valid", "OK");
		createServlet("/app/autorized", (req, res) -> {

			try {
				OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(req);
				String code = oar.getCode();

				OAuthClientRequest request = OAuthClientRequest
	                .tokenLocation("https://graph.facebook.com/v2.11/oauth/access_token")
	                .setClientId("1015110228632117")
	                .setClientSecret("4bcb1512d42f81b9c4bd0bd9c08abf42")
	                .setRedirectURI(getServerUrl() + "/app/autorized")
	                .setCode(code)
	                .buildBodyMessage()
	                ;
				
				OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
				 
				OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);
	 
	            String accessToken = oAuthResponse.getAccessToken();
	            Long expiresIn = oAuthResponse.getExpiresIn();

				System.out.println("OK");

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		});
		createServlet("/auth", (req, res) -> {

			try {
				OAuthClientRequest request = OAuthClientRequest
						.authorizationLocation("https://www.facebook.com/v2.11/dialog/oauth")
						.setResponseType(OAuth.OAUTH_CODE)
						.setClientId("1015110228632117")
						.setRedirectURI(getServerUrl() + "/app/autorized")
						.setState(UUID.randomUUID().toString())
						.buildQueryMessage();

				res.sendRedirect(request.getLocationUri());

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		});

		getEmbeddedServer().start();
		getEmbeddedServer().getServer().await();
	}
	
}
