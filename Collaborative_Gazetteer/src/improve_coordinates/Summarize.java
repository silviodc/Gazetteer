package improve_coordinates;

import java.util.ArrayList;
import java.util.Arrays;


import com.bbn.openmap.geo.Geo;


public class Summarize {
	
	/*	   public static void referenciaGeo(ArrayList<Local> locais,Poligono geo, Expressao e){
	        ArrayList<Geo> points = new ArrayList<Geo>();
	        ArrayList<String> Municipio= new ArrayList<String>();
	                for (int i = 0; i < locais.get(locais.size() - 1).getSemelhantes().size(); i++) {
	                    
	                    String lati = locais.get(locais.size()-1).getSemelhantes().get(i).getLatitude();
	                    String longi = locais.get(locais.size()-1).getSemelhantes().get(i).getLongitude();
	                    String mun =  locais.get(locais.size()-1).getSemelhantes().get(i).getMunicipio();
	                    if(!mun.equals("N��O INFORMADO") && !mun.equals("") && mun!=null){
	                        Municipio.add(locais.get(locais.size()-1).getSemelhantes().get(i).getMunicipio());
	                    }
	                    float l1=0,l2=0 ;
	                    if(lati != null && longi!= null){
	                        l1 = transformaFloat(lati);                   
	                        l2 = transformaFloat(longi);
	                    
	                    if (!lati.equals("") && !longi.equals("")) {
	                        if(geo.insidePolygon(l1, l2)){
	                          points.add(new Geo(l1,l2));
	                        }else{
	                            contLatfora++;
	                        }
	                   //     System.out.println("add ponto");
	                    }
	                    }
	                }
	                if(points.size()>1){
	                    double distances[] = new double[points.size()];
	                    for(int i=0;i<points.size();i++){
	                        distances[i]=points.get(i).length();
	                    }

	                    Arrays.sort(distances);
	                   
	                    int indice=0;
	                    int maior=0;
	                    int index=0;
	                    for(int j=0;j<distances.length;j++){
	                        double first=distances[j];
	                        for(int i=0;i<distances.length;i++){
	                            if(distances[i]==first){
	                                 indice++;
	                            }
	                            if(indice>maior){
	                                maior = indice;
	                                index = i;
	                            }
	                        }
	                    }
	                   
	                   locais.get(locais.size()-1).setLatitude(points.get(index).getLatitude());
	                   locais.get(locais.size()-1).setLongitude(points.get(index).getLongitude());
	                   if(Municipio.size()>1 && e.getPoly().equals("1"))
	                   locais.get(locais.size()-1).setMunicipio(Municipio.get((int)Municipio.size()/2));
	                   for(int n=0;n<geo.getPolys().size();n++){
	                       if(geo.dentro(geo.getPolys().get(n), points.get(index))){
	                           locais.get(locais.size()-1).setPoly(geo.getPoligonos().get(n));
	                           break;
	                       }
	                   }
	                }
	                
	    }*/
}
