package com.naskar.manyways;

import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;

import com.naskar.manyways.base.EmbeddedServerTestBase;
import com.naskar.manyways.impl.ManyWayImpl;
import com.naskar.manyways.impl.handlers.redirect.URLRedirectHandler;

public class RedirectTest extends EmbeddedServerTestBase {
	
	/*
	TODO: configurate https tomcat
	@Test
	public void testHttpToHttpsRedirectHandler() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new HttpToHttpsRedirectHandler());
			;
		
		// Arrange
		String expected = "OK";
		
		createServlet("/target/app/*", expected);
		createServlet("/mw/*", manyWay);
        
        // Act
		start();
        String actual = Request.Get(getServerUrl() + "/mw/app/teste?id=1&tt=2").execute().returnContent().asString();
        
        // Assert
        Assert.assertEquals(expected, actual);
	}
	*/
	
	@Test
	public void testURLRedirectHandler() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
				.addHandler(new URLRedirectHandler()
					.path("/mw/app")
					.target(getServerUrl() + "/target/app"));
		
		// Arrange
		String expected = "OK";
		
		createServlet("/target/app/*", expected);
		createServlet("/mw/*", manyWay);
        
        // Act
		start();
        String actual = Request.Get(getServerUrl() + "/mw/app/teste?id=1&tt=2").execute().returnContent().asString();
        
        // Assert
        Assert.assertEquals(expected, actual);
	}
}
