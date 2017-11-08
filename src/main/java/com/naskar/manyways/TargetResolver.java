package com.naskar.manyways;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TargetResolver {

	String resolve(HttpServletRequest req, HttpServletResponse res);

}
