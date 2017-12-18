package br.usp.icmc.gazetteer.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;




import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.GeoArray;
import com.bbn.openmap.geo.OMGeo;
import com.bbn.openmap.geo.OMGeo.Polygon;

public class Out_Polygon {
	
	private OMGeo.Polygon poly;
	
	public OMGeo.Polygon getPoly() {
		return poly;
	}

	public void setPoly(OMGeo.Polygon poly) {
		this.poly = poly;
	}

	public boolean insidePolygon(OMGeo.Polygon poly, float x, float y){       
	       return poly.isPointInside(new Geo(x,y));
	}
	
	public boolean insidePolygon(OMGeo.Polygon poly, Geo point){       
	       return poly.isPointInside(point);
	}
	
	public int count_out_Polygon(OMGeo.Polygon poly, ArrayList<Locality> places){
		int out_polygon=0;
		for(Locality p: places){
			if(p.getGeometry()!=null)
				if(!insidePolygon(poly,transform(p.getGeometry()))){
					out_polygon++;	
				}
		}
		
		return out_polygon;
	}
	
	private Geo transform(String geometry) {
		return null;
	}

	public void clean_noise_coordinates(OMGeo.Polygon poly, ArrayList<Locality> places){
		for(int i=0;i<places.size();i++){
			if(places.get(i).getGeometry()!=null)
				if(!insidePolygon(poly,transform(places.get(i).getGeometry()))){
					places.get(i).setGeometry(null);	
				}
		}
		
	}
	//-60.266527122919086 -1.9861605335312493, -60.232453280512786 -2.0028559334131457, -60.22205287356857 -2.0005934285007587,
	 public String buildPolygon(String geometry,boolean sparql){
		 geometry = geometry.substring(10,geometry.length()-2);
		 System.out.println(geometry);
		 //-60.266527122919086 -1.9861605335312493,
		//-60.232453280512786 -2.0028559334131457,
	        String vetor [] = geometry.split(",");
	        String geo="";
            for(int i=0;i<vetor.length;i++){
            	   if(vetor[i].charAt(0)==' ')
            	   vetor[i] = vetor[i].substring(1);
                   String [] coordinate = vetor[i].split(" ");
                   geo+=coordinate[1]+" "+coordinate[0]+",";
         //          System.out.println(coordinate[1]+" "+coordinate[0]+"      "+coordinate.length);
               }
            geo =geo.substring(0, geo.length()-1);
            geo ="POLYGON(("+geo+"))";
            System.out.println(geo);
               return geo;
	 }
	
	 public OMGeo.Polygon buildPolygon(String path) throws FileNotFoundException, IOException{
	       
	        String line;
	        ArrayList<Float> p1 =new ArrayList<Float>();
	        ArrayList<Float> p2 =new ArrayList<Float>();
	        File arq = new File(path).getAbsoluteFile();
	        BufferedReader read =  new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
	           
	        while((line = read.readLine())!=null){ 
	                
	             String vetor [] = line.split(" ");
	             for(int i=0;i<vetor.length;i++){
	                    String [] coordinate = vetor[i].split(",");
	                    float temp =transformFloat(coordinate[0]);
	                    float temp1 = transformFloat(coordinate[1]);
	                    p1.add(temp);
	                    p2.add(temp1);
	                }
	        }
	        Geo listGeo[] = new Geo [p1.size()];
	        for(int i=0;i<p1.size();i++){
	                listGeo[i]= new Geo(p2.get(i),p1.get(i));
	        }
	        OMGeo.Polygon poly = new Polygon(listGeo);    
	        return poly;
	    }
	    
	 public float transformFloat(String numero) {
	        float valor = 0;
	        char n[] = numero.toCharArray();
	        numero = "";
	        for (int i = 0; i < n.length; i++) {
	            if (n[i] == ',') {
	                numero += ".";
	            }
	            numero += n[i];
	        }
	        try {
	            valor = Float.parseFloat(numero);
	        } catch (Exception e) {
	            return 0;
	        }
	        return valor;
	    }
	 
	 
	 
}
