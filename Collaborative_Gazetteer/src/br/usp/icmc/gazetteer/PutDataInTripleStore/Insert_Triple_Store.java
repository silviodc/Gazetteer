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
package br.usp.icmc.gazetteer.PutDataInTripleStore;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Insert_Triple_Store {
	
	private final String URL_endpoint="http://biomac.icmc.usp.br:8080/swiendpoint/Query";
	
	
	/*public void updateLocalhost(String queryString) throws Exception{
		Strabon endpoint = new Strabon("swiendpoint", "postgres", "postgres", 5432, "localhost", true);
		//	String queryString = "DELETE WHERE { <http://www.semanticweb.org/ontologies/Gazetter#5032> <http://www.semanticweb.org/ontologies/Gazetter#contributors> \"0\"^^<http://www.w3.org/2001/XMLSchema#long> . }; INSERT DATA {<http://www.semanticweb.org/ontologies/Gazetter#1107> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://www.semanticweb.org/ontologies/Gazetter#1108> . }";
			endpoint.update(queryString, endpoint.getSailRepoConnection());
			endpoint.close();
	}
	*/

	public void updateDataRemote(String queryString )throws Exception{
		//String queryString = "DELETE WHERE { <http://www.semanticweb.org/ontologies/Gazetter#5032> <http://www.semanticweb.org/ontologies/Gazetter#contributors> \"13\"^^<http://www.w3.org/2001/XMLSchema#long> . }; INSERT DATA  { <http://www.semanticweb.org/ontologies/Gazetter#5032> <http://www.semanticweb.org/ontologies/Gazetter#contributors> \"12\"^^<http://www.w3.org/2001/XMLSchema#long> . } ";

		Client client = Client.create();
		WebResource webResource = client.resource(URL_endpoint);
		String input = "query="+queryString+"&view=HTML&format=HTML&handle=plain&submit=Update";

		ClientResponse response = webResource.type("application/x-www-form-urlencoded").post(ClientResponse.class, input);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus() +"  "+response.getResponseDate().toString());
		}
		if(response.getStatus()==200)
			System.out.println("Data inserted!!");
		
	}
	
/*	public void insertDataLocalhost() throws Exception{
		 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("municipality.nt"), "UTF-8"));
		 Strabon endpoint = new Strabon("swiendpoint2", "postgres", "postgres", 5432, "localhost", true);
		 while(br.ready()){  
			 String queryString = br.readLine();
			 endpoint.storeInRepo(queryString, "N-Triples", true);
		 }
		 endpoint.close();

	}*/
}
