package com.naskar.manyways;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Way {
	
	String getPath();
	
	List<Handler> resolveHandlers(HttpServletRequest req, HttpServletResponse res);

}
