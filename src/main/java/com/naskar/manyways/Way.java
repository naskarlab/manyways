package com.naskar.manyways;

import java.util.List;

public interface Way {
	
	String getPath();
	
	List<Handler> resolveHandlers();

}
