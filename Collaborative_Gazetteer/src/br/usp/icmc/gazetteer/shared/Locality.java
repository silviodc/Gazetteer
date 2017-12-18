package br.usp.icmc.gazetteer.shared;

import java.io.Serializable;

public class Locality implements Serializable  {

	private static final long serialVersionUID = 1L;

	/*
	 * [0] => placeSlected (String)
	 * [1] => agree_text (String)  ok
	 * [2] => place_text (String) ok
	 * [3] => county_text (String) ok
	 * [4] => species_text (String) ok
	 * [5] => communCoordinate (String) ok
	 * [6] => placeLink (String) ok
	 * 
	 */
	private String locality;
	private String county="";
	private String geometry="";
	private String userId;
	private int agreeCoordinate;
	private int contributors;
	private String idTriple;
	private String date="";
	private String type;
	private boolean updateGeo=false;
	private String idGeo="";
	private String ntriplas;
	
	public Locality(){}
	
	public Locality(String locality, String geo, String user,String county, int agree,String date, int contributors, String link,String type) {
		this.locality = locality;
		this.geometry = geo;
		this.userId= user;
		this.agreeCoordinate = agree;
		this.contributors = contributors;
		this.idTriple = link;
		this.county = county;
		this.date = date;
		this.type = type;
	}

	public String getLocality() {
		return locality;
	}



	public String getNtriplas() {
		return ntriplas;
	}

	public void setNtriplas(String ntriplas) {
		this.ntriplas = ntriplas;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}



	public String getCounty() {
		return county;
	}



	public void setCounty(String county) {
		this.county = county;
	}



	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGeometry() {
		return geometry;
	}



	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}



	public String getUserId() {
		return userId;
	}



	public int getContributors() {
		return contributors;
	}

	public void setContributors(int contributors) {
		this.contributors = contributors;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getAgreeCoordinate() {
		return agreeCoordinate;
	}

	public void setAgreeCoordinate(int agreeCoordinate) {
		this.agreeCoordinate = agreeCoordinate;
	}

	public String getPlaceLink() {
		return idTriple;
	}

	public void setPlaceLink(String placeLink) {
		this.idTriple = placeLink;
	}

	public String getIdTriple() {
		return idTriple;
	}

	public void setIdTriple(String idTriple) {
		this.idTriple = idTriple;
	}

	public boolean isUpdateGeo() {
		return this.updateGeo;
	}

	public void setUpdateGeo(boolean existGeo) {
		this.updateGeo= existGeo;
	}

	public String getIdGeo() {
		return idGeo;
	}

	public void setIdGeo(String idGeo) {
		this.idGeo = idGeo;
	}
	
	
}
