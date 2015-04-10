package improve_coordinates;

import java.util.ArrayList;
import java.util.Arrays;


import java.util.Collection;
import java.util.Collections;
import java.util.List;

import TAD.County;
import TAD.Expression;
import TAD.Group;
import TAD.Place;
import cluster.Bigram_Similarity;

import com.bbn.openmap.geo.Geo;


public class Summarize {

	public static int improved=0;

	private boolean verific_county( County county) {

		if (county != null && !county.getNome().equals("não informado") && !county.getNome().equals(" ") && !county.getNome().equals("")) {
			return true;
		}
		return false;
	}
	public void referenciaGeo(ArrayList<Group> group){
		for (Group gp:group) {
			Place moda=null;
			int ant=0,ant2=0;
			String municipality="";
			County c=null;
			List<String> geo = new ArrayList<String>();
			List<String> county = new ArrayList<String>();
			gp.getPlaces().remove(null);
			for(int j=0;j<gp.getPlaces().size();j++){
				if(gp.getPlaces().get(j).getGeometry()!=null){
					geo.add(gp.getPlaces().get(j).getGeometry().toString());
				}
				if(verific_county(gp.getPlaces().get(j).getCounty())){
					county.add(gp.getPlaces().get(j).getCounty().getNome());
				}
			}
			for(int j=0;j<gp.getPlaces().size();j++){
				if(gp.getPlaces().get(j).getGeometry()!=null){
					if(Collections.frequency(geo, gp.getPlaces().get(j).getGeometry().toString())>ant){
						moda =  gp.getPlaces().get(j);
					}
					if(verific_county(gp.getPlaces().get(j).getCounty()) && Collections.frequency(county, gp.getPlaces().get(j).getCounty().getNome())>ant2){
						municipality = gp.getPlaces().get(j).getCounty().getNome();
					}
				}
			}
			for(int j=0;j<gp.getPlaces().size();j++){
				if(verific_county(gp.getPlaces().get(j).getCounty()) && gp.getPlaces().get(j).getCounty().equals(municipality)){
					c = gp.getPlaces().get(j).getCounty();
					break;
				}
			}
			if(moda!=null){
				if(moda.getGeometry()!=null){
					gp.getCentroid().setGeometry(moda.getGeometry());
					gp.getCentroid().setCounty(c);
				}
				replace_moda_all_data(moda, gp.getPlaces(),c);
			}
		}
	}

	private static void replace_moda_all_data(Place moda, ArrayList<Place> arrayList,County municipality) {
		for(int i=0;i<arrayList.size();i++){
			if(moda.getGeometry()!=null)
			arrayList.get(i).setGeometry(moda.getGeometry());
			if(municipality!=null)
			arrayList.get(i).setCounty(municipality);
		}

	}
}
