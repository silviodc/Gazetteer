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

public class Expression implements Serializable{
	    /**
	 * 
	 */
	private static final long serialVersionUID = 5358809359962697467L;
		private String name;
	    private String local;
	    private String expression;
	    private String feature;
	    private String ontology;
	    private int id;
	    
	    
	    public Expression(String name, String local, String expression, String feature,String ontology,int id) {
			this.name = name;
			this.local = local;
			this.expression = expression;
			this.feature = feature;
			this.ontology = ontology;
			this.id = id;
		}
	    public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getExpression() {
			return expression;
		}
		public void setExpression(String expression) {
			this.expression = expression;
		}
		public String getFeature() {
			return feature;
		}
		public void setFeature(String feature) {
			this.feature = feature;
		}
		public String getOntology() {
			return ontology;
		}
		public void setOntology(String ontology) {
			this.ontology = ontology;
		}
		public String getLocal() {
			return local;
		}
		public void setLocal(String local) {
			this.local = local;
		}
	    
	    
}
