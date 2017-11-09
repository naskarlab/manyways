package com.naskar.manyways;

import java.util.List;

public interface ManyWay {
	
	List<Handler> resolveHandlers();
	
	List<Way> resolveWays();

}
