/**
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
package br.usp.icmc.gazetteer.CommunicateWithOtherDataSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo.Polygon;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.util.FileManager;

import br.usp.icmc.gazetteer.AnalyzeGeographicalCoordinates.Out_Polygon;
import br.usp.icmc.gazetteer.Similarity.Metrics;
import br.usp.icmc.gazetteer.TAD.County;
import br.usp.icmc.gazetteer.TAD.Place;

public class DBpedia {

	public boolean DBpediaWorks() { 
		//		String service = "http://dbpedia.org/sparql"; 
		//		String query = "ASK { }";
		//		QueryExecution qe = QueryExecutionFactory.sparqlService(service, query); 
		//		try { 
		//			if (qe.execAsk()) { 
		//				System.out.println(service + " is UP"); }
		//			    return true;
		//			// end if 
		//			} catch (QueryExceptionHTTP e) {
		//				System.out.println(service + " is DOWN");
		//				return false;
		//			}finally{ 
		//				qe.close();			
		//			}
		return true;
	} 

	public ArrayList<Place> pull_query() throws NumberFormatException, FileNotFoundException, IOException{


		Polygon poly = Out_Polygon.buildPolygon("files"+File.separator+"trustCoordinates"+File.separator+"Amazonas_polygon.txt");
		String query="";
		ArrayList<Place> dbpedia_places = new ArrayList<Place>();


		query = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX dbpedia-owl:<http://dbpedia.org/ontology/> "
				+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/> "
				+ "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#> "
				+ "select * where { ?place ?p <http://dbpedia.org/resource/Category:Protected_areas_of_Amazonas_(Brazilian_state)> . "
				+ " ?place geo:lat ?lat . "
				+ " ?place geo:long ?long . "
				+ "?place foaf:name ?name . "
				+ "?place geo:geometry ?geo . }";
		query(query, dbpedia_places,poly);

		query = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
				+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
				+ "PREFIX dcterms:<http://purl.org/dc/terms/> "
				+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/> "
				+ "select * where { ?place rdf:type <http://dbpedia.org/class/yago/RiversOfBrazil> . "
				+ "?place foaf:name ?name . "
				+ " ?place geo:lat ?lat . "
				+ " ?place geo:long ?long . "
				+ "?place dcterms:subject <http://dbpedia.org/resource/Category:Tributaries_of_the_Amazon_River> . }";

		query(query, dbpedia_places,poly);
		query = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
				+ "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#> "
				+ "PREFIX dcterms:<http://purl.org/dc/terms/> "
				+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/> "
				+ "select * where { ?place rdf:type <http://dbpedia.org/class/yago/RiversOfBrazil> . "
				+ "?place foaf:name ?name . "
				+ " ?place geo:lat ?lat . "
				+ " ?place geo:long ?long . "
				+ "?place dcterms:subject <http://dbpedia.org/resource/Category:Amazon_Basin> . }";
		query(query, dbpedia_places,poly);

		query="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
				+ "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#> "
				+ "PREFIX dcterms:<http://purl.org/dc/terms/>"
				+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select * where {"
				+ "?place rdf:type <http://dbpedia.org/class/yago/WaterfallsOfBrazil> . "
				+ " ?place geo:lat ?lat . "
				+ " ?place geo:long ?long . }";
		query(query, dbpedia_places,poly);
		System.out.println(query+"   "+dbpedia_places.size());

		return dbpedia_places;
	}
	public void query(String queryString, ArrayList<Place> dbpedia_places, Polygon poly) throws NumberFormatException, FileNotFoundException, IOException{
		Out_Polygon out = new Out_Polygon();	

		String service="http://dbpedia.org/sparql";
		 
		// Execute the query and obtain results
		@SuppressWarnings("resource")
		QueryEngineHTTP result = new QueryEngineHTTP(service, queryString);
		ResultSet rs = result.execSelect();
		while (rs.hasNext()){			
			QuerySolution s=rs.nextSolution(); 
			try{
				float lat = s.getLiteral("?lat").getFloat();
				float log = s.getLiteral("?long").getFloat();
				if(out.insidePolygon(poly,lat,log)){
					String temp = Normalizer.normalize(s.getLiteral("name").toString().replaceAll("@en", ""), Normalizer.Form.NFD);  
					temp = temp.replaceAll("(?!\")\\p{Punct}", "").toLowerCase();
					System.out.println(temp+" "+s.getLiteral("?lat").getFloat()+" "+s.getLiteral("long").getFloat());
					dbpedia_places.add(new Place( temp, new Geo(lat,log)));
					dbpedia_places.get(dbpedia_places.size()-1).setNameFilter(s.getLiteral("name").toString());
				}
			}catch(Exception ex){
				System.out.println(ex.getCause());
			}
		}
		System.out.println("Search done!");
	}


	public static OntModel OpenConnectOWL(){
		String path = new File("files"+File.separator+"ontology"+File.separator+"dbpedia_2014.owl").getAbsolutePath();
		OntModel mod = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		java.io.InputStream in = FileManager.get().open(path);
		if(in == null){
			System.out.println("ERRO AO CARREGAR A ONTOLOGIA");
		}
		return (OntModel) mod.read(in,"");
	}

	public HashMap<String,String> findAmazonasCounty(){
		return null;

	}

	public List<County> getMunicipalityFromAmazonas(ArrayList<Place> places,String method) throws Exception{
		List<County> countys = new ArrayList<County>();
		String service="http://dbpedia.org/sparql";
		String queryString = "PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#> "
				+ " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " select * where {"
				+ " ?s <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:Populated_places_in_Amazonas_(Brazilian_state)>."
				+ " ?s rdfs:label ?nome ."
				+ " ?s geo:lat ?latitude ."
				+ " ?s geo:long ?longitude ."
				+ "FILTER(langMatches(lang(?nome), \"pt\"))}";
		System.out.println(queryString);
		@SuppressWarnings("resource")
		QueryEngineHTTP result = new QueryEngineHTTP(service, queryString);
		ResultSet rs = result.execSelect();
		while (rs.hasNext()){			
			QuerySolution s=rs.nextSolution(); 
			Literal nome = s.getLiteral("nome");
			Literal latitude = s.getLiteral("latitude");
			Literal longitude = s.getLiteral("longitude");
			RDFNode uri = s.get("s");

			String temp = Normalizer.normalize(nome.getString().replaceAll("@pt", ""), Normalizer.Form.NFD);  
			temp = temp.replaceAll("(?!\")\\p{Punct}", "").toLowerCase();

			County county = new County(temp);
			county.setURI(uri.toString());
			county.setPoint(new Geo(latitude.getFloat(),longitude.getFloat()));
			countys.add(county);
		}

		Metrics metric = new Metrics(method);
		List<County> IBGEcounty = Build_Polygons_using_IBGE.loadMunicipality();

		for(int i=0;i<countys.size();i++){
			for(int j=0;j<IBGEcounty.size();j++){

				String str1 = 	IBGEcounty.get(j).getNome();
				String str2 =	countys.get(i).getNome();
				if(metric.getSimilarity(str1, str2)>0.7){
					IBGEcounty.get(j).setURI(countys.get(i).getURI());
					IBGEcounty.get(j).setPoint(countys.get(i).getPoint());
				}

			}
		}

		for(int i=0;i<IBGEcounty.size();i++){
			for(int j=0;j<places.size();j++){
				if(places.get(j).getCounty()!=null && places.get(j).getCounty().getNome()!=null && !places.get(j).getCounty().getNome().equals("")) {
					String str1 = places.get(j).getCounty().getNome();
					String str2 = IBGEcounty.get(i).getNome();
					if(metric.getSimilarity(str1, str2)>0.7)
						places.get(j).setCounty(IBGEcounty.get(i));
				}
			}
		}
		return IBGEcounty;
	}

}

