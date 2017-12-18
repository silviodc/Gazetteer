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
package br.usp.icmc.gazetteer.SemanticSearchTest;

/**
 *
 * @author Silvio
 */
public class Query {
    private double recalprecision[][];
    private double interpoled[][];
    private double onzepontos[][];

    public Query(double[][] recalprecision) {
        this.recalprecision = recalprecision;
    }

    public Query() { }

    
    
    public double[][] getRecalprecision() {
        return recalprecision;
    }

    public void setRecalprecision(double[][] recalprecision) {
        this.recalprecision = recalprecision;
    }

    public double[][] getInterpoled() {
        return interpoled;
    }

    public void setInterpoled(double[][] interpoled) {
        this.interpoled = interpoled;
    }

    public double[][] getOnzepontos() {
        return onzepontos;
    }

    public void setOnzepontos(double[][] onzepontos) {
        this.onzepontos = onzepontos;
    }
    
}
