package uk.ac.ncl.ziyu.query.query;

public interface Neo4jIndex {
	public void createIndex();
	
	public void createNodeOrRelationshipIndex(String type, String indexName);
	
	public void addNodeOrRelationshipToIndex(String type, String indexName, String key, String value, String nodeUri);
	
	public String queryNodeOrRelationship(String type, String indexName, String key, String value);
	
	public void delete(String deleteIndexUri);

}
