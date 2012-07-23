/**
 * 
 */
package uk.ac.ncl.ziyu.analysisImpl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import uk.ac.ncl.ziyu.analysis.Statics;
import uk.ac.ncl.ziyu.analysis.Vandalism;
import uk.ac.ncl.ziyu.query.query.Neo4jIndex;
import uk.ac.ncl.ziyu.query.query.Neo4jQuery;


import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;



/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class VandalismImpl implements Vandalism {
	

	private Neo4jIndex neo4jIndex;
	private Neo4jQuery neo4jQuery;
	private Statics statics;
	
	public VandalismImpl(){
	}
	
	public VandalismImpl(Neo4jIndex neo4jIndex, Neo4jQuery neo4jQuery, Statics statics){
		this.neo4jIndex = neo4jIndex;
		this.neo4jQuery = neo4jQuery;
		this.statics = statics;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Vandalism#getVandalismArticleNumberByUser(java.lang.String)
	 */
	@Override
	public Set<String> getVandalismArticleNumberByUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String flagKind = "user";
		Set<String> nodeNumberArray = new HashSet<String>();
		if (specialUserNodeUri != null){
		String[] uriArray=specialUserNodeUri.split("/");
		String userNodeNumber=uriArray[6];	
		ClientResponse response = this.neo4jQuery.getAllArticlesInfoByUserNode(userNodeNumber);
		//System.out.println(response.getEntity(String.class));
		nodeNumberArray = runVandalismArticleNumber(response, flagKind);	
		//System.out.println(nodeNumberArray.toString());
		}
		return nodeNumberArray;		
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Vandalism#getVandalismArticleNumber()
	 */
	@Override
	public Set<String> getVandalismArticleNumber(){	
		String flagKind = "all";
		ClientResponse response = this.neo4jQuery.getAllArticlesInfo();
		Set<String> nodeNumberArray = runVandalismArticleNumber(response,flagKind);
		return nodeNumberArray;	
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Vandalism#getVandalismArticleNumberByTitle(java.lang.String)
	 */
	@Override
	public Set<String> getVandalismArticleNumberByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{	
		List<List> articlesInfo= new ArrayList<List>();
		Set<String> revisionArray = this.statics.getTitles(title);
		Iterator<String> it = revisionArray.iterator();
		while (it.hasNext()) {
			  String articleNodeNumber = it.next();
			  //System.out.println(articleNodeNumber);
			  List<String> revisionsInfo = this.neo4jQuery.queryArticleNode(articleNodeNumber);
			  articlesInfo.add(revisionsInfo);
			}
		Set<String> nodeNumberArray = runVandalismByTitle(articlesInfo);
		return nodeNumberArray;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Vandalism#runVandalismByTitle(java.util.List)
	 */
	@Override
	public Set<String> runVandalismByTitle(List<List> articlesInfo) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> nodeNumberArray = new HashSet<String>();
		
		for(int i = 0 ; i < articlesInfo.size(); i++){
			String size = null;
			String articleNumberUri = (String) articlesInfo.get(i).get(4);
			String[] uriArray=articleNumberUri.split("/");
			String nodeNumber=uriArray[6];
			size = (String) articlesInfo.get(i).get(5);
					
			if(size != null) {
				boolean vandalism = compareSize(nodeNumber, size);
				if (vandalism){
					nodeNumberArray.add(nodeNumber);
				}else{
					String parentNodeNumber = null;
					String childNode = this.neo4jQuery.findChildNodeNumber(nodeNumber);
					if(childNode != null){
					
						parentNodeNumber = checkSpecialString(childNode);
						if(parentNodeNumber != null) nodeNumberArray.add(parentNodeNumber);		
					}		
				}					
			}
		}
		return nodeNumberArray;	
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Vandalism#runVandalismArticleNumber(com.sun.jersey.api.client.ClientResponse, java.lang.String)
	 */
	@Override
	public Set<String> runVandalismArticleNumber(ClientResponse response, String flagKind){		
		Set<String> nodeNumberArray = new HashSet<String>();
		//List<String> nodeNumberArray = new ArrayList<String>();
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
			//System.err.println(getData.length());
					
			for(int count = 0; count < getData.length(); count++){
				String getNodesize = null;
				JSONArray getNode = getData.getJSONArray(count);
				JSONObject getNodeInfo = getNode.getJSONObject(0);
				JSONObject getNodeData = getNodeInfo.getJSONObject("data");
				if(getNodeData.has("size"))
					getNodesize = getNodeData.getString("size");			
				String getNodeUri = getNodeInfo.getString("self");
				
				String[] uriArray=getNodeUri.split("/");
				String nodeNumber=uriArray[6];
				//System.out.println("===================================");
				//System.out.println("    size:" + getNodesize+ "    nodeNumber:" + nodeNumber);
				if(getNodesize != null) {
					boolean vandalism = compareSize(nodeNumber, getNodesize);
					if (vandalism){
					//	System.out.println(nodeNumber);
						nodeNumberArray.add(nodeNumber);
					}else{
						String parentNodeNumber = null;
						//if (flagKind == "user"){
						String childNodeNumber = this.neo4jQuery.findChildNodeNumber(nodeNumber);
						//}
						if(childNodeNumber != null) parentNodeNumber = checkSpecialString(childNodeNumber);
						if(parentNodeNumber != null) nodeNumberArray.add(nodeNumber);
					}					
				}
			}
			
		} catch (Exception e) {
				System.err.println(e.getMessage());			// these two lines should be commented , otherwise " Index: 0, Size: 0 "
				System.err.println(e.getStackTrace());		
				e.printStackTrace();
		}
		response.close();
		return nodeNumberArray;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Vandalism#compareSize(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean compareSize(String nodeNumber, String size){
		int countSize = 0;
		boolean vandalism = false;
		String parentSize = this.neo4jQuery.findParent(nodeNumber);
		if (parentSize != null){
			countSize = Integer.parseInt(parentSize) - Integer.parseInt(size);
			if(countSize > 2000){
				String childSize = this.neo4jQuery.findChild(nodeNumber);
				if (childSize != null){
					countSize = Integer.parseInt(childSize) - Integer.parseInt(size);
					if (countSize>2000) vandalism = true;
				}
			}
		}
		return vandalism;		
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Vandalism#checkSpecialString(java.lang.String)
	 */
	@Override
	public String checkSpecialString(String nodeNumber) throws ClientHandlerException, UniformInterfaceException, JSONException{
		boolean vandalism = false;
		boolean rvv = false;
		String parentNodeNumber = null;
		String articleComment = new String();
		List<String> articleInfoArray = this.neo4jQuery.queryArticleNode(nodeNumber);
		String articleTitle = articleInfoArray.get(0);
		try{
		articleComment = articleInfoArray.get(3);
		}catch (Exception e) {
			articleComment = "null";
		}
		if(articleTitle != "Vandalism"){
			try{
			vandalism = articleComment.contains("vandalism");
			rvv = articleComment.contains("rvv");
			}catch (Exception e) {
				//e.printStackTrace();
			}
			if(vandalism||rvv){
				parentNodeNumber = this.neo4jQuery.findParentNodeNumber(nodeNumber);
			}
		}		
		return parentNodeNumber;
	}

}
