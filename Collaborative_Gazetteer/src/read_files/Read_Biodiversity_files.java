package read_files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Read_Biodiversity_files {

	ArrayList<String> paths = new ArrayList<String>();
	ArrayList<String> data = new ArrayList<String>();
	
	public void read_biodiversity_files(){
		
		int i=0;
		
		 try{     
	         BufferedReader br = new BufferedReader(new FileReader("/Users/silvio/Documents/Gazetteer/speciesLink.txt"));  
	  
	         while(br.ready()){  
	            String linha = br.readLine();  
	            String values [] = linha.split("\t");
	            System.out.println(linha);  
	           // data.add(linha);
	            if(i==20)
	            	return;
	            i++;
	         }  
	         br.close();  
	      }catch(IOException ioe){  
	         ioe.printStackTrace();  
	      }  
	}
}
