package TAD;

public class Statistics {
	private int allrecords;
	private int onlyLocalityAndCounty;
	private int onlyPlace;
	private int onlyCounty;
	private int noRecord;
	private int out_polygon;
	private int[][] coordinates_per_year;
	
		
	public int[][] getCoordinates_per_year() {
		return coordinates_per_year;
	}
	public void setCoordinates_per_year(int[][] coordinates_per_year) {
		this.coordinates_per_year = coordinates_per_year;
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
	
	public int getOut_polygon() {
		return out_polygon;
	}
	public void setOut_polygon(int out_polygon) {
		this.out_polygon = out_polygon;
	}
	public void print_information(){
        System.out.println("Todos registros: "+allrecords);
        System.out.println("Somente local e municipio: "+onlyLocalityAndCounty);
        System.out.println("Somente local: "+onlyPlace);
        System.out.println("Somente municipio: "+onlyCounty);
        System.out.println("Nenhum registro: "+noRecord);
	}
}
