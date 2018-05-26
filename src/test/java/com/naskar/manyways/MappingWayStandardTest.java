package com.naskar.manyways;

import java.util.Arrays;

import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;

import com.naskar.manyways.base.EmbeddedServerTestBase;
import com.naskar.manyways.impl.ManyWayImpl;
import com.naskar.manyways.impl.handlers.metrics.SimpleTimeRequestLoggerHandler;
import com.naskar.manyways.impl.handlers.proxy.StandardProxyHttpHandler;
import com.naskar.manyways.impl.ways.MappingWay;

public class MappingWayStandardTest extends EmbeddedServerTestBase {
	
	@Test
	public void testMappingStandardProxy() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new SimpleTimeRequestLoggerHandler())
			.addWay(new MappingWay()
						.path("/app")
						.handlers(Arrays.asList(
							new StandardProxyHttpHandler()
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
}
