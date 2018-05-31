package com.naskar.manyways;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;

import com.naskar.manyways.base.EmbeddedServerTestBase;
import com.naskar.manyways.impl.ManyWayImpl;
import com.naskar.manyways.impl.handlers.proxy.StandardProxyHttpHandler;
import com.naskar.manyways.impl.handlers.proxy.standard.RoundRobinLoadBalancer;
import com.naskar.manyways.impl.handlers.proxy.standard.SingleTarget;

public class StandardProxyHttpTest extends EmbeddedServerTestBase {
	
	@Test
	public void testSuccessHandler() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new StandardProxyHttpHandler().factory(new SingleTarget()
					.prefix("/app")
					.target(getServerUrl() + "/target/app")));
		
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
	public void testSuccessLoadBalancer() throws Exception {
		
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new StandardProxyHttpHandler().factory(
					new RoundRobinLoadBalancer()
						.prefix("/app")
						.addTarget(getServerUrl() + "/target/app")
						.addTarget("http://127.0.0.1:8099/naoexiste/app")
					));
		
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
	public void testSucessTwoRequestLoadBalancer() throws Exception {
		
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new StandardProxyHttpHandler().factory(
					new RoundRobinLoadBalancer()
						.prefix("/app")
						.addTarget(getServerUrl() + "/target/app")
						.addTarget("http://127.0.0.1:8099/naoexiste/app")
					));
		
		// Arrange
		String expected = "OK";
		
		createServlet("/target/app/*", expected);
		createServlet("/mw/*", manyWay);
        
        // Act
		start();
		// 1
        String actual = Request.Get(getServerUrl() + "/mw/app/teste?id=1&tt=2").execute().returnContent().asString();
        
        // Assert
        Assert.assertEquals(expected, actual);
        
        //2
        actual = Request.Get(getServerUrl() + "/mw/app/teste?id=1&tt=2").execute().returnContent().asString();
        
        // Assert
        Assert.assertEquals(expected, actual);
	}
	
	@Test(expected = IOException.class)
	public void testFailedRequestLoadBalancer() throws Exception {
		
		ManyWayImpl manyWay = new ManyWayImpl()
			.addHandler(new StandardProxyHttpHandler().factory(
					new RoundRobinLoadBalancer()
						.prefix("/app")
						.addTarget("http://127.0.0.1:8099/naoexiste/app")
						.addTarget("http://127.0.0.1:8100/naoexiste/app")
					));
		
		// Arrange
		createServlet("/mw/*", manyWay);
        
        // Act
		start();
		// 1
        Request.Get(getServerUrl() + "/mw/app/teste?id=1&tt=2").execute().returnContent().asString();
        
	}
}
