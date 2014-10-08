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
	
	public ArrayList<Place> pull_query(){
		ArrayList<Place> dbpedia_places = new ArrayList<Place>();
		String service="http://dbpedia.org/sparql";
		String query="PREFIX dbo:<http://dbpedia.org/ontology/>" +
		"PREFIX : <http://dbpedia.org/resource/>" +
				"select ?person where {?person dbo:birthPlace :Eindhoven.}";
		QueryExecution qe=QueryExecutionFactory.sparqlService(service, query);
		ResultSet rs=qe.execSelect(); 
		while (rs.hasNext()){
			QuerySolution s=rs.nextSolution(); 
			System.out.println(s.getResource("?person").toString());
		}
		return dbpedia_places;
	}
}

