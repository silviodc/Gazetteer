package cluster;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TAD.Expression;
import TAD.Group;
import TAD.Place;


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
	
		findComposite(places,exp);
		
		ArrayList<Place> candidate_place = new ArrayList<Place>();
		ArrayList<Place> composite = new ArrayList<Place>();
		ArrayList<Group> group = new ArrayList<Group>();
		for(Expression e:exp){
			Pattern pattern = Pattern.compile(e.getExpression());
				//Split names according with regular expression	
				for(Place pl: places ){				
					Matcher matcher = pattern.matcher(pl.getNameFilter());	
					while (matcher.find()) {
						 if(!pl.isAmbiguo() && !pl.isUsed()){ // look if the name is used or ambiguous
							 candidate_place.add(pl);
							 pl.setUsed(true);
						 }else if(!pl.isUsed()){
							 composite.add(pl);
							 pl.setUsed(true);
						 }
					 }
				}
			    agroup(candidate_place,e,group);
				candidate_place.clear();
				System.out.println("*");
		}
		agroup(composite,group);
		composite.clear();
		return group;
	}
	
	private void agroup(ArrayList<Place> candidate_place, Expression e,ArrayList<Group> group) throws CloneNotSupportedException{
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
	}
	private void agroup(ArrayList<Place> candidate_place, ArrayList<Group> group) throws CloneNotSupportedException{
		while(candidate_place.size()>0){		
			Random rand = new Random();
			Place centroid = candidate_place.get(rand.nextInt(candidate_place.size())); // get some centroid to start matching
			candidate_place.remove(centroid);//remove centroid from candidate places
			Group group_created = clustering_using_start(candidate_place,centroid);
			group_created.setCentroid(centroid);
			group_created.setRepository(centroid.getRepository());
			group.add(group_created);
		}
	}

	private Collection<? extends Place> resolveCompositePlaces(ArrayList<Expression> expression) throws CloneNotSupportedException {
		ArrayList<Place> places = new ArrayList<Place>();
		ArrayList<String> composite = new ArrayList<String>();
		while(ambiguoPlace.size()>0){
			Place temp1 = ambiguoPlace.get(0);
			String test = temp1.getLocation().toLowerCase();
			test = Normalizer.normalize(test, Normalizer.Form.NFD);  
			test = test.replaceAll("[^\\p{ASCII}]", "");  
			String temp[] = test.split(" ");
			for(int i=0; i<temp.length;i++){
				for(Expression ex:expression){
					Pattern pattern = Pattern.compile(ex.getExpression());
					Matcher matcher = pattern.matcher(temp[i]+" .");	
					while (matcher.find()) {				
					   composite.add(ex.getLocal());
					   temp[i]= "=";
					   
					}
				}
			}
			String local ="";
			for(int i=0; i<temp.length;i++){
				local+= temp[i]+" ";
			}
			temp = local.split("=");
			for(int j=1;j<temp.length;j++){
				//Place(int year, String location, String nameFilter, String county,Geo geo, String repository)
				String placeDiscovered = composite.get(j-1)+" "+temp[j];
				Place discovered = new Place(temp1.getYear(),placeDiscovered,placeDiscovered,temp1.getCounty(),temp1.getGeometry(), temp1.getRepository());
				discovered.setFather(temp1);
				discovered.setPartOf(true);
				places.add(discovered);
			}
			ambiguoPlace.remove(0);
		}
		return places;
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
	
	private void findComposite(ArrayList<Place> places, ArrayList<Expression> exp) throws CloneNotSupportedException{
		
		for(Expression e:exp){
			Pattern pattern = Pattern.compile(e.getExpression());
				//Split names according with regular expression	
				for(int i=0;i<places.size();i++){				
					Matcher matcher = pattern.matcher(places.get(i).getNameFilter());	
					while (matcher.find()) {
						 if(!uniqueExp(places.get(i),exp,e) && !places.get(i).isAmbiguo()){ // look if the name is used or ambiguous
							 places.get(i).setAmbiguo(true);// set this place as a ambiguous					
						 }
					 }
				}		
		}
		for(int j=0;j<places.size();j++){
			if(places.get(j).isAmbiguo()){
				ambiguoPlace.add(places.get(j));
			}
		}
		places.addAll(resolveCompositePlaces(exp));
	}
}
