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
package br.usp.icmc.gazetteer.PrepareSampleToCheck;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


import com.bbn.openmap.geo.OMGeo;
import com.bbn.openmap.geo.OMGeo.Polygon;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import br.usp.icmc.gazetteer.AnalyzeGeographicalCoordinates.Out_Polygon;

public class SemanticQuery {
	
	private final String URL_endpoint = "http://biomac.icmc.usp.br:8080/swiendpoint/Query";
	
	public void prepareSample(String nameFile) throws IOException{
		HashMap<String,String> uris = new HashMap<String,String>();
		File file = new File(nameFile);
		FileWriter writer = new FileWriter(file);
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ " PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
				+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#>"
				+ " SELECT ?instance ?local"
				+ " WHERE {"
				+ " ?instance geo:hasGeometry ?geometry ."
				+ " ?instance swi:locality ?local"
				+ "} ORDER BY(?instance) ";
		System.out.println(queryString);
		@SuppressWarnings("resource")
		QueryEngineHTTP result = new QueryEngineHTTP(URL_endpoint, queryString);
		ResultSet rs = result.execSelect();
		while(rs.hasNext()){
			QuerySolution soln = rs.nextSolution() ;
			RDFNode ontUri = soln.get("?instance") ;
			Literal nome = soln.get("?local").asLiteral();
			uris.put(ontUri.toString(),nome.getString());
		}
		for(int i=0;i<8;i++){	
		Set<String> indexx = uris.keySet();
		Iterator<String> t = indexx.iterator();
		while(t.hasNext()){
			String temp = t.next();
				writer.write("Q"+(i+1)+" <"+temp+"> 0  --- "+uris.get(temp));
				writer.write("\n");
			}
		}
		writer.close();
	}
	
	public void verifyPlacesInsideJauPark(String path) throws IOException{
		File file = new File("files"+File.separator+"trustCoordinates"+File.separator+"manaus.txt");
		FileWriter writer = new FileWriter(file);
		Out_Polygon out = new Out_Polygon();
		Polygon p = Out_Polygon.buildPolygon(path);
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  "
				+ " PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
				+ " PREFIX swi: <http://www.semanticweb.org/ontologies/Gazetter#>"
				+ " SELECT ?instance ?geometry ?local ?municipio ?geo"
				+ " WHERE {"
				+ " ?subject rdfs:subClassOf ?object ."
				+ " ?instance a ?subject ."
				+ " ?instance geo:hasGeometry ?geometry ."
				+ " ?geometry geo:asWKT ?geo ."
				+ " ?instance swi:locality ?local ."
				+ " ?instance swi:county ?municipio."
				+ " FILTER EXISTS { ?instance geo:hasGeometry ?o } }";
		System.out.println(queryString);
		@SuppressWarnings("resource")
		QueryEngineHTTP result = new QueryEngineHTTP(URL_endpoint, queryString);
		ResultSet rs = result.execSelect();
		while(rs.hasNext()){
			QuerySolution soln = rs.nextSolution();
			Literal point = soln.getLiteral("?geo");
			String value = point.getString().replaceAll("<http://www.opengis.net/def/crs/EPSG/4326> POINT", "");
			value = (String) value.subSequence(1, value.length()-1);
			String [] data = value.split(",");
			System.out.println(OMGeo.distanceToEdge(out.transformFloat(data[0]),out.transformFloat(data[1]), p.getShape()));
			if( OMGeo.distanceToEdge(out.transformFloat(data[0]),out.transformFloat(data[1]), p.getShape())<100){
				writer.write(soln.get("?instance").toString());
				writer.write("\n");
				System.out.println("inside...");
			}
		}
		writer.close();
	}
	
	public void testQuery(){
		String queryString = "PREFIX opengis: <http://www.opengis.net/def/uom/OGC/1.0/> "
				+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
				+ " PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>"
				+ " select *"
				+ " where {"
				+ " ?s1 geo:asWKT ?o1 ."
				+ " ?s2 geo:asWKT ?o2."
				+ " FILTER(strdf:Disjoint(?o1, ?o2)) . }limit 100";
		System.out.println(queryString);
		@SuppressWarnings("resource")
		QueryEngineHTTP result = new QueryEngineHTTP(URL_endpoint, queryString);
		ResultSet rs = result.execSelect();
		while(rs.hasNext()){
			QuerySolution soln = rs.nextSolution();
			System.out.println(soln.get("?s1").toString());
		}
	}
}
