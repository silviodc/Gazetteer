package TAD;

public class Expression {
	    private String name;
	    private String expression;
	    private String feature;
	    private String ontology;
	    private int id;
	    
	    
	    public Expression(String name, String expression, String feature,String ontology,int id) {
			this.name = name;
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
	    
	    
}
