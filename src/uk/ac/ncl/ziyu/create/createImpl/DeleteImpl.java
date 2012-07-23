/**
 * 
 */
package uk.ac.ncl.ziyu.create.createImpl;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import uk.ac.ncl.ziyu.create.create.Delete;
import uk.ac.ncl.ziyu.query.query.Neo4jIndex;
import uk.ac.ncl.ziyu.query.query.Neo4jQuery;

import com.sun.jersey.api.client.ClientResponse;


/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class DeleteImpl implements Delete {
	
	private Neo4jIndex neo4jIndex;
	private Neo4jQuery neo4jQuery;
	
	public DeleteImpl(){
	}
	
	public DeleteImpl(Neo4jIndex neo4jIndex, Neo4jQuery neo4jQuery){
		this.neo4jIndex = neo4jIndex;
		this.neo4jQuery = neo4jQuery;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.create.createImpl.Delete#deleteAllIndex()
	 */
	@Override
	public void deleteAllIndex(){
		String deleteArticleNodeIndexUri = "index/node/articleNodeIndex";
		this.neo4jIndex.delete(deleteArticleNodeIndexUri);		
		String deleteActivityNodeIndexUri = "index/node/activityNodeIndex";
		this.neo4jIndex.delete(deleteActivityNodeIndexUri);		
		String deleteUserNodeIndexUri = "index/node/userNodeIndex";
		this.neo4jIndex.delete(deleteUserNodeIndexUri);
		
		String deleteWasRevisionOfIndexUri = "index/relationship/wasRevisionOf";
		this.neo4jIndex.delete(deleteWasRevisionOfIndexUri);
		String deleteWasGeneratedByIndexUri = "index/relationship/wasGeneratedBy";
		this.neo4jIndex.delete(deleteWasGeneratedByIndexUri);
		String deleteWasAssociatedWithIndexUri = "index/relationship/wasAssociatedWith";
		this.neo4jIndex.delete(deleteWasAssociatedWithIndexUri);
		String deleteUsedIndexUri = "index/relationship/used";
		this.neo4jIndex.delete(deleteUsedIndexUri);
	}
	
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.create.createImpl.Delete#deleteRelationship()
	 */
	@Override
	public void deleteRelationship(){
		String type = "relationship";
		ClientResponse responseWasAssociatedWith = this.neo4jQuery.getWasAssociatedWith();	
		deleteAllNodeOrRelationship(responseWasAssociatedWith, type);
		System.out.println("delete relationship wasAssociatedWith successfully");
		ClientResponse responseWasGeneratedBy = this.neo4jQuery.getWasGeneratedBy();		
		deleteAllNodeOrRelationship(responseWasGeneratedBy, type);
		System.out.println("delete relationship wasGeneratedBy successfully");
		ClientResponse responseWasRevisionOf = this.neo4jQuery.getWasRevisionOf();
		deleteAllNodeOrRelationship(responseWasRevisionOf, type);
		System.out.println("delete relationship wasRevisionOf successfully");
		ClientResponse responseUsed = this.neo4jQuery.getUsed();
		deleteAllNodeOrRelationship(responseUsed, type);
		System.out.println("delete relationship used successfully");
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.create.createImpl.Delete#deleteNode()
	 */
	@Override
	public void deleteNode(){
		String type = "node";
		ClientResponse responseArticlesresponse = this.neo4jQuery.getAllArticlesInfo();
		deleteAllNodeOrRelationship(responseArticlesresponse, type);
		System.out.println("delete node article successfully");
		ClientResponse responseActivity = this.neo4jQuery.getAllActivityInfo();
		deleteAllNodeOrRelationship(responseActivity, type);
		System.out.println("delete node activity successfully");
		ClientResponse responseUser = this.neo4jQuery.getAllUserInfo();
		deleteAllNodeOrRelationship(responseUser, type);
		System.out.println("delete node user successfully");
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.create.createImpl.Delete#deleteAllNodeOrRelationship(com.sun.jersey.api.client.ClientResponse, java.lang.String)
	 */
	@Override
	public void deleteAllNodeOrRelationship(ClientResponse response, String type){
		
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
					
			for(int count = 0; count < getData.length(); count++){
				JSONArray getNode = getData.getJSONArray(count);
				JSONObject getNodeInfo = getNode.getJSONObject(0);	
				String getNodeUri = getNodeInfo.getString("self");
				
				String[] uriArray=getNodeUri.split("/");
				String nodeNumber=uriArray[6];
				this.neo4jQuery.deleteRelationshipOrNode(type, nodeNumber);
			}
		} catch (Exception e) {
//			System.err.println(e.getMessage());			// these two lines should be commented , otherwise " Index: 0, Size: 0 "
//			System.err.println(e.getStackTrace());		
	}			
				
	}

}
