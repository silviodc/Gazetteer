package improve_coordinates;

import java.util.ArrayList;
import java.util.Arrays;


import TAD.Expression;
import TAD.Group;
import TAD.Place;

import com.bbn.openmap.geo.Geo;


public class Summarize {

	public static int improved=0;
	public void referenciaGeo(ArrayList<Group> group,int today){
		   for (Group gp:group) {
	        	int numTimes=1;
	        	Place moda=null;
	 	        int compValue=0;
	 	        for(int j=0;j<gp.getPlaces().size();j++){
	 	        	if(gp.getPlaces().get(j).getGeometry()!=null){
	 	        			Place pl = gp.getPlaces().get(j);
	 	        			for(int i=j+1;i<gp.getPlaces().size();i++){
	 	        				if(gp.getPlaces().get(i).getGeometry()!=null && pl.getGeometry().distance(gp.getPlaces().get(i).getGeometry())==0)
	 	        					numTimes++;
	 	        			}
	 	        			if(numTimes>compValue){
	 	        				compValue=numTimes;
	 	        				numTimes=1;
	 	        				moda = gp.getPlaces().get(j);
	 	        			}
	 	        	}
	 	        }
	 	        if(moda!=null){
	 	        	gp.setCentroid(moda);
	 	        	replace_moda_all_data(moda, gp.getPlaces(),today);
	 	        }
	       }
	}

	private static void replace_moda_all_data(Place moda, ArrayList<Place> arrayList,int today) {
		int tempYear=0;
		if(moda.getYear()>today){
			for(int i=0;i<arrayList.size();i++){
				if(arrayList.get(i).getYear()<=today){
					if(tempYear!=0 && tempYear>arrayList.get(i).getYear() && arrayList.get(i).getGeometry().distance(moda.getGeometry())<300){
						tempYear = arrayList.get(i).getYear();
					}else if(tempYear==0 && arrayList.get(i).getGeometry()!=null && arrayList.get(i).getGeometry().distance(moda.getGeometry())<300){
						tempYear = arrayList.get(i).getYear();
					 }
				}			
			}
		}
		if(tempYear!=0)
			moda.setYear(tempYear);
					
		for(int i=0;i<arrayList.size();i++){
			 arrayList.get(i).setGeometry(moda.getGeometry());
			 if(arrayList.get(i).getYear()>today){
				 arrayList.get(i).setYear(moda.getYear());
				 improved++;
			 }
		}
		
	}
}
