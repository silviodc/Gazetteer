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

import java.math.BigDecimal;

/**
 *
 * @author Silvio
 */
public class Documento {
    private int numQ;
    private String filename;
    private int relevante;
    private BigDecimal precisao;
    private BigDecimal recall;

    public BigDecimal getPrecisao() {
        return precisao;
    }

    public void setPrecisao(BigDecimal precisao) {
        this.precisao = precisao;
    }

    public BigDecimal getRecall() {
        return recall;
    }

    public void setRecall(BigDecimal recall) {
        this.recall = recall;
    }
    

    public Documento(int numQ, String filename, int relevante) {
        this.numQ = numQ;
        this.filename = filename;
        this.relevante = relevante;
    }

    public int getNumQ() {
        return numQ;
    }

    public void setNumQ(int numQ) {
        this.numQ = numQ;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getRelevante() {
        return relevante;
    }

    public void setRelevante(int relevante) {
        this.relevante = relevante;
    }
    
 
   
}
