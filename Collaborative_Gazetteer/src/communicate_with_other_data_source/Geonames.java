package communicate_with_other_data_source;

import java.util.ArrayList;

import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import TAD.Place;

public class Geonames {

	private ArrayList<Place> geonamesPlaces;
	
	
	public void acessGeonames() throws Exception{
		  
		
		 WebService.setUserName("silviodc"); // add your username here
		 
				 
		 ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
		 searchCriteria.setQ("z&uumlrich");
		 ToponymSearchResult searchResult = WebService.search(searchCriteria);
		 for (Toponym toponym : searchResult.getToponyms()) {
		 	System.out.println(toponym.getName() + " " + toponym.getCountryName());
		 }

	}
}
