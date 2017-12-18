/*
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
package br.usp.icmc.gazetteer.Maps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import br.usp.icmc.gazetteer.AnalyzeGeographicalCoordinates.Out_Polygon;
import br.usp.icmc.gazetteer.TAD.Place;

import com.bbn.openmap.geo.OMGeo;

public class Build_coordinates {

	public void KMLfile(OMGeo.Polygon geo, ArrayList<Place> places,String name) throws FileNotFoundException, UnsupportedEncodingException, IOException{
	       System.out.println("Building KML file");
	       File aq1 = new File(name+".kml").getAbsoluteFile(); 
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
			                " <coordinates>"+p.getGeometry().getLongitude()+","+p.getGeometry().getLatitude()+"</coordinates>\n" +
			                " </Point>\n </Placemark>\n";
			                amostg.write(template);
			                amostg.flush();
			       } 
	           }
	       }
	       template = "<Placemark> <Polygon> <outerBoundaryIs> <LinearRing> <coordinates>  </coordinates> </LinearRing> </outerBoundaryIs> </Polygon> </Placemark>";
	       amostg.write(template);
           amostg.flush();
	       template="  </Document>  </kml>";
	       amostg.write(template);
	       amostg.flush();
	       amostg.close();
	       System.out.println("KML file built");
	       
	   }
}
