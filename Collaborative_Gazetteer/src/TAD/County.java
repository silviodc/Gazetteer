package TAD;
import java.io.Serializable;

import com.bbn.openmap.geo.Geo;
import com.hp.hpl.jena.rdf.model.RDFNode;
public class County implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1422609559550447455L;
	private String URI;
	private String nome;
	private Geo point;
	private String wktPolygon;
	
	
	
	public String getWktPolygon() {
		return wktPolygon;
	}

	public void setWktPolygon(String wktPolygon) {
		this.wktPolygon = wktPolygon;
	}

	public County(String nome){
		this.nome = nome;
	}
	
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Geo getPoint() {
		return point;
	}
	public void setPoint(Geo point) {
		this.point = point;
	}
	
	
	
}
