package communicate_with_other_data_source;

import com.hp.hpl.jena.query.QueryExecution; 
import com.hp.hpl.jena.query.QueryExecutionFactory; 
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import TAD.Place;

import java.util.ArrayList;

public class DBpedia {
	
	@SuppressWarnings("finally")
	public boolean DBpediaWorks() { 
		String service = "http://dbpedia.org/sparql"; 
		String query = "ASK { }";
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query); 
		try { 
			if (qe.execAsk()) { 
				System.out.println(service + " is UP"); }
			    return true;
			// end if 
			} catch (QueryExceptionHTTP e) {
				System.out.println(service + " is DOWN");
			}finally{ 
				qe.close();
				return false;
			}
		} 
	
	public ArrayList<String> pull_query(){
		ArrayList<String> dbpedia_places = new ArrayList<String>();
		String query="PREFIX dbpedia-owl:<http://dbpedia.org/ontology/> "
				+ "PREFIXX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "select *"
				+ " where { ?place rdf:type dbpedia-owl:Place ."
				+ " ?place dbpedia-owl:isPartOf <http://dbpedia.org/resource/North_Region,_Brazil>."
				+ " }";
		query(query,dbpedia_places);
	
		query = "select * where { ?place rdf:type dbpedia-owl:Place . ?s dbpedia-owl:isPartOf <http://dbpedia.org/resource/Amazonas_(Brazilian_state)>. }";
		query(query,dbpedia_places);
		
		query = "select * where { ?place ?p <http://dbpedia.org/resource/Category:Protected_areas_of_Amazonas_(Brazilian_state)> . }";
		query(query,dbpedia_places);
		
		query = "";
		
		return dbpedia_places;
	}
	public void query(String query, ArrayList<String> dbpedia_places){

		String service="http://dbpedia.org/sparql";
		
		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs=qe.execSelect(); 
		while (rs.hasNext()){
			QuerySolution s=rs.nextSolution(); 
			System.out.println(s.getResource("?place").toString());
			dbpedia_places.add(s.getResource("?place").toString());
		}
	}
}

class main {
	public static void main(String args[]){
		DBpedia db = new DBpedia();
		db.pull_query();
	}
}

