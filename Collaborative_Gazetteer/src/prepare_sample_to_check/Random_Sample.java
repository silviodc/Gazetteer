package prepare_sample_to_check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import TAD.Group;
import TAD.Place;
import TAD.Statistics;

public class Random_Sample {
	
	
	public void corrected_link_before_swi(ArrayList<Place> place, String nameFile) throws IOException{
		ArrayList<Place> arrayList = (ArrayList<Place>) place.clone();
		 File file = new File(nameFile);
	      // creates the file
	      file.createNewFile();
	      // creates a FileWriter Object
	      FileWriter writer = new FileWriter(file); 
	      // Writes the content to the file
	      Random rand = new Random();
	      int i=0;
	      while(i<100){
	    	  int temp = rand.nextInt(arrayList.size());
	    	  if(arrayList.get(temp).getGeometry()!=null && arrayList.get(temp).getGeometry().getLatitude()!=0){
		    	  writer.write("Centroid >> "+arrayList.get(temp).getLocation()+"    "+arrayList.get(temp).getGeometry());
		    	  writer.write("\n");
		    	  writer.write("___________________________________");
		    	  writer.write("\n");
		    	  writer.write("\n");
		    	  i++;
	    	  }
	      }
	      writer.close();
	}
	
	public void random_inner_Group(ArrayList<Group> group, int num) throws IOException{
		  ArrayList<Group> cloned = (ArrayList<Group>) group.clone();
		  File file = new File("Inner_Group_sample.txt");
	      // creates the file
	      file.createNewFile();
	      // creates a FileWriter Object
	      FileWriter writer = new FileWriter(file); 
	      // Writes the content to the file
	      Random rand = new Random();
	      for(int i=0;i<num;i++){
	    	  int temp = rand.nextInt(cloned.size());
	    	  writer.write("Centroid >> "+cloned.get(temp).getCentroid().getLocation()+"    "+cloned.get(temp).getCentroid().getYear()+"   "+cloned.get(temp).getCentroid().getGeometry());
	    	  writer.write("\n");
	    	  for(Place p: cloned.get(temp).getPlaces()){
	    		  writer.write(p.getLocation()+"    "+p.getYear()+"   "+p.getGeometry()); 
	    		  writer.write("\n");
	    		  writer.flush();
	    	  }
	    	  cloned.remove(temp);
	    	  writer.write("___________________________________");
	    	  writer.write("\n");
	    	  writer.write("\n");
	      }
	      writer.close();
	}
	
	public void random_Centroid(ArrayList<Group> group, int num) throws IOException{
		ArrayList<Group> cloned = (ArrayList<Group>) group.clone();
		  File file = new File("Centroid_sample.txt");
	      // creates the file
	      file.createNewFile();
	      // creates a FileWriter Object
	      FileWriter writer = new FileWriter(file); 
	      // Writes the content to the file
	      Random rand = new Random();
	      for(int i=0;i<num;i++){
	    	  	  int temp = rand.nextInt(cloned.size());
	    		  writer.write(cloned.get(temp).getCentroid().getLocation()+"    "+cloned.get(temp).getCentroid().getYear()+"   "+cloned.get(temp).getCentroid().getGeometry()); 
	    		  writer.write("\n");
	    		  writer.flush();
	    		  cloned.remove(temp);
	      }
	      writer.close();
	}

	public void checkAmoutCorrectBeforeSWI(String local) throws IOException {
		   int wrong=0,correct=0;
		   String path = new File(local).getAbsolutePath();
		   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		   Statistics number = new Statistics();
	         while(br.ready()){  
	            String linha = br.readLine();  
	            String values [] = linha.split(" ");
	            System.out.println(values[0]);
	            if(values[0].equals("0"))
	            		wrong++;
	            else if(values[0].equals("1"))
	            	correct++;
	            
	         }
	         System.out.println("Correct: "+correct+" ---- Wrong: "+wrong);
	}

}
