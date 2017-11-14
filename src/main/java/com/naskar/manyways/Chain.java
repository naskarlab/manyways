package com.naskar.manyways;

import java.util.Map;

public interface Chain {
	
	void next();

	Map<String, Object> getHeaderMap();

}
