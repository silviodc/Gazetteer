package communicate_with_other_data_source;	
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import TAD.County;
import TAD.Place;
import analyze_geographical_coordinates.Out_Polygon;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.CompressedFile;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.JMLReader;



public class Build_Polygons_using_IBGE {

	private static final int today = 2015;
	
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
				System.out.println(feature.getAttribute("NOME_UC1").toString());
				
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
		System.out.println("_____DONE NATIONAL PARKS_____");
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
				System.out.println(feature.getAttribute("NOMEMUNI2").toString().toLowerCase());
				String centroid = feature.getGeometry().getCentroid().toText().substring(7,feature.getGeometry().getCentroid().toText().length()-1);
				String [] temp = centroid.split(" ");
				c.setPoint(new Geo(Out_Polygon.transformFloat(temp[0]),Out_Polygon.transformFloat(temp[1])));
				countys.add(c);
			}
		}
		System.out.println("_________County DONE!____________");
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
				System.out.println(feature.getAttribute("NOME_UC1").toString());
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
		System.out.println("_______DONE RESERVAS_________");
		return places;
	}


}

