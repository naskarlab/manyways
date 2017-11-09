package com.naskar.manyways;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Handler {
	
	void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception;

}
