package put_data_in_Triple_Store;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.openrdf.query.MalformedQueryException;

import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

import eu.earthobservatory.org.StrabonEndpoint.client.SPARQLEndpoint;
import eu.earthobservatory.runtime.postgis.Strabon;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Insert_Triple_Store {
	
	private final String URL_endpoint="http://biomac.icmc.usp.br:8080/swiendpoint/Query";
	
	
	public void updateLocalhost(String queryString) throws Exception{
		Strabon endpoint = new Strabon("endpoint", "postgres", "postgres", 5432, "localhost", true);
		//	String queryString = "DELETE WHERE { <http://www.semanticweb.org/ontologies/Gazetter#5032> <http://www.semanticweb.org/ontologies/Gazetter#contributors> \"0\"^^<http://www.w3.org/2001/XMLSchema#long> . }; INSERT DATA {<http://www.semanticweb.org/ontologies/Gazetter#1107> <http://www.opengis.net/ont/geosparql#hasGeometry> <http://www.semanticweb.org/ontologies/Gazetter#1108> . }";
			endpoint.update(queryString, endpoint.getSailRepoConnection());
			endpoint.close();
	}
	

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
	
	public void insertDataLocalhost() throws Exception{
		 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("triples.nt"), "UTF-8"));
		 Strabon endpoint = new Strabon("endpoint", "postgres", "postgres", 5432, "localhost", true);
		 while(br.ready()){  
			 String queryString = br.readLine();
			 endpoint.storeInRepo(queryString, "N-Triples", true);
		 }
		 endpoint.close();
//		SPARQLEndpoint end = new SPARQLEndpoint("127.0.0.1", 8080,"swiendpoint");
//		end.setUser("postgres");
//		end.setPassword("postgres");
//		System.out.println(end.getConnectionURL());
//	
//	        System.out.println(query);
//			try {
//				boolean ok = end.update(query);
//				if(ok){
//					System.out.println("works");
//				}
//				//UpdateRequest queryObj=UpdateFactory.create(query);
//				//UpdateProcessor qexec=UpdateExecutionFactory.createRemoteForm(queryObj,"http://biomac.icmc.usp.br:8080/swiendpoint/query.jsp#2020554288");
//				//qexec.execute();
//				}catch (Exception e) {
//					 e.printStackTrace();
//				 }finally{
//					 break;
//				 }
//		 }
	}
}
