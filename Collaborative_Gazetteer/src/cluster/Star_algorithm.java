package cluster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import TAD.Expression;
import TAD.Group;
import TAD.Place;
import TAD.Place_descovered;
import TAD.Repository;


public class Star_algorithm {

	private final double similarity = 0.5;
	private static ArrayList<Place> ambiguoPlace =new ArrayList<Place>();
	
	public static ArrayList<Place> getAmbiguoPlace() {
		return ambiguoPlace;
	}

	public static void setAmbiguoPlace(ArrayList<Place> ambiguoPlace) {
		Star_algorithm.ambiguoPlace = ambiguoPlace;
	}

	public ArrayList<Group> start_clustering(ArrayList<Place> places, ArrayList<Expression> exp) throws InstantiationException, IllegalAccessException, CloneNotSupportedException, IOException{
	
		
		
		ArrayList<Place> candidate_place = new ArrayList<Place>();		
		ArrayList<Group> group = new ArrayList<Group>();
		for(Expression e:exp){
			Pattern pattern = Pattern.compile(e.getExpression());
				//Split names according with regular expression	
				for(Place pl: places ){				
					Matcher matcher = pattern.matcher(pl.getNameFilter());	
					while (matcher.find()) {
						 if(!pl.isUsed() && !pl.isAmbiguo() && uniqueExp(pl,exp,e)){ // look if the name is used or ambiguous
							 candidate_place.add(pl.clone());
							 pl.setUsed(true);
						 }else{					
								pl.setUsed(false);
								pl.setAmbiguo(true);// set this place as a ambiguous
							}
					 }
				}
					while(candidate_place.size()>0){		
						Random rand = new Random();
						Place centroid = candidate_place.get(rand.nextInt(candidate_place.size())); // get some centroid to start matching
						candidate_place.remove(centroid);//remove centroid from candidate places
						Group group_created = clustering_using_start(candidate_place,centroid);
						group_created.setExp(e);
						group_created.setCentroid(centroid);
						group_created.setRepository(centroid.getRepository());
						group.add(group_created);
					}
				
				candidate_place.clear();
		}
		for(int j=0;j<places.size();j++){
				if(places.get(j).isAmbiguo()){
					ambiguoPlace.add(places.get(j));
				}
		}
				return group;
	}
	

	private boolean uniqueExp(Place pl, ArrayList<Expression> expression, Expression e) {
		ArrayList<Expression> exp = new ArrayList<Expression>();
		exp.addAll(expression);
		for(int i=0;i<exp.size();i++){
		   if(exp.get(i).getId()==e.getId())
			   exp.remove(i);
		}
		for(Expression ex:exp){
			Pattern pattern = Pattern.compile(ex.getExpression());
			Matcher matcher = pattern.matcher(pl.getNameFilter());	
			while (matcher.find() && ex.getId()!=e.getId()) {
			//	System.out.println(ex.getExpression()+"  "+e.getExpression());
				return false;
			}
		}
		return true;
	}

	public Group clustering_using_start(ArrayList<Place> candidate_place,Place centroid) throws CloneNotSupportedException{
		
		// CLUSTERING!!! using star
		
		Group local_group = new Group();
		local_group.getPlaces().add(centroid);		
		
		Jaccard_Similarity jaccard = new Jaccard_Similarity(); // try resolve matching using jaccard similarity metric
		
		for(int i =0; i<candidate_place.size();i++){
			double value = jaccard.jaccardSimilarity(centroid.getNameFilter(),candidate_place.get(i).getNameFilter());
			if(value >= similarity && verific_county(candidate_place.get(i).getCounty(),centroid.getCounty())){
				
					
					local_group.getPlaces().add(candidate_place.get(i).clone());
					candidate_place.remove(candidate_place.get(i));
			//		  System.out.println(local_group.getPlaces().get(local_group.getPlaces().size()-1).getGeometry() +"  "+local_group.getPlaces().get(local_group.getPlaces().size()-1).getYear());
			}	
		}
		return local_group;
	}

	
	private boolean verific_county(String county, String county1) {
        
        if (county.equals(county1) || county1.equals("") || county1.equals("não informado") ||county.equals(" ")||county1.equals(" ") ||county==null ||county1==null) {
            return true;
        }
        return false;
    }
	
	
}
