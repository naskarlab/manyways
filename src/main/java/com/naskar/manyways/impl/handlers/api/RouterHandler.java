package com.naskar.manyways.impl.handlers.api;

import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;
import com.naskar.router.servlet.Router;
import com.naskar.router.servlet.impl.RouterImpl;

public class RouterHandler implements Handler {
	
	private String path;
	private RouterImpl router;
	
	public RouterHandler() {
		this.router = new RouterImpl();
	}
	
	public RouterHandler path(String value) {
		this.router.prefix(value);
		this.path = value;
		return this;
	}
	
	public RouterHandler configure(Consumer<Router> action) {
		action.accept(this.router);
		return this;
	}

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {
		String uri = req.getPathInfo();
        if(uri != null && uri.startsWith(path)) {
        	this.router.execute(req, res);
        } else {
        	// TODO: verificar 
        	chain.next();
        }
	}
	
}
