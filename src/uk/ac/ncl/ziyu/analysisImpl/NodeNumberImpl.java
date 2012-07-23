/**
 * 
 */
package uk.ac.ncl.ziyu.analysisImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import uk.ac.ncl.ziyu.analysis.NodeNumber;
import uk.ac.ncl.ziyu.analysis.Statics;
import uk.ac.ncl.ziyu.query.query.Neo4jIndex;
import uk.ac.ncl.ziyu.query.query.Neo4jQuery;


import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;


/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class NodeNumberImpl implements NodeNumber {
	
	private Neo4jIndex neo4jIndex;
	private Neo4jQuery neo4jQuery;
	private Statics statics;
	
	public NodeNumberImpl(){
	}
	
	public NodeNumberImpl(Neo4jIndex neo4jIndex, Neo4jQuery neo4jQuery, Statics statics){
		this.neo4jIndex = neo4jIndex;
		this.neo4jQuery = neo4jQuery;
		this.statics = statics;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.NodeNumber#findNodeNumberByUser(java.lang.String)
	 */
	@Override
	public String findNodeNumberByUser(String user){
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		return splitNodeUri(specialUserNodeUri);		
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.NodeNumber#findUserInfoByRevid(java.lang.String)
	 */
	@Override
	public ArrayList<String> findUserInfoByRevid(String revid) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialArticleNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", revid);
		String revidNodeNumber=splitNodeUri(specialArticleNodeUri);
		String user = this.neo4jQuery.queryUserNode(revidNodeNumber);
		ArrayList<String> list = new ArrayList<String>();
		if(user != null){
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String userNodeNumber = splitNodeUri(specialUserNodeUri);		
		list.add(userNodeNumber);
		list.add(user);
		}
		return list;
	
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.NodeNumber#splitNodeUri(java.lang.String)
	 */
	@Override
	public String splitNodeUri(String specialNodeUri){
		String nodeNumber = new String();
		if(specialNodeUri != null){
			String[] uriArray=specialNodeUri.split("/");
			nodeNumber=uriArray[6];
			}
		return nodeNumber;				
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.NodeNumber#getUserLatestEdit(java.lang.String)
	 */
	@Override
	public String getUserLatestEdit(String user) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException{
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String userNodeNumber=splitNodeUri(specialUserNodeUri);
		String userLatesetEditInfo = new String();
		int revid = 0;
		int count = 0;
		List<ArrayList> ArticlesInfo = this.neo4jQuery.getArticlesInfoByUserNode(userNodeNumber);
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
			String tmp = (String) ArticlesInfo.get(i).get(1);
			int tmpRevid = Integer.parseInt(tmp);
			if(tmpRevid > revid){
				revid = tmpRevid;
				count = i;
			}
		}
		if(revid != 0){
			userLatesetEditInfo =  "title:" + ArticlesInfo.get(count).get(0) + " " +
								   "revid:" + ArticlesInfo.get(count).get(1) + " " +
			                       "time:" + ArticlesInfo.get(count).get(2) + " " +
			                       "comment:" + ArticlesInfo.get(count).get(3) + " " ;
			String articleNumberUri = (String) ArticlesInfo.get(count).get(4);
			String articleNodeNumber=splitNodeUri(articleNumberUri);
			userLatesetEditInfo = userLatesetEditInfo + "articleNodeNumber:"+ articleNodeNumber;
			}
			//userLatesetEditInfo = title + articleRevid + time + comment + nodeNumber;
		return userLatesetEditInfo;
		}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.NodeNumber#getArticleLatestEdit(java.lang.String)
	 */
	@Override
	public String getArticleLatestEdit(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> revisionArray = this.statics.getTitles(title);
		Iterator<String> it = revisionArray.iterator();
		String revisonLatesetEditInfo = new String();
		int revid = 0;
		String nodeNumber = new String();
		while (it.hasNext()) {
			  String articleNodeNumber = it.next();
			  List<String> revisionsInfo = this.neo4jQuery.queryArticleNode(articleNodeNumber);
			  int tmpRevid =  Integer.parseInt(revisionsInfo.get(1));
			  if(tmpRevid > revid){
					revid = tmpRevid;
					nodeNumber = articleNodeNumber;
				}
			}
		if(!nodeNumber.equals("")){
			List<String> latestEditRevisionInfo = this.neo4jQuery.queryArticleNode(nodeNumber);			
			revisonLatesetEditInfo =  "title:" + latestEditRevisionInfo.get(0) + " " +
					   				  "revid:" + latestEditRevisionInfo.get(1) + " " +
					   				  "time:" + latestEditRevisionInfo.get(2) + " " +
					   				  "comment:" + latestEditRevisionInfo.get(3) + " " + 
					   				  "articleNodeNumber:"+ nodeNumber;	
		}
		return revisonLatesetEditInfo;		
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.NodeNumber#getRevisonInfoByRevid(java.lang.String)
	 */
	@Override
	public String getRevisonInfoByRevid(String revid) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialArticleNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "articleNodeIndex", "revid", revid);
		String revidNodeNumber=splitNodeUri(specialArticleNodeUri);
		String revisonInfo = new String();
		if(!revidNodeNumber.equals("")){
		List<String> revisionArticleInfo = this.neo4jQuery.queryArticleNode(revidNodeNumber);			
		revisonInfo =  "title:" + revisionArticleInfo.get(0) + " " +
				   				  "revid:" + revisionArticleInfo.get(1) + " " +
				   				  "time:" + revisionArticleInfo.get(2) + " " +
				   				  "comment:" + revisionArticleInfo.get(3) + " " + 
				   				  "articleNodeNumber:"+ revidNodeNumber;	
		}
		return revisonInfo;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.NodeNumber#getRevisonByTitleAndUser(java.lang.String, java.lang.String)
	 */
	@Override
	public String getRevisonByTitleAndUser(String title, String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String userNodeNumber=splitNodeUri(specialUserNodeUri);
		List<ArrayList> ArticlesInfo = this.neo4jQuery.getArticlesInfoByUserNode(userNodeNumber);
		String revisonByTitleAndUserInfo = new String();
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
			String tmpTitle = (String) ArticlesInfo.get(i).get(0);
			if(tmpTitle.equals(title)){
				revisonByTitleAndUserInfo = "title:" + ArticlesInfo.get(i).get(0) + " " +
						   					"revid:" + ArticlesInfo.get(i).get(1) + " " +
						   					"time:" + ArticlesInfo.get(i).get(2) + " " +
						   					"comment:" + ArticlesInfo.get(i).get(3) + " " ;
				String articleNumberUri = (String) ArticlesInfo.get(i).get(4);
				String articleNodeNumber=splitNodeUri(articleNumberUri);
				revisonByTitleAndUserInfo = revisonByTitleAndUserInfo + "articleNodeNumber:"+ articleNodeNumber + "\n";
			}
		}
		return revisonByTitleAndUserInfo;
	}

}
