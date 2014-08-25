package count_and_statistic_analyze;

import java.util.ArrayList;
import java.util.Date;

import TAD.Place;

public class Count_Coordinates {
	
	private static final int today =2014;
	
	public static int [][] countDate( ArrayList<Place> places){
		Place place_min = null, place_max = null;
		
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
					count++;
					places.remove(k);
				}else if (places.get(k).getGeometry()==null){
					places.remove(k);
					pl[pl.length-1][1]++;
				}
			}
			pl[i][1]=count;
		}
		return pl;
	}
    
}
