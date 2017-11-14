package com.naskar.manyways;

import java.util.Arrays;

import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;

import com.naskar.manyways.impl.ManyWayImpl;
import com.naskar.manyways.impl.ProxyHttpHandler;
import com.naskar.manyways.impl.ways.DiscoveryWay;
import com.naskar.manyways.impl.ways.MappingWay;

public class ManyWayTest extends EmbeddedServerTestBase {
	
	//@Test
	public void testMapping() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			.addWay(new MappingWay()
						.path("/app")
						.handlers(Arrays.asList(
							new ProxyHttpHandler()
								.prefix("/app")
								.target(getServerUrl() + "/target/app"))))
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
	
	@Test
	public void testDiscovery() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			.addWay(new DiscoveryWay()
						.path("/api")
						.url(getServerUrl() + "/discovery"))
			;
		
		// Arrange
		String expected = "OK";
		
		createServlet("/discovery/*", readFile("testSuccessDiscoveryHandlers.json"));
		createServlet("/target/api/*", expected);
		createServlet("/mw/*", manyWay);
        
        // Act
		start();
        String actual = Request.Get(getServerUrl() + "/mw/api/teste?id=1&tt=2").execute().returnContent().asString();
        
        // Assert
        Assert.assertEquals(expected, actual);
	}
}
