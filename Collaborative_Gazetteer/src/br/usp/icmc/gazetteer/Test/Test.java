/*    This file is part of SWI Gazetteer.

    SWI Gazetteer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SWI Gazetteer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SWI Gazetteer.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.usp.icmc.gazetteer.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import br.usp.icmc.gazetteer.AnalyzeGeographicalCoordinates.Out_Polygon;
import br.usp.icmc.gazetteer.Clustering.GazetterCluster;
import br.usp.icmc.gazetteer.CommunicateWithOtherDataSource.Build_Polygons_using_IBGE;
import br.usp.icmc.gazetteer.CommunicateWithOtherDataSource.DBpedia;
import br.usp.icmc.gazetteer.CommunicateWithOtherDataSource.Geonames;
import br.usp.icmc.gazetteer.CountAndStatisticAnalyze.Count_Coordinates;
import br.usp.icmc.gazetteer.ImproveCoordinates.Summarize;
import br.usp.icmc.gazetteer.MappingOntology.Mapping;
import br.usp.icmc.gazetteer.Maps.Build_coordinates;
import br.usp.icmc.gazetteer.PrepareSampleToCheck.Random_Sample;
import br.usp.icmc.gazetteer.PrepareSampleToCheck.SemanticQuery;
import br.usp.icmc.gazetteer.PutDataInTripleStore.Insert_Triple_Store;
import br.usp.icmc.gazetteer.ReadFiles.Read_Biodiversity_files;
import br.usp.icmc.gazetteer.ReadFiles.Transform_and_Filter;
import br.usp.icmc.gazetteer.TAD.County;
import br.usp.icmc.gazetteer.TAD.Group;
import br.usp.icmc.gazetteer.TAD.Place;

public class Test {

	public static void main(String[] args) {

		Random_Sample sample = new Random_Sample();
		Geonames geonames = new Geonames();
		DBpedia dbpedia = new DBpedia();
		Read_Biodiversity_files rb = new Read_Biodiversity_files();
		Transform_and_Filter tsf = new Transform_and_Filter();
		Out_Polygon out = new Out_Polygon();

		GazetterCluster clustering = new GazetterCluster();
		Summarize sumy = new Summarize();
		Mapping map = new Mapping();

		ArrayList<County> county = new ArrayList<County>();
		ArrayList<Group> group = new ArrayList<Group>();
		ArrayList<Place> all_place = new ArrayList<Place>();

		HashMap<Integer,Place> candidate_place = new HashMap<Integer,Place>();
		Build_coordinates mapa = new Build_coordinates();

		try {
			rb.read_Expression();
			rb.start_read();
			tsf.transform_Repository_to_Place(rb.getRepository());

			for (int i = 0; i < rb.getRepository().size(); i++) {
				mapa.KMLfile(rb.getRepository().get(i).getPolygon(), rb.getRepository().get(i).getPlaces(),"map"+i);
				System.out.println("Statistics: <<<<<<<");
				System.out.println("All records: "
						+ rb.getRepository().get(i).getNumbers()
						.getAllrecords());
				System.out.println("No record:  "
						+ rb.getRepository().get(i).getNumbers().getNoRecord());
				System.out.println("County: "
						+ rb.getRepository().get(i).getNumbers()
						.getOnlyCounty());
				System.out.println("Locality and County "
						+ rb.getRepository().get(i).getNumbers()
						.getOnlyLocalityAndCounty());
				System.out.println("Place "
						+ rb.getRepository().get(i).getNumbers()
						.getOnlyPlace());
				System.out.println("=========================");
				System.out.println("Repositorio "
						+ rb.getRepository().get(i).getName()
						+ " Fora do poligono "
						+ out.count_out_Polygon(rb.getRepository().get(i)
								.getPolygon(), rb.getRepository().get(i)
								.getPlaces()));
				out.clean_noise_coordinates(rb.getRepository().get(i)
						.getPolygon(), rb.getRepository().get(i).getPlaces());
				int lugar = 0;
				for (Place pl : rb.getRepository().get(i).getPlaces()) {
					if (pl.getGeometry() != null)
						lugar++;
				}
				System.out.println("Quantidade de coordenadas: " + lugar);
				int[][] years = Count_Coordinates.countDate(rb.getRepository()
						.get(i).getPlaces(), rb.getRepository().get(i)
						.getPolygon());
				Count_Coordinates.build_csv(years, rb.getRepository().get(i)
						.getName());

			}
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("Read all files DONE!!");

		for (int i = 0; i < rb.getRepository().size(); i++) {
			all_place.addAll(rb.getRepository().get(i).getPlaces());
		}
		for (int i = 0; i < rb.getRepository().size(); i++)
			rb.getRepository().get(i).getPlaces().clear();

		if (dbpedia.DBpediaWorks()) {
			all_place.addAll(geonames.getGeonamesPlaces());
			all_place.addAll(dbpedia.pull_query());
			county.addAll(dbpedia.getMunicipalityFromAmazonas(all_place));
		}




		System.out.println("Amount places: " + all_place.size());
		for(int i=0;i<all_place.size();i++){
			candidate_place.put(i, all_place.get(i));
		}
		group.addAll(GazetterCluster.buildKmeans(candidate_place,county));

		int total=0;
		for(Group p: group){
			total+=p.getPlaces().size();
		}

		System.out.println("Improve Coordinates....    "+total);
		sumy.referenciaGeo(group);
		System.out.println("Improve Coordinates DONE!!");

		System.out.println("coordinates improved "+Summarize.improved);


		total=0;
		for(Group p: group){
			total+=p.getPlaces().size();
		}
		System.out.println("Improve Coordinates....    "+total);


		for (int i = 0; i < rb.getRepository().size(); i++) {
			String name = rb.getRepository().get(i).getName();
			rb.getRepository().get(i).getPlaces().clear();
			for(Group g: group){
				if (g.getRepository().equalsIgnoreCase(name)) {
					rb.getRepository().get(i).getPlaces().addAll(g.getPlaces());
				}
			}
			System.out.println(rb.getRepository().get(i).getPlaces().size());
		}
		for(int i = 0; i<rb.getRepository().size();i++) {
			int relative_date[][] = Count_Coordinates.countDate(rb.getRepository().get(i).getPlaces(), rb.getRepository().get(i).getPolygon());
			Count_Coordinates.build_csv(relative_date, rb.getRepository().get(i).getName()+ "NewKStart");
		}
		total =0;
		for(int i = 0; i<rb.getRepository().size();i++) {
			for(int j=0; j< rb.getRepository().get(i).getPlaces().size();j++){
				if(rb.getRepository().get(i).getPlaces().get(j).getGeometry()!=null)
					total++;
			}
		}
		System.out.println("Total de coordenadas"+total);
		System.gc();

		candidate_place.clear();
		all_place.clear();
		all_place.addAll(Build_Polygons_using_IBGE.loadNationalParks());
		all_place.addAll(Build_Polygons_using_IBGE.loadReservas());
		for(int i=0;i<all_place.size();i++){
			candidate_place.put(i, all_place.get(i));
		}
		GazetterCluster.findComposite(candidate_place);
		for(int i=0;i<candidate_place.size();i++){
			Group gp =new Group();
			gp.setCentroid(candidate_place.get(i));
			gp.setRepository(candidate_place.get(i).getRepository());
			group.add(gp);
		}

		System.out.println("Mapping data ....");
		map.build_RDF(group);
		System.out.println("Mapping data DONE!!!");

		//
		System.out.println("Preparing sample...");
		sample.random_Centroid(group, 100,"KStartCentroid");
		sample.random_inner_Group(group, 50,"KStartInner");
		System.out.println("Data sample DONE!!!");
		//	
		//
		//	
	}
}