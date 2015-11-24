package core_CloverNetwork;

import java.util.Comparator;

public class MatchPercentage implements Comparator<CompaMatch>{

	
	
    public int compare(CompaMatch x, CompaMatch y) {
    	if(x.score < y.score)
    		return 1;
    	if(x.score > y.score)
    		return -1;
    	return 0;
    }
	
	
	
	
	
	
	
	
	
}
