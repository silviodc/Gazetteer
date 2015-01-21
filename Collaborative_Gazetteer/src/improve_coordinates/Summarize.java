package improve_coordinates;

import java.util.ArrayList;
import java.util.Arrays;


import TAD.Expression;
import TAD.Group;
import TAD.Place;

import com.bbn.openmap.geo.Geo;


public class Summarize {

	public static int improved=0;
	public void referenciaGeo(ArrayList<Group> group){
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
	 	        	replace_moda_all_data(moda, gp.getPlaces());
	 	        }
	       }
	}

	private static void replace_moda_all_data(Place moda, ArrayList<Place> arrayList) {
		
		for(int i=0;i<arrayList.size();i++){
			 arrayList.get(i).setGeometry(moda.getGeometry());
			 improved++;
	//		 System.out.println(arrayList.get(i).getGeometry()+"  "+arrayList.get(i).getYear()+"=== MODA: "+moda.getGeometry());

		}
	}
}
