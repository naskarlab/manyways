//package com.naskar.manyways;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.Writer;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.catalina.Context;
//import org.apache.catalina.LifecycleException;
//import org.apache.catalina.startup.Tomcat;
//import org.apache.http.client.fluent.Request;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.naskar.manyways.impl.SimpleMappingTargetResolver;
//import com.naskar.manyways.impl.SimpleProxyHandler;
//
//public class SimpleProxyHandlerTest {
//	
//	private static final String serverUrl = "http://localhost:9021";
//	
//	private static Tomcat tomcat;
//	
//	@AfterClass
//	public static void cleanUp() throws LifecycleException {
//		tomcat.stop();
//		tomcat.destroy();
//		tomcat = null;
//	}
//	
//	@Test
//	public void testSuccess() throws Exception {
//		// Arrange
//		String expected = "OK";
//		
//		createServlet(expected, "/tt/path", new SimpleProxyHandler(
//			new SimpleMappingTargetResolver()
//				.add("/tt/path", serverUrl + "/tt/target")
//		));
//        
//        // Act
//        String actual = Request.Get(serverUrl + "/tt/path").execute().returnContent().asString();
//        
//        // Assert
//        Assert.assertEquals(expected, actual);
//	}
//	
//	@Test
//	public void testFailedStatus404() throws Exception {
//		// Arrange
//		int expected = 404;
//		
//		createServlet(null, "/tt/path", new SimpleProxyHandler(
//			new SimpleMappingTargetResolver()
//		));
//        
//        // Act
//        int actual = Request.Get(serverUrl + "/tt/path").execute().returnResponse().getStatusLine().getStatusCode();
//        
//        // Assert
//        Assert.assertEquals(expected, actual);
//	}
//
//	private void createServlet(String expected, String path, SimpleProxyHandler handler) throws LifecycleException {
//		if(tomcat != null) {
//			cleanUp();
//		}
//		
//		tomcat = new Tomcat();
//        tomcat.setPort(9021);
//        
//        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
//        
//        if(expected != null) {
//	        Tomcat.addServlet(ctx, "target", new HttpServlet() {
//	        	
//				private static final long serialVersionUID = 1L;
//	
//				@Override
//	            protected void service(HttpServletRequest req, HttpServletResponse resp) 
//	                    throws ServletException, IOException {
//	                
//	                Writer w = resp.getWriter();
//	                w.write(expected);
//	                w.flush();
//	                w.close();
//	            }
//	        })
//	        .addMapping("/tt/target");
//        }
//        
//        Tomcat.addServlet(ctx, "path", new HttpServlet() {
//        	
//			private static final long serialVersionUID = 1L;
//
//			@Override
//            protected void service(HttpServletRequest req, HttpServletResponse res) 
//                    throws ServletException, IOException {
//        
//				try {
//					handler.handle(req, res);
//				} catch(Exception e) {
//					throw new RuntimeException(e);
//				}
//                
//            }
//        }).addMapping(path);
//
//        tomcat.start();
//	}
//
//}
