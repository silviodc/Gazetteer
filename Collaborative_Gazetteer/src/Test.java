import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import communicate_with_other_data_source.Geonames;

import analyze_geographical_coordinates.Out_Polygon;
import count_and_statistic_analyze.Count_Coordinates;
import TAD.Place;
import TAD.Repository;
import read_files.Read_Biodiversity_files;
import read_files.Transform_and_Filter;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
      
		
		Geonames g = new Geonames();
		try {
			g.acessGeonames();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		
		Read_Biodiversity_files rb = new Read_Biodiversity_files();
       Place p=null;
       try {
		rb.start_read();
		Transform_and_Filter tsf = new Transform_and_Filter();
		tsf.transform_Repository_to_Place(rb.getRepository());		
		int [][] years = Count_Coordinates.countDate(rb.getRepository().get(0).getPlaces(),rb.getRepository().get(0).getPolygon());
		int cont=0;
		for(int i=0;i<years.length;i++){
			cont +=years[i][1];
			System.out.println(years[i][0]+","+years[i][1]);
		}
		
		rb.getRepository().get(0).getNumbers().print_information();

		Out_Polygon out = new Out_Polygon();
		for(Repository r: rb.getRepository()){
			System.out.println("Repositorio "+r.getName()+" Fora do poligono "+out.count_out_Polygon(r.getPolygon(),r.getPlaces()));
		}
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
	}

}
