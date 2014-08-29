package put_data_in_Triple_Store;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import Old_source.Expressao;
import Old_source.Local;
import TAD.Expression;
import TAD.Place;
import TAD.Repository;

public class Insert_Triple_Store {
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
                     ind +="\t\t\t<![CDATA[<http://www.opengis.net/def/crs/OGC/1.3/CRS84>Point("+locais.get(i).getLatitude()+" "+locais.get(i).getLongitude()+")]]>\n";
                     ind +="\t\t</geosparql:asWKT> \n";
                     ind +="\t</owl:NamedIndividual>\n\n";
                    if(locais.get(i).getPoly()!=null){
                         total++;
                         ind += "<!-- http://www.semanticweb.org/ontologies/Gazetter#/Geometry/"+(total)+" -->\n\n";
                         ind +="\t\t<owl:NamedIndividual rdf:about=\"&Gazetter;/Geometry/"+(total)+"\">\n";
                         ind +="\t\t\t<geosparql:asWKT rdf:datatype=\"http://www.opengis.net/ont/geosparql#wktLiteral\">\n";
                         ind +="\t\t\t\t<![CDATA[<http://www.opengis.net/def/crs/OGC/1.3/CRS84>"+locais.get(i).getPoly()+"]]>\n";
                         ind +="\t\t\t</geosparql:asWKT> \n";
                         ind +="\t\t</owl:NamedIndividual>\n\n";
                         pol=true;
                     }
                     
                     
                     ind+="<!-- http://www.semanticweb.org/ontologies/Gazetter#"+e.getIngles()+(total)+" -->\n";
                     ind+="\t<owl:NamedIndividual rdf:about=\"&Gazetter;"+e.getIngles()+total+"\">\n";
                     ind+="\t\t<rdf:type rdf:resource=\""+e.getOnt()+"\"/>\n";
                     ind+="\t\t<Gazetter:locality>"+locais.get(i).getNome()+"</Gazetter:locality>\n";
                     ind+="\t\t<Gazetter:county>"+locais.get(i).getMunicipio()+"</Gazetter:county>\n";
                   
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
         
         
        
         
     }
}
