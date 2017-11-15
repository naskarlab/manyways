package com.naskar.manyways;

import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;

import com.naskar.manyways.base.EmbeddedServerTestBase;
import com.naskar.manyways.impl.ManyWayImpl;
import com.naskar.manyways.impl.handlers.metrics.SimpleTimeRequestLoggerHandler;
import com.naskar.manyways.impl.ways.DiscoveryWay;

public class DiscoveryWayTest extends EmbeddedServerTestBase {
		
	@Test
	public void testDiscovery() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new SimpleTimeRequestLoggerHandler())
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
