package Old_source;


import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo;
import com.bbn.openmap.geo.OMGeo.Polygon;
import java.util.HashMap;
import java.lang.Float;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Silvio
 */
public class TesteDentroPoligono {
   
    
    public static void main(String args[]){
        
        //23.93 33.23, 23.93 36.23, 22.63 33.23, 22.63 36.23, 23.93 33.23
         Geo listGeo[] = new Geo [5];
         listGeo[0] = new Geo(new Float(23.93), new Float(33.23));
         listGeo[1] = new Geo(new Float(23.93), new Float(36.23));
         listGeo[2] = new Geo(new Float(22.63), new Float(33.23));
         listGeo[3] = new Geo(new Float(22.63), new Float(36.23));
         listGeo[4] = new Geo(new Float(23.93), new Float(33.23));
       
         Polygon p = new Polygon(listGeo);
         System.out.println(p.isPointInside(new Geo(new Float(23.73333),new Float(35.68333))));
         
         System.out.println(p.isPointInside(new Geo(new Float(23.73333),new Float(39.68333))));
    }
}
