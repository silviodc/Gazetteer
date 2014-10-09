package graphs;

import java.io.IOException;

import javax.script.ScriptException;

public class Build_graph {
	
	private void makeGraphUsingR() throws ScriptException {
	    try {
	        Runtime.getRuntime().exec(" Rscript firstscript.r");
	        System.out.println("Script executed");
	         } catch (IOException ex) {
	       System.out.println("Exception");
	       System.out.println(ex.getMessage());
	    }

	}
}
