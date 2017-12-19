/**
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.usp.icmc.gazetteer.PrepareSampleToCheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import br.usp.icmc.gazetteer.TAD.Group;
import br.usp.icmc.gazetteer.TAD.Place;

public class Random_Sample {
	
	
	public void corrected_link_before_swi(ArrayList<Place> place, String nameFile) throws IOException{
		ArrayList<Place> arrayList = new ArrayList<Place>();
		arrayList.addAll(place);
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
		    	  writer.write("Centroid >> "+arrayList.get(temp).getLocation()+"    "+arrayList.get(temp).getID()+"  "+arrayList.get(temp).getGeometry());
		    	  writer.write("\n");
		    	  writer.write("___________________________________");
		    	  writer.write("\n");
		    	  writer.write("\n");
		    	  i++;
	    	  }
	      }
	      writer.close();
	}
	
	public void random_inner_Group(ArrayList<Group> group, int num,String simi) throws IOException{
		  File file = new File("files"+File.separator+"results"+File.separator+"Inner_Group_sample"+simi+".txt");
		  boolean used [] = new boolean[group.size()];
	      // creates the file
	      file.createNewFile();
	      // creates a FileWriter Object
	      FileWriter writer = new FileWriter(file); 
	      // Writes the content to the file
	      Random rand = new Random();
	      for(int i=0;i<num;i++){
	    	  int temp = rand.nextInt(group.size());
	    	  if(!used[temp]){ 	  
		    	  writer.write("Centroid >> "+group.get(temp).getCentroid().getLocation()+"  "+group.get(temp).getCentroid().getID()+"    "+group.get(temp).getCentroid().getYear()+"   "+group.get(temp).getCentroid().getGeometry());
		    	  writer.write("\n");
		    	  for(Place p: group.get(temp).getPlaces()){
		    		  writer.write("=="+p.getLocation()+"    "+p.getID()+"  "+p.getYear()+"   "+p.getGeometry()); 
		    		  writer.write("\n");
		    		  writer.flush();
		    	  }
		    	  group.remove(temp);
		    	  writer.write("___________________________________");
		    	  writer.write("\n");
		    	  writer.write("\n");
		    	  used[temp]=true;
	    	  }
	      }
	      writer.close();
	}
	
	public void random_Centroid(ArrayList<Group> group, int num,String simi) throws IOException{
		  boolean used [] = new boolean[group.size()];
		  File file = new File("files"+File.separator+"results"+File.separator+"Centroid_sample"+simi+".txt");
	      // creates the file
	      file.createNewFile();
	      // creates a FileWriter Object
	      FileWriter writer = new FileWriter(file); 
	      // Writes the content to the file
	      Random rand = new Random();
	      for(int i=0;i<num;i++){
	    	  	  int temp = rand.nextInt(group.size());
	    	  	  if(!used[temp]){
		    		  writer.write(group.get(temp).getCentroid().getLocation()+"    "+group.get(temp).getCentroid().getYear()+"   "+group.get(temp).getCentroid().getGeometry()); 
		    		  writer.write("\n");
		    		  writer.flush();
		    		  used[temp] =true;
	    	  	  }
	      }
	      writer.close();
	}

	public void checkAmoutCorrectBeforeSWI(String local) throws IOException {
		   int wrong=0,correct=0;
		   String path = new File(local).getAbsolutePath();
		   FileWriter writer = new FileWriter("files"+File.separator+"results"+File.separator+path);
		   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
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
	
	public void readAndWritePolygon(String name)throws IOException{
		  File file = new File(name);
	      // creates the file
	      file.createNewFile();
	      // creates a FileWriter Object
	      FileWriter writer = new FileWriter(file);
	      
		String polyPath = new File("files"+File.separator+"results"+File.separator+name).getAbsolutePath();
		BufferedReader read =  new BufferedReader(new InputStreamReader(new FileInputStream(polyPath), "UTF-8"));
        String line="";
        String polygon="";
	        while((line = read.readLine())!=null){ 
	        	polygon+=line.trim()+" ";
	        }
	    writer.write(polygon);
	    writer.close();
	}

}
