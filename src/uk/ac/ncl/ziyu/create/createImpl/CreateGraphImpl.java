/**
 * 
 */
package uk.ac.ncl.ziyu.create.createImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


import uk.ac.ncl.ziyu.create.create.CreateGraph;
import uk.ac.ncl.ziyu.query.query.Neo4jIndex;
import uk.ac.ncl.ziyu.query.queryImpl.Neo4jIndexImpl;
import uk.ac.ncl.ziyu.variable.Variable;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class CreateGraphImpl extends Variable implements CreateGraph{

	private Neo4jIndex neo4jIndex;
	
	public CreateGraphImpl(){
	}
	
	public CreateGraphImpl(Neo4jIndex neo4jIndex){
		this.neo4jIndex=neo4jIndex;
	}
	
/* (non-Javadoc)
 * @see uk.ac.ncl.ziyu.create.createImpl.CreateGraph#getData(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
 */
@Override
public void getData(String title, String revid, String parentid, String user, String time, String comment, String size, String pageid) throws URISyntaxException, ClientHandlerException, UniformInterfaceException, JSONException{
		
		checkDatabaseIsRunning();
		
		String specialArticleNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", revid);
		URI articleNode = new URI("");
		if(specialArticleNodeUri == null){
			articleNode = createNode();
			this.neo4jIndex.addNodeOrRelationshipToIndex("node", "articleNodeIndex", "revid", revid, articleNode.toString());
		}else{
			articleNode = new URI(specialArticleNodeUri);
		}	
		addProperty(articleNode, "id", revid);
		addProperty(articleNode, "revid", revid);
		addProperty(articleNode, "title", title);
		addProperty(articleNode, "prov:type", "article");
		addProperty(articleNode, "type", "entity");
		addProperty(articleNode, "pageid", pageid);
		addProperty(articleNode, "comment", comment);
		addProperty(articleNode, "time", time);
		addProperty(articleNode, "size", size);
		addProperty(articleNode, "parentid", parentid);
	
		
		String specialActivityNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "activityNodeIndex", "revid", revid);
		URI activityNode = new URI("");
		String commentId = "comment" + revid;
		if(specialActivityNodeUri == null){
			activityNode = createNode();
			this.neo4jIndex.addNodeOrRelationshipToIndex("node", "activityNodeIndex", "revid", revid, activityNode.toString());
			
			
			addProperty(activityNode, "prov:type", "edit");
			addProperty(activityNode, "type", "activity");
			addProperty(activityNode, "comment", comment);
			addProperty(activityNode, "starttime", "null");
			addProperty(activityNode, "endtime", time);
			addProperty(activityNode, "id", commentId);
			addProperty(activityNode, "revid", revid);
		}else{
			activityNode = new URI(specialActivityNodeUri);
		}
		

		
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		URI userNode = new URI("");
		if (specialUserNodeUri == null){
			userNode = createNode();
			this.neo4jIndex.addNodeOrRelationshipToIndex("node", "userNodeIndex", "username", user, userNode.toString());
			
			addProperty(userNode, "prov:type", "editor");
			addProperty(userNode, "type", "agent");
			addProperty(userNode, "user_name", user);
			addProperty(userNode, "id", user);
		}else{
			userNode = new URI(specialUserNodeUri);
		}


		Map<String,String> property=new HashMap<String,String>();
		
		String relationshipArticleActivityName = revid + "comment";
		String specialRelationshipArticleActivityUri = this.neo4jIndex.queryNodeOrRelationship("relationship", "wasGeneratedBy", "relationshipName", relationshipArticleActivityName);
		URI relationshipArticleActivityUri = new URI("");
		if (specialRelationshipArticleActivityUri == null){
			relationshipArticleActivityUri = addRelationship(articleNode, activityNode, "wasGeneratedBy", "{}");
			this.neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasGeneratedBy", "relationshipName", relationshipArticleActivityName, relationshipArticleActivityUri.toString());
			property.clear();
			property.put("time", time);
			property.put("relationshipName", relationshipArticleActivityName);
			property.put("id", relationshipArticleActivityName);
			property.put("entity", revid);
			property.put("activity", commentId);
			addMetadataToProperty(relationshipArticleActivityUri, property);
		}//else{
//			relationshipArticleActivityUri = new URI(specialRelationshipArticleActivityUri);
//		}
		

		
		String relationshipActivityUserName = "comment" + revid + user;
		String specialRelationshipActivityUserUri = this.neo4jIndex.queryNodeOrRelationship("relationship", "wasAssociatedWith", "relationshipName", relationshipActivityUserName);
		URI relationshipActivityUserUri = new URI("");
		if (specialRelationshipActivityUserUri == null){
		    relationshipActivityUserUri = addRelationship(activityNode, userNode, "wasAssociatedWith", "{}");
		    this.neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasAssociatedWith", "relationshipName", relationshipActivityUserName, relationshipActivityUserUri.toString());
		    property.clear();
			property.put("user_name", user);
			property.put("activity", commentId);
			property.put("agent", user);
			property.put("publicationpolicy", "null");
			property.put("relationshipName", relationshipActivityUserName);
			property.put("id", relationshipActivityUserName);
			addMetadataToProperty(relationshipActivityUserUri, property);
		}//else{
//			relationshipActivityUserUri = new URI(specialRelationshipActivityUserUri);
//		}
		

		
		if(parentid != "0") 
		{
			specialArticleNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", parentid);
			URI parentNode = new URI("");
			if(specialArticleNodeUri == null){
				parentNode = createNode();
				this.neo4jIndex.addNodeOrRelationshipToIndex("node", "articleNodeIndex", "revid", parentid, parentNode.toString());
				addProperty(parentNode, "revid", parentid);
				addProperty(parentNode, "id", parentid);
				addProperty(parentNode, "title", title);
				addProperty(parentNode, "type", "entity");
				addProperty(parentNode, "prov:type", "article");
				addProperty(parentNode, "pageid", pageid);
			}else{
				parentNode = new URI(specialArticleNodeUri);			
			}
			
	
			
			String relationshipRevisionParentName = revid + parentid;
			String specialRelationshipRevisionParentUri = this.neo4jIndex.queryNodeOrRelationship("relationship", "wasRevisionOf", "relationshipName", relationshipRevisionParentName);
			URI relationshipRevParentUri = new URI("");
			if (specialRelationshipRevisionParentUri == null){
				relationshipRevParentUri = addRelationship(articleNode, parentNode, "wasRevisionOf", "{}");
				this.neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasRevisionOf", "relationshipName", relationshipRevisionParentName, relationshipRevParentUri.toString());
				
				property.clear();
				property.put("parentid", parentid);
				property.put("entity1", parentid);
				property.put("revid", revid);
				property.put("entity2", revid);
				property.put("agent", user);
				property.put("relationshipName", relationshipRevisionParentName);
				property.put("id", relationshipRevisionParentName);
				addMetadataToProperty(relationshipRevParentUri, property);
			}//else{
//				relationshipRevParentUri = new URI(specialRelationshipRevisionParentUri);
//			}
			
			String relationshipUsedName = commentId + parentid;
			String specialRelationshipActivityParentUri = this.neo4jIndex.queryNodeOrRelationship("relationship", "used", "relationshipName", relationshipUsedName);
			URI relationshipActivityParentUri = new URI("");
			if (specialRelationshipActivityParentUri == null){
				relationshipActivityParentUri = addRelationship(activityNode, parentNode, "used", "{}");
				this.neo4jIndex.addNodeOrRelationshipToIndex("relationship", "used", "relationshipName", relationshipUsedName, relationshipActivityParentUri.toString());
				
				property.clear();
				property.put("entity", parentid);
				property.put("agent", user);
				property.put("id", relationshipUsedName);
				property.put("time", time);
				addMetadataToProperty(relationshipActivityParentUri, property);
			}
//			
			
		}
	}
	
/* (non-Javadoc)
 * @see uk.ac.ncl.ziyu.create.createImpl.CreateGraph#getUserData(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
 */
@Override
public void getUserData(String title, String revid, String user, String time, String comment, String size, String pageid) throws URISyntaxException, ClientHandlerException, UniformInterfaceException, JSONException{
		
		checkDatabaseIsRunning();
		
		
		String specialArticleNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", revid);
		URI articleNode = new URI("");
		if(specialArticleNodeUri == null){
			articleNode = createNode();
			this.neo4jIndex.addNodeOrRelationshipToIndex("node", "articleNodeIndex", "revid", revid, articleNode.toString());
		}else{
			articleNode = new URI(specialArticleNodeUri);
		}	
		addProperty(articleNode, "id", revid);
		addProperty(articleNode, "revid", revid);
		addProperty(articleNode, "title", title);
		addProperty(articleNode, "prov:type", "article");
		addProperty(articleNode, "type", "entity");
		addProperty(articleNode, "pageid", pageid);
		addProperty(articleNode, "comment", comment);
		addProperty(articleNode, "time", time);
		addProperty(articleNode, "size", size);
		
		
		String specialActivityNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "activityNodeIndex", "revid", revid);
		URI activityNode = new URI("");
		String commentId = "comment" + revid;
		if(specialActivityNodeUri == null){
			activityNode = createNode();
			this.neo4jIndex.addNodeOrRelationshipToIndex("node", "activityNodeIndex", "revid", revid, activityNode.toString());
			addProperty(activityNode, "prov:type", "edit");
			addProperty(activityNode, "type", "activity");
			addProperty(activityNode, "id", commentId);
			addProperty(activityNode, "starttime", "null");
			addProperty(activityNode, "endtime", time);
			addProperty(activityNode, "comment", comment);
			addProperty(activityNode, "revid", revid);
		}else{
			activityNode = new URI(specialActivityNodeUri);
		}
		
		

		
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		URI userNode = new URI("");
		if (specialUserNodeUri == null){
			userNode = createNode();
			this.neo4jIndex.addNodeOrRelationshipToIndex("node", "userNodeIndex", "username", user, userNode.toString());			
			addProperty(userNode, "prov:type", "editor");	
			addProperty(userNode, "type", "agent");
			addProperty(userNode, "user_name", user);
			addProperty(userNode, "id", user);
		}else{
			userNode = new URI(specialUserNodeUri);
		}
		
		
		Map<String,String> property=new HashMap<String,String>();		
		String relationshipArticleActivityName = revid + "comment";
		String specialRelationshipArticleActivityUri = this.neo4jIndex.queryNodeOrRelationship("relationship", "wasGeneratedBy", "relationshipName", relationshipArticleActivityName);
		URI relationshipArticleActivityUri = new URI("");
		if (specialRelationshipArticleActivityUri == null){
			relationshipArticleActivityUri = addRelationship(articleNode, activityNode, "wasGeneratedBy", "{}");
			this.neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasGeneratedBy", "relationshipName", relationshipArticleActivityName, relationshipArticleActivityUri.toString());
			
			property.clear();
			property.put("time", time);
			property.put("relationshipName", relationshipArticleActivityName);
			property.put("id", relationshipArticleActivityName);
			property.put("entity", revid);
			property.put("activity", commentId);
			addMetadataToProperty(relationshipArticleActivityUri, property);
		}//else{
//			relationshipArticleActivityUri = new URI(specialRelationshipArticleActivityUri);
//		}	
		
		
	
		String relationshipActivityUserName = "comment" + revid + user;
		String specialRelationshipActivityUserUri = this.neo4jIndex.queryNodeOrRelationship("relationship", "wasAssociatedWith", "relationshipName", relationshipActivityUserName);
		URI relationshipActivityUserUri = new URI("");
		if (specialRelationshipActivityUserUri == null){
		    relationshipActivityUserUri = addRelationship(activityNode, userNode, "wasAssociatedWith", "{}");
		    this.neo4jIndex.addNodeOrRelationshipToIndex("relationship", "wasAssociatedWith", "relationshipName", relationshipActivityUserName, relationshipActivityUserUri.toString());
		    
		    property.clear();
			property.put("user_name", user);
			property.put("activity", commentId);
			property.put("agent", user);
			property.put("publicationpolicy", "null");
			property.put("relationshipName", relationshipActivityUserName);
			property.put("id", relationshipActivityUserName);
			addMetadataToProperty(relationshipActivityUserUri, property);
		}
	}
	
	private URI createNode(){
		
		final String nodeEntryPointUri = SERVER_ROOT_URI + "node";		
		WebResource resource = Client.create().resource(nodeEntryPointUri);		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity("{}").post(ClientResponse.class);
		
		final URI location = response.getLocation();
		System.out.println(String.format("POST to [%s], status code [%d], location header [%s]", nodeEntryPointUri, response.getStatus(),location.toString()));		
		response.close();		
		return location;
	}
	
	
	private void addProperty(URI nodeUri, String propertyName, String propertyValue){
		String propertyUri = nodeUri.toString() + "/properties/" + propertyName;		
		WebResource resource = Client.create().resource(propertyUri);		
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity("\"" + propertyValue + "\"").put(ClientResponse.class);		
		response.close();
	}
	
	
	private URI addRelationship(URI startNode, URI endNode, String relationshipType, String jsonAttributes) throws URISyntaxException{
		URI fromUri = new URI(startNode.toString() + "/relationships");
		String relationshipJson = generateJsonRelationship(endNode, relationshipType, jsonAttributes);
		WebResource resource = Client.create().resource(fromUri);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity(relationshipJson).post(ClientResponse.class);		
		final URI location = response.getLocation();
		System.out.println(String.format("POST to [%s], status code [%d], location header [%s]", fromUri, response.getStatus(), location.toString()));
		response.close();
		return location;
	}
	
	
	private String generateJsonRelationship(URI endNode, String relationshipType, String... jsonAttributes ){
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"to\" : \"");
		sb.append(endNode.toString());
		sb.append("\", ");		
		sb.append("\"type\" : \"");
		sb.append(relationshipType);
		if (jsonAttributes == null || jsonAttributes.length<1){
			sb.append("\"");
		}else{
			sb.append("\", \"data\" : ");
			for(int i = 0; i < jsonAttributes.length; i++){
				sb.append(jsonAttributes[i]);
					if(i < jsonAttributes.length - 1){
						sb.append(", ");
					}
				}
			}	
		sb.append(" }");
		return sb.toString();
		}
	

		
	private void addMetadataToProperty( URI relationshipUri,
            Map<String,String> property) throws URISyntaxException
    {
        URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
        String entity = toJsonNameValuePairCollection( property );
        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( entity )
                .put( ClientResponse.class );

        System.out.println( String.format(
                "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
                response.getStatus() ) );
        response.close();
    }
	
	private String toJsonNameValuePairCollection(Map<String,String> property){
		
		Set<String> keys=property.keySet();
		Iterator<String> iter=keys.iterator();
		String outPut="{ ";
		while(iter.hasNext()){
			String key=iter.next();
			String value=property.get(key);
			
			outPut+="\""+key+"\" : \""+value+"\"";
			if(iter.hasNext()){
				outPut+=", ";
			}
		}
		outPut+="}";		
		return outPut;
	}
	
	private void checkDatabaseIsRunning(){
		WebResource resource = Client.create().resource(SERVER_ROOT_URI);
		ClientResponse response = resource.get(ClientResponse.class);		
		response.close();
	}
}
