/**
 * 
 */
package uk.ac.ncl.ziyu.query.queryImpl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import uk.ac.ncl.ziyu.query.query.Neo4jQuery;
import uk.ac.ncl.ziyu.variable.Variable;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class Neo4jQueryImpl extends Variable implements Neo4jQuery{
	
	public void deleteRelationshipOrNode(String type, String nodeOrRelationshipNumber){
		final String cypherUri = SERVER_ROOT_URI + "" + type + "/" + nodeOrRelationshipNumber + "";
		Neo4jRest.getGeneralDeleteResponse(cypherUri);
	}
	
	
	public List<String> queryArticleNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {

		String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") RETURN a\", \"params\":{}}";
		List<String> articleInfoArray = getArticleInfo(cypherPayload).get(0);
		return articleInfoArray;

	}
	
	public String queryUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {
		
			String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") MATCH a-[:`wasGeneratedBy`]->b-[:`wasAssociatedWith`]->c RETURN c\", \"params\":{}}";
			String user_name = getUserName(cypherPayload);
			return user_name;
	}
	
	public List<ArrayList> getArticlesInfoByUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {
		
		String cypherPayload = "{\"query\": \"START c=node(" + nodeNumber + ") MATCH a-[:`wasGeneratedBy`]->b-[:`wasAssociatedWith`]->c RETURN a\", \"params\":{}}";
		List<ArrayList> ArticlesInfo = getArticleInfo(cypherPayload);
		return ArticlesInfo;
	}
	
	public ClientResponse getAllArticlesInfoByUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException {		
		String cypherPayload = "{\"query\": \"START c=node(" + nodeNumber + ") MATCH a-[:`wasGeneratedBy`]->b-[:`wasAssociatedWith`]->c RETURN a\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public ClientResponse getAllArticlesInfo(){
		//START n=node:favorites('revid : *') RETURN n
		//START n=node:articleNodeIndex('revid : *') RETURN n
		String cypherPayload = "{\"query\": \"START n=node:articleNodeIndex('revid : *') RETURN n\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		
		return response;
	}
	
	public ClientResponse getAllActivityInfo(){
		String cypherPayload = "{\"query\": \"START n=node:activityNodeIndex('revid : *') RETURN n\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public ClientResponse getAllUserInfo(){
		String cypherPayload = "{\"query\": \"START n=node:userNodeIndex('username : *') RETURN n\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public ClientResponse getWasGeneratedBy(){
		String cypherPayload = "{\"query\": \"START n=relationship:wasGeneratedBy('relationshipName : *') RETURN n\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public ClientResponse getWasAssociatedWith(){
		String cypherPayload = "{\"query\": \"START n=relationship:wasAssociatedWith('relationshipName : *') RETURN n\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public ClientResponse getWasRevisionOf(){
		String cypherPayload = "{\"query\": \"START n=relationship:wasRevisionOf('relationshipName : *') RETURN n\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public ClientResponse getUsed(){
		String cypherPayload = "{\"query\": \"START n=relationship:used('relationshipName : *') RETURN n\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	public String findParentNodeNumber(String nodeNumber){
		String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") MATCH a-[r:`wasRevisionOf`*]->b RETURN b\", \"params\":{}}";
		String parentNodeUri = (String) getArticleInfo(cypherPayload).get(0).get(4);	
		String[] uriArray=parentNodeUri.split("/");
		String parentNodeNumber=uriArray[6];
		return parentNodeNumber;
	}
	
	public String findChildNodeNumber(String nodeNumber){
		String childNodeNumber = null;
		String cypherPayload = "{\"query\": \"START b=node(" + nodeNumber + ") MATCH a-[r:`wasRevisionOf`*]->b RETURN a\", \"params\":{}}";
		if(!getArticleInfo(cypherPayload).isEmpty()){
			String childNodeUri = (String) getArticleInfo(cypherPayload).get(0).get(4);	
			String[] uriArray=childNodeUri.split("/");
			childNodeNumber=uriArray[6];
		}
		return childNodeNumber;
	}
	
	
	public String findParent(String nodeNumber){
		//POST http://localhost:7474/db/data/cypher {"query": "START a=node(474) MATCH a-[r:`wasRevisionOf`]->b RETURN b", "params":{}}
		String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") MATCH a-[r:`wasRevisionOf`*]->b RETURN b\", \"params\":{}}";
		String getNodesize = getSpecialNodeSize(cypherPayload);
		return getNodesize;
	}
	
	public String findChild(String nodeNumber){
		//POST http://localhost:7474/db/data/cypher {"query": "START a=node(474) MATCH b-[r:`wasRevisionOf`]->a RETURN b", "params":{}}
		String cypherPayload = "{\"query\": \"START a=node(" + nodeNumber + ") MATCH b-[r:`wasRevisionOf`*]->a RETURN b\", \"params\":{}}";
		String getNodesize = getSpecialNodeSize(cypherPayload);
		return getNodesize;
	}
	
	public ClientResponse countRevisionNumber(){
		String cypherPayload = "{\"query\": \"START n=node:articleNodeIndex('revid : *') RETURN count(n)\", \"params\":{}}";
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		return response;
	}
	
	private String getSpecialNodeSize(String cypherPayload){
		String getNodesize = null;
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);

		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
			JSONArray test= new JSONArray();
			if(!getData.isNull(0)){
			test = getData.getJSONArray(0);
			JSONObject  getDataObject = test.getJSONObject(0);
			JSONObject getNodeData = getDataObject.getJSONObject("data");
			if(getNodeData.has("size"))
			getNodesize = getNodeData.getString("size");
			}	
		} catch (Exception e) {
				System.err.println(e.getMessage());
		}
		response.close();		
		return getNodesize;
	}


	private String getUserName(String cypherPayload) throws ClientHandlerException, UniformInterfaceException, JSONException {
		String user_name = null;
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			if(json.has("data")){
			JSONArray getData = json.getJSONArray("data");
			if(!getData.isNull(0)){
			JSONArray test = getData.getJSONArray(0);
			JSONObject getDataObject = test.getJSONObject(0);
			JSONObject getNodeData = getDataObject.getJSONObject("data");
			user_name = getNodeData.getString("user_name");
			}
			}
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		response.close();
		return user_name;
	}
	
	
	private List<ArrayList> getArticleInfo(String cypherPayload){
		ClientResponse response = Neo4jRest.getGeneralPostResponse(cypherPayload);
		
		List<ArrayList> articleArray = new ArrayList<ArrayList>();

		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			if(json.has("data")){
			JSONArray getData = json.getJSONArray("data");
			
			for (int i = 0; i < getData.length(); i++){
				
			ArrayList<String> articleInfoArray = new ArrayList<String>();
				
			JSONArray test= new JSONArray();
			if(!getData.isNull(0)){
			test = getData.getJSONArray(i);
			JSONObject  getDataObject = test.getJSONObject(0);
			JSONObject getNodeData = getDataObject.getJSONObject("data");
			String getNodeUri = getDataObject.getString("self");
			
			String getNodeTitle = getNodeData.getString("title");
			String getNodeRevid = getNodeData.getString("revid");
			String getNodeTime = null;
			if(getNodeData.has("time")){
			getNodeTime = getNodeData.getString("time");
			}
			String getNodeComment = null;
			if(getNodeData.has("comment")){
			getNodeComment = getNodeData.getString("comment");
			}
			String getNodeSize = null;
			if(getNodeData.has("size")){
				getNodeSize = getNodeData.getString("size");   //add get size
			}
			
			articleInfoArray.add(getNodeTitle);
			articleInfoArray.add(getNodeRevid);
			articleInfoArray.add(getNodeTime);
			articleInfoArray.add(getNodeComment);
			articleInfoArray.add(getNodeUri);
			articleInfoArray.add(getNodeSize);
			articleArray.add(articleInfoArray);
			}
			}	
			}
		} catch (Exception e) {
				System.err.println(e.getMessage());
		}
		response.close();
		
		
		return articleArray;
	}

}
