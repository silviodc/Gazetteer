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
package br.usp.icmc.gazetteer.CommunicateWithOtherDataSource;	
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import br.usp.icmc.gazetteer.AnalyzeGeographicalCoordinates.Out_Polygon;
import br.usp.icmc.gazetteer.TAD.County;
import br.usp.icmc.gazetteer.TAD.Place;
import br.usp.icmc.gazetteer.cluster.Star_algorithm;

import com.bbn.openmap.geo.Geo;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.CompressedFile;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.JMLReader;



public class Build_Polygons_using_IBGE {

	public static FeatureCollection read_IBGE(String path)throws Exception{

		String extension;
		File selectedFile = new File(path);
		FeatureCollection featureCollection;
		JMLReader jmlReader = new JMLReader();
		String name = selectedFile.getAbsolutePath();
		extension = name.substring(name.length() - 3);
		DriverProperties  dp = new DriverProperties();

		if (extension.equalsIgnoreCase("zip")) {
			String internalName;
			dp.set("CompressedFile", name);
			internalName = CompressedFile.getInternalZipFnameByExtension(".jml", name);
			if (internalName == null) {
				internalName = CompressedFile.getInternalZipFnameByExtension(".gml",name);
			}
			if (internalName == null) {
				internalName = CompressedFile.getInternalZipFnameByExtension(".xml",name);
			}

			if (internalName == null) {
				throw new Exception("Couldnt find a .jml, .xml, or .gml file inside the .zip file: " +name);
			}
			dp.set("File", internalName);
		} else if (extension.equalsIgnoreCase(".gz")) {
			dp.set("CompressedFile", name);
			dp.set("File", name); // not useed
		} else {
			dp.set("File", name);
		}
		// no "TemplateFile" specified, so read it from the top of the "File"
		featureCollection = jmlReader.read(dp);
		return featureCollection;
	}


	public static List<Place> loadNationalParks()throws Exception{
		List<Place> places = new ArrayList<Place>();
		
		String path1 ="files"+File.separator+"ucsfi.jml";
		String path2 ="files"+File.separator+"EstadoAmazonas.jml";

		FeatureCollection Amazonas = read_IBGE(path2);

		FeatureCollection featureCollection = read_IBGE(path1);

		Iterator<FeatureCollection> ite = Amazonas.iterator();
		BasicFeature amazonas = (BasicFeature) ite.next();

		Iterator<FeatureCollection> it = featureCollection.iterator();
		while(it.hasNext()){
			BasicFeature feature =  (BasicFeature) it.next();
			if(amazonas.getGeometry().intersects(feature.getGeometry())){    
				Star_algorithm.fLogger.log(Level.SEVERE,feature.getAttribute("NOME_UC1").toString());
				
				int begin = feature.getAttribute("DT_ULTIM10").toString().length()-4;
				int end = feature.getAttribute("DT_ULTIM10").toString().length();
				String ano = feature.getAttribute("DT_ULTIM10").toString().substring(begin, end);
				
				Place c = new Place(feature.getAttribute("NOME_UC1").toString().toLowerCase());
				c.setNameFilter(feature.getAttribute("NOME_UC1").toString().toLowerCase());
				c.setGoldStandart(true);
				c.setWktgoldStandart(feature.getGeometry().toText());
				c.setRepository("IBGE");
				c.setYear(Integer.parseInt(ano));
				String centroid = feature.getGeometry().getCentroid().toText().substring(7,feature.getGeometry().getCentroid().toText().length()-1);
				String [] temp = centroid.split(" ");
				c.setGeometry(new Geo(Out_Polygon.transformFloat(temp[0]),Out_Polygon.transformFloat(temp[1])));
				places.add(c);								
			}
		}
		Star_algorithm.fLogger.log(Level.SEVERE,"_____DONE NATIONAL PARKS_____");
		return places;
	}


	public static List<County> loadMunicipality()throws Exception{
		List<County> countys = new ArrayList<County>();
		String path1 ="files"+File.separator+"municip07.jml";
		String path2 ="files"+File.separator+"EstadoAmazonas.jml";

		FeatureCollection Amazonas = read_IBGE(path2);

		FeatureCollection featureCollection = read_IBGE(path1);

		Iterator<FeatureCollection> ite = Amazonas.iterator();
		BasicFeature amazonas = (BasicFeature) ite.next();

		Iterator<FeatureCollection> it = featureCollection.iterator();
		while(it.hasNext()){
			BasicFeature feature =  (BasicFeature) it.next();
			if(amazonas.getGeometry().intersects(feature.getGeometry())){
				County c = new County(feature.getAttribute("NOMEMUNI2").toString().toLowerCase());
				c.setWktPolygon(feature.getGeometry().toText());
				Star_algorithm.fLogger.log(Level.SEVERE,feature.getAttribute("NOMEMUNI2").toString().toLowerCase());
				String centroid = feature.getGeometry().getCentroid().toText().substring(7,feature.getGeometry().getCentroid().toText().length()-1);
				String [] temp = centroid.split(" ");
				c.setPoint(new Geo(Out_Polygon.transformFloat(temp[0]),Out_Polygon.transformFloat(temp[1])));
				countys.add(c);
			}
		}
		Star_algorithm.fLogger.log(Level.SEVERE,"_________County DONE!____________");
		return countys;
	}

	public static List<Place> loadReservas() throws Exception{
		List<Place> places = new ArrayList<Place>();
		String path1 ="files"+File.separator+"ucstodas.jml";
		String path2 ="files"+File.separator+"EstadoAmazonas.jml";

		FeatureCollection Amazonas = read_IBGE(path2);

		FeatureCollection featureCollection = read_IBGE(path1);

		Iterator<FeatureCollection> ite = Amazonas.iterator();
		BasicFeature amazonas = (BasicFeature) ite.next();

		Iterator<FeatureCollection> it = featureCollection.iterator();
		while(it.hasNext()){
			BasicFeature feature =  (BasicFeature) it.next();
			if(amazonas.getGeometry().intersects(feature.getGeometry())){    
				Star_algorithm.fLogger.log(Level.SEVERE,feature.getAttribute("NOME_UC1").toString());
				int begin = feature.getAttribute("DT_ULTIM10").toString().length()-4;
				int end = feature.getAttribute("DT_ULTIM10").toString().length();
				String ano = feature.getAttribute("DT_ULTIM10").toString().substring(begin, end);
				Place c = new Place(feature.getAttribute("NOME_UC1").toString().toLowerCase());
				c.setNameFilter(feature.getAttribute("NOME_UC1").toString().toLowerCase());
				c.setGoldStandart(true);
				c.setWktgoldStandart(feature.getGeometry().toText());
				c.setRepository("IBGE");
				c.setYear(Integer.parseInt(ano));
				String centroid = feature.getGeometry().getCentroid().toText().substring(7,feature.getGeometry().getCentroid().toText().length()-1);
				String [] temp = centroid.split(" ");
				c.setGeometry(new Geo(Out_Polygon.transformFloat(temp[0]),Out_Polygon.transformFloat(temp[1])));
				places.add(c);
			}
		}
		Star_algorithm.fLogger.log(Level.SEVERE,"_______DONE RESERVAS_________");
		return places;
	}


}

