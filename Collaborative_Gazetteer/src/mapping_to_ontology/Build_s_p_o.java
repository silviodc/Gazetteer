package mapping_to_ontology;

import java.io.IOException;
import java.util.ArrayList;

import TAD.Expression;
import TAD.Place;
import TAD.Repository;

public class Build_s_p_o {
	 public  void RDF_ind(ArrayList<Repository> repository,Expression e) throws IOException{
         String poly="";
         int total=0;                  
         for(Repository rep:repository){
        	 for(Place p: rep.getPlaces()){
        		
        		 if(p.getGeometry()!=null){
        			 total++;
                     String ind = "<!-- http://www.semanticweb.org/ontologies/Gazetter#/Geometry/"+(total)+" -->\n\n";
                     ind +="\t<owl:NamedIndividual rdf:about=\"&Gazetter;/Geometry/"+(total)+"\">\n";
                     ind +="\t\t<geosparql:asWKT rdf:datatype=\"http://www.opengis.net/ont/geosparql#wktLiteral\">\n";
                     ind +="\t\t\t<![CDATA[<http://www.opengis.net/def/crs/OGC/1.3/CRS84>Point("+p.getGeoBuilded()+")]]>\n";
                     ind +="\t\t</geosparql:asWKT> \n";
                     ind +="\t</owl:NamedIndividual>\n\n";
                     
                     ind+="<!-- http://www.semanticweb.org/ontologies/Gazetter#"+p.getType()+(total)+" -->\n";
                     ind+="\t<owl:NamedIndividual rdf:about=\"&Gazetter;"+p.getType()+total+"\">\n";
                     ind+="\t\t<rdf:type rdf:resource=\""+e.getOntology()+"\"/>\n";
                     ind+="\t\t<Gazetter:locality>"+p.getLocation()+"</Gazetter:locality>\n";
                     ind+="\t\t<Gazetter:county>"+p.getCounty()+"</Gazetter:county>\n";                   
                     if(p.getIspolygon()){
                         ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total-1)+"\"/>\n";
                         ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total)+"\"/>\n";
                     }else{
                           ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total)+"\"/>\n";
                     }
                     ind+="\t</owl:NamedIndividual>\n";
                     
                     System.out.println(ind);
        		 }
        		 
        	 }
         }
     }
}
