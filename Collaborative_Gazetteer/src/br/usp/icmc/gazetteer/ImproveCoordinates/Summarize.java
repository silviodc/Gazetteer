/*
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
package br.usp.icmc.gazetteer.ImproveCoordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.usp.icmc.gazetteer.TAD.County;
import br.usp.icmc.gazetteer.TAD.Group;
import br.usp.icmc.gazetteer.TAD.Place;
import br.usp.icmc.gazetteer.cluster.Similarity;


public class Summarize {

	public static int improved=0;
	private int today=2014;
	private boolean verific_county( County county) {

		if (county != null && !county.getNome().equals("não informado") && !county.getNome().equals(" ") && !county.getNome().equals("")) {
			return true;
		}
		return false;
	}
	public void referenciaGeo(ArrayList<Group> group){
		System.out.println("GROUP SIZE"+group.size());
		Similarity sm = new Similarity();
		for (int i=0;i<group.size();i++) {
			Group gp = group.get(i);
			Place moda=null;
			int ant=0,ant2=0;
			String municipality="";
			County c=null;
			List<String> geo = new ArrayList<String>();
			List<String> county = new ArrayList<String>();
			List<String> locality = new ArrayList<String>();
			if(gp.getCentroid().isGoldStandart()){
				moda = gp.getCentroid();
				for(int j=0;j<gp.getPlaces().size();j++){
					if(verific_county(gp.getPlaces().get(j).getCounty())){
						county.add(gp.getPlaces().get(j).getCounty().getNome());
					}
				}
				for(int j=0;j<gp.getPlaces().size();j++){
					if(verific_county(gp.getPlaces().get(j).getCounty()) && Collections.frequency(county, gp.getPlaces().get(j).getCounty().getNome())>ant2){
						municipality = gp.getPlaces().get(j).getCounty().getNome();

					}
				}
				for(int j=0;j<gp.getPlaces().size();j++){
					if(verific_county(gp.getPlaces().get(j).getCounty()) && gp.getPlaces().get(j).getCounty().equals(municipality)){
						c = gp.getPlaces().get(j).getCounty();
						break;
					}
				}
			}else{	
				
				for(int j=0;j<gp.getPlaces().size();j++){
					if(sm.jaccardSimilarity(gp.getCentroid().getLocation(), gp.getPlaces().get(j).getLocation())>=0.4)
						locality.add(gp.getPlaces().get(j).getLocation());
				}
				for(int j=0;j<gp.getPlaces().size();j++){
					for(int k=0;k<locality.size();k++){
						if(locality.get(k).equals(gp.getPlaces().get(j).getLocation())){
							if(gp.getPlaces().get(j).getGeometry()!=null){
								geo.add(gp.getPlaces().get(j).getGeometry().toString());
							}
							break;
						}						
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

	private void replace_moda_all_data(Place moda, ArrayList<Place> arrayList,County municipality) {		
		for(int i=0;i<arrayList.size();i++){
			 arrayList.get(i).setGeometry(moda.getGeometry());
				 arrayList.get(i).setCounty(municipality);
				 improved++;
			 
		}
		moda.setCounty(municipality);
	}
}
