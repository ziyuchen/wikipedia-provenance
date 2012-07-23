/**
 * 
 */
package uk.ac.ncl.ziyu.analysisImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import uk.ac.ncl.ziyu.analysis.ContributionAndRevision;
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
public class ContributionAndRevisionImpl implements ContributionAndRevision {
	
	private Neo4jIndex neo4jIndex;
	private Neo4jQuery neo4jQuery;
	private Statics statics;
	
	public ContributionAndRevisionImpl(){
	}
	
	public ContributionAndRevisionImpl(Neo4jIndex neo4jIndex, Neo4jQuery neo4jQuery, Statics statics){
		this.neo4jIndex = neo4jIndex;
		this.neo4jQuery = neo4jQuery;
		this.statics = statics;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.ContributionAndRevision#getUserContribsTxtOffline(java.lang.String, java.lang.String)
	 */
	@Override
	public void getUserContribsTxtOffline(String user, String folderPath) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException{
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String[] uriArray=specialUserNodeUri.split("/");
		String userNodeNumber=uriArray[6];
		List<ArrayList> ArticlesInfo = this.neo4jQuery.getArticlesInfoByUserNode(userNodeNumber);
		String src =folderPath +  "/" + user + ".txt ";
		
		generateUserContribsTxtOffline(src, ArticlesInfo);		
	}
	
	private void generateUserContribsTxtOffline(String src, List<ArrayList> ArticlesInfo) throws IOException{
		File f = new File(src);	 
		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter(f));
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
				bw.append("title:" + ArticlesInfo.get(i).get(0) + " ");
				bw.append("revid:" + ArticlesInfo.get(i).get(1) + " ");
				bw.append("time:" + ArticlesInfo.get(i).get(2) + " ");
				String articleNumberUri = (String) ArticlesInfo.get(i).get(4);
				String[] uriArray=articleNumberUri.split("/");
				String NodeNumber=uriArray[6];
				bw.append("nodeNumber:"+ NodeNumber + "\n");
		}
		bw.flush();
		bw.close();
		
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.ContributionAndRevision#showContrisByUser(java.lang.String)
	 */
	@Override
	public List<Object[]> showContrisByUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String[] uriArray=specialUserNodeUri.split("/");
		String userNodeNumber=uriArray[6];
		List<ArrayList> ArticlesInfo = this.neo4jQuery.getArticlesInfoByUserNode(userNodeNumber);
		
		List<Object[]> resultList=new ArrayList<Object[]>();
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
			String articleNumberUri = (String) ArticlesInfo.get(i).get(4);
			String[] articleUriArray=articleNumberUri.split("/");
			String NodeNumber=articleUriArray[6];
			
			String title=(String) ArticlesInfo.get(i).get(0);
			String revid=(String) ArticlesInfo.get(i).get(1);
			String time=(String) ArticlesInfo.get(i).get(2);
			Object[] tmpArray=new Object[]{title,revid,time,NodeNumber};
			resultList.add(tmpArray);
		}
	
		return resultList;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.ContributionAndRevision#getArticleRevisionTxtOffline(java.lang.String, java.lang.String)
	 */
	@Override
	public void getArticleRevisionTxtOffline(String title, String folderPath) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException{
		List<List> articlesInfo= new ArrayList<List>();
		Set<String> revisionArray = this.statics.getTitles(title);
		Iterator<String> it = revisionArray.iterator();
		while (it.hasNext()) {
			  String articleNodeNumber = it.next();
			  List<String> revisionsInfo = this.neo4jQuery.queryArticleNode(articleNodeNumber);
			  articlesInfo.add(revisionsInfo);
			}
		String src =folderPath +  "/" + title + ".txt ";		
		generateArticleRevisionTxtOffline(src, articlesInfo);		
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.ContributionAndRevision#generateArticleRevisionTxtOffline(java.lang.String, java.util.List)
	 */
	@Override
	public void generateArticleRevisionTxtOffline(String src, List<List> ArticlesInfo) throws IOException{
		File f = new File(src);	 
		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter(f));
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
				bw.append("title:" + ArticlesInfo.get(i).get(0) + " ");
				bw.append("revid:" + ArticlesInfo.get(i).get(1) + " ");
				bw.append("time:" + ArticlesInfo.get(i).get(2) + " ");
				bw.append("comment:" + ArticlesInfo.get(i).get(3) + " ");
				String articleNumberUri = (String) ArticlesInfo.get(i).get(4);
				String[] uriArray=articleNumberUri.split("/");
				String NodeNumber=uriArray[6];
				bw.append("nodeNumber:"+ NodeNumber + "\n");
		}
		bw.flush();
		bw.close();
		
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.ContributionAndRevision#showRevisionsInfoByTitle(java.lang.String)
	 */
	@Override
	public List<Object[]> showRevisionsInfoByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		List<List> articlesInfo= new ArrayList<List>();
		Set<String> revisionArray = this.statics.getTitles(title);
		Iterator<String> it = revisionArray.iterator();
		while (it.hasNext()) {
			  String articleNodeNumber = it.next();
			  List<String> revisionsInfo = this.neo4jQuery.queryArticleNode(articleNodeNumber);
			  articlesInfo.add(revisionsInfo);
			}
		
		List<Object[]> resultList=new ArrayList<Object[]>();
		for(int i = 0 ; i < articlesInfo.size(); i++){
			//String title = (String) articlesInfo.get(i).get(0) ;
			String revid = (String) articlesInfo.get(i).get(1);
			String time = (String) articlesInfo.get(i).get(2);
			String comment = (String) articlesInfo.get(i).get(3);
			String articleNumberUri = (String) articlesInfo.get(i).get(4);
			String[] uriArray=articleNumberUri.split("/");
			String nodeNumber=uriArray[6];
			
			Object[] tmpArray=new Object[]{title, revid, time, comment, nodeNumber};
			resultList.add(tmpArray);
		}
		return resultList;
	}
	
}
