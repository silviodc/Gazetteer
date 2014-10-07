package cluster;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TAD.Expression;
import TAD.Group;
import TAD.Place;
import TAD.Repository;


public class Star_algorithm {

	private final double similarity = 0.4;
	
	
	public ArrayList<Group> start_clustering(Repository rep, ArrayList<Expression> exp) throws InstantiationException, IllegalAccessException, CloneNotSupportedException{
		ArrayList<Place> candidate_place = new ArrayList<Place>();		
		ArrayList<Group> group = new ArrayList<Group>();
		for(Expression e:exp){
			Pattern pattern = Pattern.compile(e.getExpression());
				//Split names according with regular expression	
				for(Place pl: rep.getPlaces() ){				
					Matcher matcher = pattern.matcher(pl.getNameFilter());	
					while (matcher.find()) {
						 if(!pl.isUsed() && !pl.isAmbiguo() && uniqueExp(pl,exp,e)){ // look if the name is used or ambiguous
							 candidate_place.add(pl.clone());
							 System.out.println(pl.getLocation());
							 pl.setUsed(true);
						 }else{					
								pl.setUsed(false);
								pl.setAmbiguo(true);// set this place as a ambiguous
							}
					 }
				}
					System.out.println("=========================");
					while(candidate_place.size()>0){		
						Random rand = new Random();
						Place centroid = candidate_place.get(rand.nextInt(candidate_place.size())); // get some centroid to start matching
						candidate_place.remove(centroid);//remove centroid from candidate places
						Group group_created = clustering_using_start(candidate_place,centroid);
						group_created.setExp(e);
						group_created.setRepository(rep.getName());
						group.add(group_created);
					}
				
				candidate_place.clear();
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
				return false;
			}
		}
		return true;
	}

	public Group clustering_using_start(ArrayList<Place> candidate_place,Place centroid){
		
		// CLUSTERING!!! using star
		
		Group local_group = new Group();
		local_group.getPlaces().add(centroid);		
		
		Jaccard_Similarity jaccard = new Jaccard_Similarity(); // try resolve matching using jaccard similarity metric
		System.out.println(candidate_place.size());
		for(int i =0; i<candidate_place.size();i++){
			double value = jaccard.jaccardSimilarity(centroid.getNameFilter(),candidate_place.get(i).getNameFilter());
			System.out.println(value);
			if(value >= similarity && verific_county(candidate_place.get(i).getCounty(),centroid.getCounty())){
				System.out.println("É similar");
					local_group.getPlaces().add(candidate_place.get(i));
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
	
	private boolean desambiguation(Place pl, ArrayList<Expression> exp) {
	
		return true;
	}	
}
