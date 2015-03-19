package prepare_sample_to_check;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import TAD.Group;
import TAD.Place;

public class Random_Sample {
	
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
	    	  for(Place p: cloned.get(temp).getPlaces()){
	    		  writer.write(p.getLocation()+"    "+p.getYear()+"   "+p.getGeometry()); 
	    		  writer.flush();
	    	  }
	    	  cloned.remove(temp);
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
	    		  writer.flush();
	    		  cloned.remove(temp);
	      }
	      writer.close();
	}

}
