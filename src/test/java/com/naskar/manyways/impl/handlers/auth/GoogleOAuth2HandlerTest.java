//package com.naskar.manyways.impl.handlers.auth;
//
//import java.util.Arrays;
//
//import org.apache.http.client.fluent.Request;
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.naskar.manyways.base.EmbeddedServerTestBase;
//import com.naskar.manyways.impl.ManyWayImpl;
//import com.naskar.manyways.impl.handlers.metrics.SimpleTimeRequestLoggerHandler;
//import com.naskar.manyways.impl.handlers.proxy.ProxyHttpHandler;
//import com.naskar.manyways.impl.ways.MappingWay;
//
//public class GoogleOAuth2HandlerTest extends EmbeddedServerTestBase {
//
//	/*
//	@Test
//	public void testMapping() throws Exception {
//		ManyWayImpl manyWay = new ManyWayImpl()
//			.addHandler(new GoogleOAuth2Handler()
//					.clientId())
//			.addWay(new MappingWay()
//						.path("/app")
//						.handlers(Arrays.asList(
//							new ProxyHttpHandler()
//								.prefix("/app")
//								.target(getServerUrl() + "/target/app"))))
//			;
//		
//		// Arrange
//		String expected = "OK";
//		
//		createServlet("/target/app/*", expected);
//		createServlet("/mw/*", manyWay);
//        
//        // Act
//		start();
//        String actual = Request.Get(getServerUrl() + "/mw/app/teste?id=1&tt=2").execute().returnContent().asString();
//        
//        // Assert
//        Assert.assertEquals(expected, actual);
//	}
//	*/
//	
//}
