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
package br.usp.icmc.gazetteer.MappingOntology;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.impl.LiteralLabel;

public class WktLiteral extends BaseDatatype {
    public static final String TypeURI = "http://strdf.di.uoa.gr/ontology#WKT";

    public static final String CRS84 = "http://www.opengis.net/def/crs/EPSG/0/4326";

    public static final RDFDatatype wktLiteralType = new WktLiteral();

    private WktLiteral() {
        super(WktLiteral.TypeURI);
    }

    /**
     * Convert a value of this datatype out
     * to lexical form.
     */
    public String unparse(Object value) {
        return value.toString();
    }

    /**
     * Parse a lexical form of this datatype to a value
     */
    public Object parse(String lexicalForm) {
        return new TypedValue(String.format("%s %s", WktLiteral.CRS84, lexicalForm), this.getURI());
    }

    /**
     * Compares two instances of values of the given datatype.
     * This does not allow rationals to be compared to other number
     * formats, Lang tag is not significant.
     *
     * @param value1 First value to compare
     * @param value2 Second value to compare
     * @return Value to determine whether both are equal.
     */
    public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
        return value1.getDatatype() == value2.getDatatype()
                && value1.getValue().equals(value2.getValue());
    }
}