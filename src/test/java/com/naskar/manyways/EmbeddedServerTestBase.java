package com.naskar.manyways;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.Before;

import com.naskar.manyways.impl.DefaultManyWayExecutor;

public class EmbeddedServerTestBase {
	
	private static final String serverUrl = "http://localhost:9021";
	private static final String baseDir = EmbeddedServerTestBase.class.getResource("/").getPath().substring(1);
	
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
	
	protected static String getServerUrl() {
		return serverUrl;
	}
	
	protected static void start() {
		try {
			tomcat.start();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void createServlet(String path, final BiConsumer<HttpServletRequest, HttpServletResponse> call) {
        Tomcat.addServlet(ctx, "target-" + System.nanoTime() + "-" + call.hashCode(), new HttpServlet() {
        	
			private static final long serialVersionUID = 1L;

			@Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) 
                    throws ServletException, IOException {
                
				call.accept(req, resp);
                
            }
        })
        .addMapping(path);
	}
	
	protected void createServlet(String path, String expected) {
		createServlet(path, (req, resp) -> {
			try {
				Writer w = resp.getWriter();
			    w.write(expected);
			    w.flush();
			    w.close();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	protected void createServlet(String path, ManyWay manyWay) {
		createServlet(path, (req, resp) -> {
			new DefaultManyWayExecutor(manyWay).execute(req, resp);
		});
	}
	
	protected String readFile(String filename) {
		try {
			return new String(Files.readAllBytes(Paths.get(baseDir + filename)));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
