package read_files;

import java.util.ArrayList;

import TAD.Place;

public class Repository {
	private String path;
	private String name;
	private int columns[];
	private int allrecords;
	private int onlyLocalityAndCounty;
	private int onlyPlace;
	private int onlyCounty;
	private int noRecord;
	private ArrayList<String[]> data = new ArrayList<String[]>();
	
	private ArrayList<Place> places = new  ArrayList<Place>();
	
	public ArrayList<Place> getPlaces() {
		return places;
	}

	public void setPlaces(ArrayList<Place> places) {
		this.places = places;
	}

	public Repository(){}
	
	public Repository(String path, String name, int[] columns) {
		super();
		this.path = path;
		this.name = name;
		this.columns = columns;
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
	public int getAllrecords() {
		return allrecords;
	}
	public void setAllrecords(int allrecords) {
		this.allrecords = allrecords;
	}
	public int getOnlyLocalityAndCounty() {
		return onlyLocalityAndCounty;
	}
	public void setOnlyLocalityAndCounty(int onlyLocalityAndCounty) {
		this.onlyLocalityAndCounty = onlyLocalityAndCounty;
	}
	public int getOnlyPlace() {
		return onlyPlace;
	}
	public void setOnlyPlace(int onlyPlace) {
		this.onlyPlace = onlyPlace;
	}
	public int getOnlyCounty() {
		return onlyCounty;
	}
	public void setOnlyCounty(int onlyCounty) {
		this.onlyCounty = onlyCounty;
	}
	public int getNoRecord() {
		return noRecord;
	}
	public void setNoRecord(int noRecord) {
		this.noRecord = noRecord;
	}
	public ArrayList<String[]> getData() {
		return data;
	}
	public void setData(ArrayList<String[]> data) {
		this.data = data;
	}
	
	public void print_information(){
        System.out.println("Todos registros: "+allrecords);
        System.out.println("Somente local e municipio: "+onlyLocalityAndCounty);
        System.out.println("Somente local: "+onlyPlace);
        System.out.println("Somente municipio: "+onlyCounty);
        System.out.println("Nenhum registro: "+noRecord);
	}
	
}
