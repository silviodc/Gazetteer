package communicate_with_other_data_source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;



import com.bbn.openmap.geo.Geo;

import TAD.Place;

public class Geonames {

	private ArrayList<Place> geonamesPlaces;
	
	public ArrayList<Place> getGeonamesPlaces(){
		return geonamesPlaces;
	}
	
	public void acessGeonames() throws Exception{
		 
		
		String path = new File("files"+File.separator+"geonames.txt").getAbsolutePath();		
		 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
         while(br.ready()){  
            String linha = br.readLine();  
            String values [] = linha.split("\t");
            if(values[values.length-2].equals("America/Manaus")){
            	float latitude=0;
            	float longitude=0;
            	Geo geo = new Geo(latitude,longitude);
            	geonamesPlaces.add(new Place(values[1],geo));
            }
         }
	}
}
