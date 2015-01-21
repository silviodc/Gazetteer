package count_and_statistic_analyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import analyze_geographical_coordinates.Out_Polygon;

import com.bbn.openmap.geo.OMGeo;

import TAD.Place;
import TAD.Repository;

public class Count_Coordinates {
	
	private static final int today =2014;
	
	
	private static boolean search(ArrayList<Integer>pl, int year){
		for(int i=0; i<pl.size();i++){
			if(pl.get(i)==year || year >today)
				return false;
		}
		return true;
	}
	
	public static int [][] countDate( ArrayList<Place> original_places, OMGeo.Polygon poly){
		Place place_min = null, place_max = null;
		ArrayList<Place> places =  (ArrayList<Place>) original_places.clone();
		
		ArrayList<Integer> pl = new ArrayList<Integer>();
		ArrayList<Integer[]> years = new ArrayList<Integer[]>();
	    
		while(!places.isEmpty()){
			if(search(pl,places.get(0).getYear())){
				pl.add(places.get(0).getYear());
			}
			places.remove(0);
		}
		System.out.println("Fez a busca");
		places.clear();
		places =  (ArrayList<Place>) original_places.clone();
		
		int []tmp = new int[pl.size()]; 
		for(int i=0;i<pl.size();i++){
			tmp[i]= pl.get(i);
		}
		Arrays.sort(tmp);
		System.out.println("Ordenou");
		for(int i=0;i<tmp.length;i++){
			years.add(new Integer[]{tmp[i],0});
		}
		System.out.println("Vai computar os anos... tam("+years.size()+")");
		for(int i=0;i<years.size();i++){
			int year = years.get(i)[0];
			int count =0;
			for(int k=0;k<places.size();k++){
				if((year == places.get(k).getYear()) && places.get(k).getGeometry()!=null ){
					Out_Polygon out = new Out_Polygon();
					if(out.insidePolygon(poly, places.get(k).getGeometry())){
						count++;
					
					}
				}
			}
			years.get(i)[1]=count;
		}
		System.out.println("Criando a matriz...");
		int [][] mat = new int [years.size()][2];
		int i=0;
		for(Integer[]temp:years){
			mat[i][0]=temp[0];
			mat[i][1]=temp[1];
			i++;
		}
		return mat;
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
