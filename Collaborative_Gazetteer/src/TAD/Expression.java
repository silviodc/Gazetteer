package TAD;

public class Expression {
	    private String name;
	    private String expression;
	    private String feature;
	    private String ontology;
	      
	    public Expression(String name, String expression, String feature,String ontology) {
			this.name = name;
			this.expression = expression;
			this.feature = feature;
			this.ontology = ontology;
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
