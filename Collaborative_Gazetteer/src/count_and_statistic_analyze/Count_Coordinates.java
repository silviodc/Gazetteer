package count_and_statistic_analyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import analyze_geographical_coordinates.Out_Polygon;

import com.bbn.openmap.geo.OMGeo;

import TAD.Place;
import TAD.Repository;

public class Count_Coordinates {
	
	private static final int today =2014;
	
	public static int [][] countDate( ArrayList<Place> original_places, OMGeo.Polygon poly){
		Place place_min = null, place_max = null;
		ArrayList<Place> places =  (ArrayList<Place>) original_places.clone();
		for(int i=0; i<places.size();i++){
			if(places.get(i).getYear()<Integer.MAX_VALUE){
				place_max = places.get(i);
				place_min = places.get(i);
				break;
			}
		}
		for(int i=0; i<places.size();i++){
			if(places.get(i).getYear()<=today){
				if(place_min.getYear()>=places.get(i).getYear())
					place_min = places.get(i);
				if(place_max.getYear()<=places.get(i).getYear())
					place_max = places.get(i);
			}
		}
		int tam = (place_max.getYear()-place_min.getYear())+1;
	
		int [][] pl = new int[tam+1][2];
		int year = place_min.getYear();
		for(int i=0;i<pl.length-1;i++){
			pl[i][0]= year;
			year++;
		}
	
		pl[pl.length-1][0]=Integer.MAX_VALUE;
		
		for(int i=0;i<pl.length-1;i++){
			year = pl[i][0];
			int count =0;
			for(int k=0;k<places.size();k++){
				if((year == places.get(k).getYear()) && places.get(k).getGeometry()!=null ){
					Out_Polygon out = new Out_Polygon();
					if(out.insidePolygon(poly, places.get(k).getGeometry())){
						count++;
					
					}
				}else if (places.get(k).getGeometry()==null){
					
					pl[pl.length-1][1]++;
				}
			}
			pl[i][1]=count;
		}
		return pl;
	}
    
	public static void build_csv(int [][] years, String name) throws IOException{
		
		File file = new File(name+".csv");
	    // creates the file
	    file.createNewFile();
	    // creates a FileWriter Object
	    FileWriter writer = new FileWriter(file); 
	    writer.write("Date,Coordinates \n");  
		for(int j=0;j<years.length;j++){			
			 writer.write(years[j][0]+","+(years[j][1])+"\n");
			 writer.flush();
		}
		writer.close();
		
	}
}
