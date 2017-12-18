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
package br.usp.icmc.gazetteer.TAD;
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
