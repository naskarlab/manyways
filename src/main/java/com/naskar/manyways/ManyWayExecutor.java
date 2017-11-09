package com.naskar.manyways;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ManyWayExecutor {
	
	void execute(HttpServletRequest req, HttpServletResponse res);

}
