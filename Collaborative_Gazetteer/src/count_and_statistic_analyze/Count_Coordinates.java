package count_and_statistic_analyze;

import java.util.ArrayList;
import java.util.Date;

import TAD.Place;

public class Count_Coordinates {
	
	public static int [][] countDate( ArrayList<Place> places){
		Date today = new Date();
		Place place_min = null, place_max = null;
		
		for(int i=0; i<places.size();i++){
			if(places.get(i).getYear()!=9999){
				place_max = places.get(i);
				place_min = places.get(i);
				break;
			}
		}
		for(int i=1; i<places.size();i++){
			if(places.get(i).getYear()!=9999 && places.get(i).getYear()<=today.getYear()){
				if(place_min.getYear()>=places.get(i).getYear())
					place_min = places.get(i);
				if(place_max.getYear()<=places.get(i).getYear())
					place_max = places.get(i);
			}else{
			//	System.out.println(places.get(i).getYear());
			}
		}
		int tam = (2014-1500)+1;
		
		System.out.println("TAM: "+tam);
		int [][] pl = new int[tam][2];
		int year = 1500;
		for(int i=0;i<pl.length;i++){
			pl[i][0]= year;
			year++;
		}
		for(int i=0;i<pl.length;i++){
			year = pl[i][0];
			int count =0;
			for(int k=0;k<places.size();k++){
				if(year == places.get(k).getYear()){
					count++;
					places.remove(k);
				}else{
				//	System.out.println(places.get(k).getYear());
					 pl[0][1]++;
				}
			}
			pl[i][1]=count;
		}
		
		return pl;
	}
    
}
