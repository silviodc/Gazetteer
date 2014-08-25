package TAD;

import java.util.ArrayList;

import analyze_geographical_coordinates.Out_Polygon;

import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;

public class Repository {
	private String path;
	private String name;
	private int columns[];
    private Statistics numbers;
	private ArrayList<Place> places = new  ArrayList<Place>();
	private OMGeo.Polygon polygon;
	
	public Repository(){}
	
	public Repository(String path, String name, int[] columns) {
		super();
		this.path = path;
		this.name = name;
		this.columns = columns;
	}
	
	public OMGeo.Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(OMGeo.Polygon polygon) {
		this.polygon = polygon;
	}

	public ArrayList<Place> getPlaces() {
		return places;
	}

	public void setPlaces(ArrayList<Place> places) {
		this.places = places;
	}

	public String getPath() {
		return path;
	}
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setPath(String path) {
		this.path = path;
	}
	public int[] getColumns() {
		return columns;
	}
	public void setColumns(int[] columns) {
		this.columns = columns;
	}
	
	public void setData(String[] data) {
		if(!data[3].equals("")&& !data[4].equals("") && !data[0].equals("")){
			float x =transformFloat(data[3]);
            float y = transformFloat(data[4]);
			//constructor (int year, String location, String nameFilter, String county, Geo geometry)
			 places.add(new Place(Integer.parseInt(data[0]),data[1]," ",data[2], new Geo(x,y)));
			 }else if(data[0].equals("")){
				 places.add(new Place(Integer.MAX_VALUE,data[1]," ",data[2]));
			 }
	}
	public Statistics getNumbers() {
		return numbers;
	}
	public void setNumbers(Statistics numbers) {
		this.numbers = numbers;
	}

	private float transformFloat(String numero) {
	        float valor = 0;
	        char n[] = numero.toCharArray();
	        numero = "";
	        for (int i = 0; i < n.length; i++) {
	            if (n[i] == ',') {
	                numero += ".";
	            }
	            numero += n[i];
	        }
	        try {
	            valor = Float.parseFloat(numero);
	        } catch (Exception e) {
	            return 0;
	        }
	        return valor;
	    }
}
