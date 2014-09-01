package put_data_in_Triple_Store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import TAD.Expression;
import TAD.Place;
import TAD.Repository;

public class Insert_Triple_Store {
	 public  void RDF_ind(ArrayList<Repository> repository,Expression e) throws IOException{
         boolean pol=false;
         int total=0;         
         File aq1 = new File("Individuals.owl").getAbsoluteFile(); 
	     BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aq1),"UTF-8"));  
         for(Repository rep:repository){
        	 for(Place p: rep.getPlaces()){
        		 if(p.getGeometry()!=null){
        			 total++;
                     String ind = "<!-- http://www.semanticweb.org/ontologies/Gazetter#/Geometry/"+(total)+" -->\n\n";
                     ind +="\t<owl:NamedIndividual rdf:about=\"&Gazetter;/Geometry/"+(total)+"\">\n";
                     ind +="\t\t<geosparql:asWKT rdf:datatype=\"http://www.opengis.net/ont/geosparql#wktLiteral\">\n";
                     ind +="\t\t\t<![CDATA[<http://www.opengis.net/def/crs/OGC/1.3/CRS84>Point("+p.getGeometry().getLatitude()+" "+p.getGeometry().getLongitude()+")]]>\n";
                     ind +="\t\t</geosparql:asWKT> \n";
                     ind +="\t</owl:NamedIndividual>\n\n";
                    if(p.getPolygon()!=null){
                         total++;
                         ind += "<!-- http://www.semanticweb.org/ontologies/Gazetter#/Geometry/"+(total)+" -->\n\n";
                         ind +="\t\t<owl:NamedIndividual rdf:about=\"&Gazetter;/Geometry/"+(total)+"\">\n";
                         ind +="\t\t\t<geosparql:asWKT rdf:datatype=\"http://www.opengis.net/ont/geosparql#wktLiteral\">\n";
                         ind +="\t\t\t\t<![CDATA[<http://www.opengis.net/def/crs/OGC/1.3/CRS84>POLYGON(("+p.getPolygon()+"))]]>\n";
                         ind +="\t\t\t</geosparql:asWKT> \n";
                         ind +="\t\t</owl:NamedIndividual>\n\n";
                         pol=true;
                     }
                     
                     
                     ind+="<!-- http://www.semanticweb.org/ontologies/Gazetter#"+e.getName()+(total)+" -->\n";
                     ind+="\t<owl:NamedIndividual rdf:about=\"&Gazetter;"+e.getName()+total+"\">\n";
                     ind+="\t\t<rdf:type rdf:resource=\""+e.getOntology()+"\"/>\n";
                     ind+="\t\t<Gazetter:locality>"+p.getLocation()+"</Gazetter:locality>\n";
                     ind+="\t\t<Gazetter:county>"+p.getCounty()+"</Gazetter:county>\n";
                   
                     if(pol){
                         ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total-1)+"\"/>\n";
                         ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total)+"\"/>\n";
                     }else{
                           ind+="\t\t<geosparql:hasGeometry rdf:resource=\"&Gazetter;/Geometry/"+(total)+"\"/>\n";
                     }
                     ind+="\t</owl:NamedIndividual>\n";
                     bw.write(ind);
                     bw.flush();
                   
        		 }
        		 
        	 }
         }
         bw.close(); 
     }
}
