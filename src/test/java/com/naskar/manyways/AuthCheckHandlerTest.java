package com.naskar.manyways;

import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;

import com.naskar.manyways.base.EmbeddedServerTestBase;
import com.naskar.manyways.base.Holder;
import com.naskar.manyways.impl.ManyWayImpl;
import com.naskar.manyways.impl.handlers.StatHandler;
import com.naskar.manyways.impl.ways.DiscoveryWay;

public class AuthCheckHandlerTest extends EmbeddedServerTestBase {
		
	@Test
	public void testAuthValid() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new StatHandler())
			.addWay(new DiscoveryWay()
						.path("/api")
						.url(getServerUrl() + "/discovery"))
			;
		
		// Arrange
		String expected = "OK";
		String expectedXUserId = "rafael";
		String expectedXUserRoles = "1;2;3;";
		
		final Holder<String> actualXUserId = new Holder<String>();
		final Holder<String> actualXUserRoles = new Holder<String>();
		
		createServlet("/discovery/*", readFile("testSuccessDiscoveryAuthCheckProxyHandlers.json"));
		createServlet("/target/auth/validate/*", readFile("testSuccessAuthCheckValid.json"));
		createServlet("/target/api/*", (req, resp) -> {
			actualXUserId.value = req.getHeader("X-User-Id");
			actualXUserRoles.value = req.getHeader("X-User-Roles");
			write(resp, expected);
		});
		createServlet("/mw/*", manyWay);
        
        // Act
		start();
        String actual = Request.Get(getServerUrl() + "/mw/api/teste?id=1&tt=2").execute().returnContent().asString();
        
        // Assert
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expectedXUserId, actualXUserId.value);
        Assert.assertEquals(expectedXUserRoles, actualXUserRoles.value);
	}
	
	@Test
	public void testAuthInvalid() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new StatHandler())
			.addWay(new DiscoveryWay()
						.path("/api")
						.url(getServerUrl() + "/discovery"))
			;
		
		// Arrange
		String expected = "OK";
		
		createServlet("/discovery/*", readFile("testSuccessDiscoveryAuthCheckProxyHandlers.json"));
		createServlet("/target/auth/validate/*", readFile("testSuccessAuthCheckInvalid.json"));
		createServlet("/auth/login", expected);
		createServlet("/mw/*", manyWay);
        
        // Act
		start();
        String actual = Request.Get(getServerUrl() + "/mw/api/teste?id=1&tt=2").execute().returnContent().asString();
        
        // Assert
        Assert.assertEquals(expected, actual);
	}
}
