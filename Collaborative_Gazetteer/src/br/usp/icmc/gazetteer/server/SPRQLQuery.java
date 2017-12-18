/*    This file is part of SWI Gazetteer.

    SWI Gazetteer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SWI Gazetteer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SWI Gazetteer.  If not, see <http://www.gnu.org/licenses/>.
    */
package br.usp.icmc.gazetteer.server;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;

import br.usp.icmc.gazetteer.shared.Locality;
import br.usp.icmc.gazetteer.shared.Out_Polygon;

import com.bbn.openmap.geo.Geo;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SPRQLQuery {

	private com.hp.hpl.jena.query.Query  sparql;
	private final String URL_endpoint = "http://biomac.icmc.usp.br:8080/swiendpoint/Query";
	private  final double treshold=0.4;
	
	private int getResponseCode(String urlString) throws MalformedURLException, IOException {
	    URL url = new URL("http://biomac.icmc.usp.br:8080/swiendpoint/"); 
	    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
	    connection.connect();
	    return connection.getResponseCode();
	    
	}
	
	public boolean askService(){
		int status;
		try {
			status = getResponseCode(URL_endpoint);
			if (status==200) { 
				System.out.println(URL_endpoint + " is UP"); 
			    return true;
			}
		} catch (Exception e) {
			System.out.println(URL_endpoint + " is DOWN");
			e.printStackTrace();
			return false;
			
		}
		System.out.println(URL_endpoint + " is DOWN");
		return false;
			
	}
	
	public List<Locality> insidePolygon(String name,List<String> parameters,String queryString){
		List<Locality> result = new ArrayList<Locality>();
		 Out_Polygon out = new Out_Polygon();
		 System.out.println("SERA?");
		  try {
				out.setPoly(out.buildPolygon(new File("files"+File.separator+name+".txt").getAbsolutePath()));
			} catch (IOException e) {
				System.out.println("Erro ao carregar o poligono");
				e.printStackTrace();
			}
		
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);
		HashMap<String,String> tripleStore = new HashMap<String,String>();
		ResultSet results= queryExecution.execSelect();
		try{
			while(results.hasNext()) {
				QuerySolution soln = results.nextSolution() ;
				Iterator<String> names = soln.varNames();
				String point = soln.getLiteral("wkt").getString();
				//System.out.println(point);
				while(names.hasNext()){
					String value = names.next();
					if(soln.get(value).isLiteral())
						tripleStore.put(value, soln.get(value).asLiteral().getString());
					else if(soln.get(value).isResource())
						tripleStore.put(value, soln.get(value).toString());
					else if(soln.get(value).isURIResource())
						tripleStore.put(value, soln.get(value).toString());
				}
				if(point.contains("POINT")){
					point = point.replaceAll(";http://www.opengis.net/def/crs/EPSG/0/4326", "");
					point = point.substring(7, point.length()-2);
					float x = out.transformFloat(point.split(" ")[0]);
		        	float y = out.transformFloat(point.split(" ")[1]);
		        	if(out.insidePolygon(out.getPoly(), x, y)){
		        		Locality l = new Locality();
		        		Iterator<String> iterator2 = parameters.iterator();
		        			while(iterator2.hasNext()){
		        				String value2 = iterator2.next();
		        								
		        					if(value2.equals("instance")) {
		        						l.setIdTriple(tripleStore.get(value2));
		        						System.out.println(l.getIdTriple());
		        						
		        					}
		        					else if(value2.equals("date"))
		        						l.setDate(tripleStore.get(value2));
		        					else if(value2.equals("locality")) 
		        						l.setLocality(tripleStore.get(value2));
		        						
		        					else if(value2.equals("contributors")) 
		        						l.setContributors(Integer.parseInt(tripleStore.get(value2)));
		        						
		        					else if(value2.equals("agreement")) 
		        						l.setAgreeCoordinate(Integer.parseInt(tripleStore.get(value2)));
		        						
		        					else if(value2.equals("wkt")){
		        						point = tripleStore.get(value2).replaceAll(";http://www.opengis.net/def/crs/EPSG/0/4326", "");
		        						point = point.substring(7, point.length()-2);
		        						x = transformFloat(point.split(" ")[0]);
		        			        	y = transformFloat(point.split(" ")[1]);
		        						l.setGeometry(x+" "+y);
		        					}
		        						
		        					else if(value2.equals("county"))
		        						l.setCounty(tripleStore.get(value2));
		        						
		        					else if(value2.equals("triplas"))
		        						l.setNtriplas(tripleStore.get(value2));
		        					else if(value2.equals("geo1")){
		        						l.setIdGeo(tripleStore.get(value2));
		        						System.out.println(">>>>>>>>>>>>>>>>>>>>"+tripleStore.get(value2));
		        					}
		        			}
		        		result.add(l);
		        	}
				}
				tripleStore.clear();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("retrieval results");
		return result;
			
	}
	
	
	/*
	 * parameters.put("?locality","@#@#@#@#");ok
			parameters.put("uri", "?instance");ok
			parameters.put("date", "?date");ok
			parameters.put("contributors", "?contributors");ok
			parameters.put("agreement", "?agreement");			ok
			parameters.put("geo", "?wkt");
			parameters.put("county", "?county");
			parameters.put("triplas", "?triplas");
	 * 
	 * 
	 * 
	 * 	parameters.put("locality","?locality");ok
					parameters.add("instance");
			parameters.add("date");
			parameters.add("locality");
			parameters.add("contributors");
			parameters.add("agreement");			
			parameters.add("wkt");
			parameters.add("county");
			parameters.add("triplas");
	 */
	private Locality createLocal(HashMap<String, String> tripleStore,List<String> parameters) {
		Locality l = new Locality();
		Iterator<String> iterator2 = parameters.iterator();
		try{
			while(iterator2.hasNext()){
				String value2 = iterator2.next();
				 	if(value2.equalsIgnoreCase("geo1")){
						l.setIdGeo(tripleStore.get(value2));
					}else if(value2.equals("instance")){ 
						l.setIdTriple(tripleStore.get(value2));
					}else if(value2.equals("date"))
						l.setDate(tripleStore.get(value2));
					else if(value2.equals("locality")) 
						l.setLocality(tripleStore.get(value2));
						
					else if(value2.equals("contributors")) 
						l.setContributors(Integer.parseInt(tripleStore.get(value2)));
						
					else if(value2.equals("agreement")) 
						l.setAgreeCoordinate(Integer.parseInt(tripleStore.get(value2)));
						
					else if(value2.equals("wkt")){
						String point = tripleStore.get(value2).replaceAll(";http://www.opengis.net/def/crs/EPSG/0/4326", "");
						point = point.substring(7, point.length()-2);
						float x = transformFloat(point.split(" ")[0]);
			        	float y = transformFloat(point.split(" ")[1]);
						l.setGeometry(x+" "+y);
					}
						
					else if(value2.equals("county"))
						l.setCounty(tripleStore.get(value2));
						
					else if(value2.equals("triplas"))
						l.setNtriplas(tripleStore.get(value2));
						
					
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(l.getLocality()+"  -- "+l.getGeometry()+" -- "+l.getNtriplas());
				
		return l;
	}
	
	 public float transformFloat(String numero) {
	        float valor = 0;
	        char n[] = numero.toCharArray();
	        numero = "";
	        for (int i = 0; i < n.length; i++) {
	            if (n[i] == ',') {
	                numero += ".";
	            }
	            numero += n[i];
	        }
	        try {
	            valor = Float.parseFloat(numero);
	        } catch (Exception e) {
	            return 0;
	        }
	        return valor;
	    }
	 
	 
	public List<Locality> findQueryBox(String queryString,List<String> parameters){
		List<Locality> result = new ArrayList<Locality>();
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);
		HashMap<String,String> tripleStore = new HashMap<String,String>();
		ResultSet results= queryExecution.execSelect();
		while(results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			Iterator<String> names = soln.varNames();
			while(names.hasNext()){
				String value = names.next();
				if(soln.get(value).isLiteral())
					tripleStore.put(value, soln.get(value).asLiteral().getString());
				else if(soln.get(value).isResource()){
					tripleStore.put(value, soln.get(value).toString());
				}
			}
			result.add(createLocal(tripleStore,parameters));
			tripleStore.clear();
		}
		System.out.println("result>>>>"+result.size());
		return result;
	}
	
	
/*
 * 		parameters.put("locality","?local2");
		parameters.put("uri", "?instance");
		parameters.put("date", "?date");
		parameters.put("contributors", "?contributors");
		parameters.put("agreement", "?agreement");			
		parameters.put("geo", "?wkt");
		parameters.put("type", "?subject");
		parameters.put("county", "?county");
		parameters.put("triplas", "?triplas");
 */
	
	
	
	public HashMap<Integer,Locality> makeSPARQLQuery () {
		HashMap<Integer,Locality> result = new HashMap<Integer,Locality>();
		int index=1;

		//SPARQL query to retrieval all inside the gazetteer, Filter the places that don't have geometry (POINT, POLYGON, LINESTRING)

		String queryString ="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ " PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
				+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#>"
				+ " SELECT ?instance ?date ?locality ?contributors ?agreement ?wkt  ?subject ?county ?triplas WHERE {"
				+ "  ?subject ?predicate ?object ."
				+ " ?instance a ?subject ."
				+ " OPTIONAL { ?instance swi:date ?date . }"
				+ " ?instance swi:locality ?locality ."
				+ " ?instance swi:contributors ?contributors."
				+ " ?instance swi:agreement ?agreement . "
				+ " ?instance swi:ntriples ?triplas ."
				+ " OPTIONAL { ?instance swi:hasCounty ?Uc . ?Uc swi:county ?county }"
				+ "  FILTER NOT EXISTS { ?instance geo:hasGeometry ?o } } ORDER BY DESC (?triplas)";
		System.out.println(queryString);

		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

		ResultSet results= queryExecution.execSelect();
		Set<String> uris = new TreeSet<String>();
		if(results.hasNext())
		while(results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			Literal date =null;
			Literal county=null;
			RDFNode ontUri = soln.get("?instance");
			
			RDFNode type = soln.get("?subject");
			Literal locality = soln.get("locality").asLiteral();
			
			if(soln.getLiteral("county")!=null)
				county = soln.getLiteral("county").asLiteral();
			
			if(soln.getLiteral("date") !=null)
				date = soln.getLiteral("date").asLiteral();
			
			Literal triplas = soln.getLiteral("triplas").asLiteral();
			
			
			Literal contributors = soln.getLiteral("contributors").asLiteral();
			Literal agree = soln.getLiteral("agreement").asLiteral();
			if(uris.add(ontUri.toString())){
				Locality local = new Locality();	
				local.setAgreeCoordinate(agree.getInt());
				local.setContributors(contributors.getInt());
				if(date!=null)
					local.setDate(date.getString());
				if(county!=null)
					local.setCounty(county.getString());
				
				local.setNtriplas(triplas.getString());
				
				local.setIdTriple(ontUri.toString());
				local.setLocality(locality.getString());
				local.setType(type.toString());
				
				index++;
				result.put(index, local);
			}
		}
		

		return result;
	}	
	
	public List<Locality> getGeometriesInsideTripleStore() {
		List<Locality> result = new ArrayList<Locality>();
		//SPARQL query to retrieval all inside the gazetteer, Filter the places that don't have geometry (POINT, POLYGON, LINESTRING)


		//SPARQL query to retrieval all inside the gazetteer, Filter the places that don't have geometry (POINT, POLYGON, LINESTRING)

		String queryString ="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ " PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
				+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#>"
				+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ " SELECT ?instance ?geometry ?date ?locality ?contributors ?agreement ?wkt  ?object ?county"
				+ " WHERE {"
				+ " ?instance swi:locality ?locality ."
				+ " ?instance swi:contributors ?contributors."
				+ " ?instance swi:agreement ?agreement ."
				+ " OPTIONAL { ?instance swi:hasCounty ?county . }"
				+ " OPTIONAL { ?instance swi:date ?date . }"
				+ " ?instance geo:hasGeometry ?geometry."
				+ " ?geometry geo:asWKT ?wkt .}";
		System.out.println(queryString);
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

		ResultSet results= queryExecution.execSelect();
	
		while(results.hasNext()) {
			QuerySolution soln = results.nextSolution() ;
			Literal date =null;
			String county="";
			RDFNode ontUri = soln.get("?instance");
			RDFNode geoUri = soln.get("?geometry");
			//RDFNode type = soln.get("?subject");
			Literal locality = soln.get("locality").asLiteral();
			
			if(soln.getLiteral("date") !=null)
				date = soln.getLiteral("date").asLiteral();
			if(soln.getResource("county")!=null)
				county = soln.getResource("county").toString();
			
			Literal contributors = soln.getLiteral("contributors").asLiteral();
			Literal agree = soln.getLiteral("agreement").asLiteral();
			Literal wkt = soln.getLiteral("wkt").asLiteral();
			Locality local = new Locality();	
			local.setAgreeCoordinate(agree.getInt());
			local.setContributors(contributors.getInt());
			
			if(date!=null)
				local.setDate(date.getString());
			if(!county.equals(""))
				local.setCounty(county);
			
			local.setIdGeo(geoUri.toString());
			local.setIdTriple(ontUri.toString());
			local.setLocality(locality.getString());
			//local.setType(type.toString());
			local.setGeometry(wkt.getString());
			result.add(local);
		}
	
		return result;
	}	
	

	public boolean insertDataEndpoint(String queryString){
		
		HTTPBasicAuthFilter autenticar = new HTTPBasicAuthFilter("endpoint","endpoint");
		Client client = Client.create();
		client.addFilter(autenticar);
		
		WebResource webResource = client.resource(URL_endpoint);
		String input = "query="+queryString+"&view=HTML&format=HTML&handle=plain&submit=Update";
		
		webResource.cookie(new Cookie("user", "endpoint"));
		webResource.cookie(new Cookie("password", "endpoint"));
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("user", "endpoint");
		queryParams.add("password", "endpoint");
		
		
		ClientResponse response = webResource.queryParams(queryParams).type("application/x-www-form-urlencoded").post(ClientResponse.class, input);
		
		if (response.getStatus() != 200) {
			System.out.println("Failed : HTTP error code : "
					+ response.getStatus() +"  "+response.getResponseDate().toString());
			return false;
		}
		if(response.getStatus()==200)
			System.out.println("Data inserted!!");
		return true;
	}
	
	public HashMap<Integer,Locality> findPlacesPolygonMap(String geometry) {
		HashMap<Integer,Locality> result = new HashMap<Integer,Locality>();
		int index=1;
		
		return result;
	}

	public int getIndex() {
		String queryString="PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
				+ " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " SELECT (COUNT(?instance) AS ?NumOfTriples)"
				+ " WHERE {"
				+ " ?subject rdfs:subClassOf ?object ."
				+ " ?instance a ?subject .}";
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

		ResultSet results= queryExecution.execSelect();
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution() ;
			return solution.getLiteral("NumOfTriples").getInt()+1;
		}
		return 0;
	}

	public List<String> getTypes() {
		List<String> classes = new ArrayList<String>();
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>	"
				+ "SELECT DISTINCT (?o AS ?class) "
				+ " WHERE { "
				+ "	?s rdf:type ?o }";
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

		ResultSet results= queryExecution.execSelect();
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution() ;
			classes.add(solution.get("?class").toString());
		}
		
		return classes;
	}

	public String [] getInfo() {
		
		String value="",value2="";
		String queryString = "PREFIX  geo:  <http://www.opengis.net/ont/geosparql#>"
				+ " SELECT  (count(?instance) AS ?NumOfTriples)"
				+ " WHERE{"
				+ "    ?instance <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?subject"
				+ "    FILTER NOT EXISTS {?instance geo:hasGeometry ?o }"
				+ "  }";
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);

		ResultSet results= queryExecution.execSelect();
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution() ;
			value = ""+solution.getLiteral("NumOfTriples").getInt();
		}
		queryString = "PREFIX  geo:  <http://www.opengis.net/ont/geosparql#>"
				+ " SELECT  (count(?instance) AS ?NumOfTriples) "
				+ "WHERE{"
				+ "    ?instance geo:hasGeometry ?o .  }";
		query = QueryFactory.create(queryString) ;
		queryExecution=new QueryEngineHTTP(URL_endpoint,query);
		results= queryExecution.execSelect();
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution() ;
			value2 = ""+solution.getLiteral("NumOfTriples").getInt();
		}
		String[] info = new String[]{value,value2};
		return info;
	}

	public int getTempIndex() {
		String queryString = "SELECT (COUNT(?s) AS ?NumOfTriples) FROM <http://swigazetteer/temp> WHERE {	?s  <http://swigazetteer/temp/ontology/hasGeometry>  ?o }";
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);
		ResultSet results= queryExecution.execSelect();
		int value=0;
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution() ;
			value = solution.getLiteral("NumOfTriples").getInt();
		}
		return value;
	}

	public Geo getCentertemp(String idTriple) {
		Out_Polygon out = new Out_Polygon();
		List<Geo> pontos = new ArrayList<Geo>();
		String queryString = "SELECT ?point FROM <http://swigazetteer/temp> WHERE {	<"+idTriple+">  <http://swigazetteer/temp/ontology/hasGeometry>  ?geo ."
				+ " ?geo <http://www.opengis.net/ont/geosparql#asWKT> ?point }";
		System.out.println(queryString);
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		QueryEngineHTTP queryExecution=new QueryEngineHTTP(URL_endpoint,query);
		ResultSet results= queryExecution.execSelect();		
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution() ;
			String temp = solution.getLiteral("point").getString();
			
			if(temp.contains("POINT")){
				String value = temp.replaceAll(";http://www.opengis.net/def/crs/EPSG/0/4326", "");
				value = value.substring(6, value.length()-2);
				double x = out.transformFloat(value.split(" ")[0]);
			  	double y = out.transformFloat(value.split(" ")[1]);
			  	Geo p = new Geo(x,y);
			  	System.out.println("TEMP GRAPH  "+p);
				pontos.add(p);
			}
		}
		Geo centr =  centroid(pontos);
		return centr;
	}
	public Geo centroid(List<Geo> knots)  {
	    double centroidX = 0, centroidY = 0;
	    	for(Geo knot : knots) {
	            centroidX += knot.getLatitude();
	            centroidY += knot.getLongitude();
	        }
	       if(knots.size()>=1){
	    	   centroidX = centroidX / knots.size();
	    	   centroidY = centroidY / knots.size();
	    	System.out.println("centroidX: "+centroidX+"   "+"centroidY: "+centroidY);
	    	   return new Geo( centroidX, centroidY);
	    	   
	       }
	       System.out.println("voltando null");
	    return null;
	}
	
}