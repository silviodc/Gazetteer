package maps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import analyze_geographical_coordinates.Out_Polygon;

import com.bbn.openmap.geo.OMGeo;

import TAD.Place;

public class Build_coordinates {

	public void KMLfile(OMGeo.Polygon geo, ArrayList<Place> places) throws FileNotFoundException, UnsupportedEncodingException, IOException{
	       System.out.println("Building KML file");
	       File aq1 = new File("coordenatesgbif.kml").getAbsoluteFile(); 
	       BufferedWriter amostg = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aq1),"UTF-8"));  
	       String template ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	       "<kml xmlns=\"http://www.opengis.net/kml/2.2\">  <Document> \n";
	       amostg.write(template);
	       amostg.flush();
	       Out_Polygon out_polygon = new Out_Polygon();
	       for(Place p:places){                           
	          if(p.getGeometry()!=null){
			       if(!out_polygon.insidePolygon(geo, p.getGeometry())){
			                template= "<Placemark>\n <Point>\n" +
			                " <coordinates>"+p.getGeometry()+","+p.getGeometry()+"</coordinates>\n" +
			                " </Point>\n </Placemark>\n";
			                amostg.write(template);
			                amostg.flush();
			       } 
	           }
	       }
	       template="  </Document>  </kml>";
	       amostg.write(template);
	       amostg.flush();
	       amostg.close();
	       System.out.println("KML file built");
	       
	   }
}
