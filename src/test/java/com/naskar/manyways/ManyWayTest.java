package com.naskar.manyways;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.fluent.Request;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.naskar.manyways.impl.DefaultManyWayExecutor;
import com.naskar.manyways.impl.ManyWayImpl;
import com.naskar.manyways.impl.ProxyHttpHandler;
import com.naskar.manyways.impl.ways.MappingWay;

public class ManyWayTest {
	
	private static final String serverUrl = "http://localhost:9021";
	
	private static Tomcat tomcat;
	private static Context ctx;
	
	@Before
	public void setUp() throws LifecycleException {
		if(tomcat != null) {
			cleanUp();
		}
		
		tomcat = new Tomcat();
        tomcat.setPort(9021);
        
        ctx = tomcat.addContext("", new File(".").getAbsolutePath());
	}
	
	@AfterClass
	public static void cleanUp() throws LifecycleException {
		ctx = null;
		tomcat.stop();
		tomcat.destroy();
		tomcat = null;
	}
	
	@Test
	public void testMapping() throws Exception {
		ManyWayImpl manyWay = new ManyWayImpl()
			//.addHandler(new StatHandler())
			.addWay(new MappingWay()
						.path("/app")
						.handlers(Arrays.asList(new ProxyHttpHandler().prefix("/app").target(serverUrl + "/target/app"))))
			//.addWay(new DiscoveryWay()
			//			.path("/api").url(serverUrl + "/target/api/discovery"))
			;
		
		// Arrange
		String expected = "OK";
		
		createServlet("/target/app/*", expected);
		createServlet("/mw/*", manyWay);
        
        // Act
		tomcat.start();
        String actual = Request.Get(serverUrl + "/mw/app/teste?id=1&tt=2").execute().returnContent().asString();
        
        // Assert
        Assert.assertEquals(expected, actual);
	}
	
	private void createServlet(String path, String expected) throws LifecycleException {
        Tomcat.addServlet(ctx, "target-" + path.hashCode(), new HttpServlet() {
        	
			private static final long serialVersionUID = 1L;

			@Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) 
                    throws ServletException, IOException {
                
                Writer w = resp.getWriter();
                w.write(expected);
                w.flush();
                w.close();
            }
        })
        .addMapping(path);
	}
	
	private void createServlet(String path, final ManyWay manyWay) throws LifecycleException {
        Tomcat.addServlet(ctx, "target-" + path.hashCode(), new HttpServlet() {
        	
			private static final long serialVersionUID = 1L;

			@Override
            protected void service(HttpServletRequest req, HttpServletResponse res) 
                    throws ServletException, IOException {
				new DefaultManyWayExecutor(manyWay).execute(req, res);
            }
        })
        .addMapping(path);
	}
}
