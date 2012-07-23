/**
 * 
 */
package uk.ac.ncl.ziyu.query.query;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface Neo4jQuery {
	public void deleteRelationshipOrNode(String type, String nodeOrRelationshipNumber);
	
	public List<String> queryArticleNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException;
	
	public String queryUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException;
	
	public List<ArrayList> getArticlesInfoByUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException;
	
	public ClientResponse getAllArticlesInfoByUserNode(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException;
	
	public ClientResponse getAllArticlesInfo();
	
	public ClientResponse getAllActivityInfo();
	
	public ClientResponse getAllUserInfo();
	
	public ClientResponse getWasGeneratedBy();
	
	public ClientResponse getWasAssociatedWith();
	
	public ClientResponse getWasRevisionOf();
	
	public ClientResponse getUsed();
	
	public String findParentNodeNumber(String nodeNumber);
	
	public String findChildNodeNumber(String nodeNumber);
	
	public String findParent(String nodeNumber);
	
	public String findChild(String nodeNumber);
	
	public ClientResponse countRevisionNumber();
	
//	private String getSpecialNodeSize(String cypherPayload);
//
//	private String getUserName(String cypherPayload) throws ClientHandlerException, UniformInterfaceException, JSONException;
//	
//	public List<ArrayList> getArticleInfo(String cypherPayload);


}
