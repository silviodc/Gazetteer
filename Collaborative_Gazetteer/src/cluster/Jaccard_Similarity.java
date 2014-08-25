package cluster;

import java.util.HashSet;

public class Jaccard_Similarity {
	
	   public double jaccardSimilarity(String similar1, String similar2) {
	        HashSet<String> h1 = new HashSet<String>();
	        HashSet<String> h2 = new HashSet<String>();
	        for (String s : similar1.split("\\s+")) {
	            h1.add(s);
	        }
	        for (String s : similar2.split("\\s+")) {
	            h2.add(s);
	        }
	        int sizeh1 = h1.size();
	        //Retains all elements in h3 that are contained in h2 ie intersection
	        h1.retainAll(h2);
	        //h1 now contains the intersection of h1 and h2
	        h2.removeAll(h1);
	        //h2 now contains unique elements
	        //Union 
	        int union = sizeh1 + h2.size();
	        int intersection = h1.size();
	        return (double) intersection / union;
	    }
}
