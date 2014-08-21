package read_files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.bbn.openmap.geo.Geo;

import TAD.Place;

public class Transform_and_Filter {

	
	
	public  ArrayList<Repository> transform_Repository_to_Place(ArrayList<Repository> repository) throws FileNotFoundException, IOException{
		
		 ArrayList<String> stop_words = read_stop_words("files"+File.separator+"stop_words.txt");
		 
		 for(Repository r: repository){
			 ArrayList<Place> places = new  ArrayList<Place>();
			 ArrayList<String[]> records = r.getData(); //return type: [DATE] [PLACE] [COUNTY] [LAT] [LONG]
			 for(String [] data: records){
				 if(!data[3].equals("")&& !data[4].equals("") && !data[0].equals("")){
				  float x =transformaFloat(data[3]);
                  float y = transformaFloat(data[4]);
				//constructor (int year, String location, String nameFilter, String county, Geo geometry)
				 places.add(new Place(Integer.parseInt(data[0]),data[1]," ",data[2], new Geo(x,y)));
				 }else if(data[0].equals("")){
					 places.add(new Place(9999,data[1]," ",data[2]));
				 }
			  }
			 remove_stop_word(places,stop_words);
			 r.setPlaces(places);
		 }
		 
		return repository;
		
	}
	 public ArrayList<String> read_stop_words(String file_path) throws FileNotFoundException, IOException {
		 String word ="";
         ArrayList<String> uselles = new ArrayList<String>();
         File arq = new File(file_path).getAbsoluteFile();
         BufferedReader lines =  new BufferedReader(new InputStreamReader(new FileInputStream(file_path), "UTF-8"));
         while ((word = lines.readLine()) != null) {
        	 uselles.add(word);
         }
         return uselles;
	 }
	 public void remove_stop_word(ArrayList <Place> places,ArrayList<String> stop_words){
	        String temp [];
	        for(Place p:places){
	            String ok ="";
	            p.setNameFilter(p.getLocation().toLowerCase().replaceAll("(?!\")\\p{Punct}", " "));
	            temp = p.getNameFilter().split(" ");
	             for (String s : stop_words) {
	                for(int k=0;k<temp.length;k++){
	                    if(temp[k].equals(s) || temp[k].equals(""))
	                     temp[k] = "";
	                }
	            }
	         
	            for(int n=0;n<temp.length;n++){
	                if(!temp[n].equals(""))
	                    ok +=temp[n]+" ";
	            }
	            p.setNameFilter(ok);
	        }        
	    }
	
	 public float transformaFloat(String numero) {
	        float valor = 0;
	        char n[] = numero.toCharArray();
	        numero = "";
	        for (int i = 0; i < n.length; i++) {
	            if (n[i] == ',') {
	                numero += ".";
	            }
	            numero += n[i];
	        }
	        try {
	            valor = Float.parseFloat(numero);
	        } catch (Exception e) {
	            return 0;
	        }
	        return valor;
	    }
}
