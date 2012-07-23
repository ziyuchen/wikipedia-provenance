/**
 * 
 */
package uk.ac.ncl.ziyu.operation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;

import uk.ac.ncl.ziyu.analysis.CollaborativeFiltering;
import uk.ac.ncl.ziyu.analysis.ContributionAndRevision;
import uk.ac.ncl.ziyu.analysis.Count;
import uk.ac.ncl.ziyu.analysis.NodeNumber;
import uk.ac.ncl.ziyu.analysis.Statics;
import uk.ac.ncl.ziyu.analysis.Vandalism;
import uk.ac.ncl.ziyu.analysis.VandalismDisplay;
import uk.ac.ncl.ziyu.analysisImpl.CollaborativeFilteringImpl;
import uk.ac.ncl.ziyu.analysisImpl.ContributionAndRevisionImpl;
import uk.ac.ncl.ziyu.analysisImpl.CountImpl;
import uk.ac.ncl.ziyu.analysisImpl.NodeNumberImpl;
import uk.ac.ncl.ziyu.analysisImpl.StaticsImpl;
import uk.ac.ncl.ziyu.analysisImpl.VandalismDisplayImpl;
import uk.ac.ncl.ziyu.analysisImpl.VandalismImpl;
import uk.ac.ncl.ziyu.create.create.Delete;
import uk.ac.ncl.ziyu.create.createImpl.DeleteImpl;
import uk.ac.ncl.ziyu.create.createImpl.ReadPageXMLImpl;
import uk.ac.ncl.ziyu.create.createImpl.ReadUserXMLImpl;
import uk.ac.ncl.ziyu.query.query.Neo4jIndex;
import uk.ac.ncl.ziyu.query.query.Neo4jQuery;
import uk.ac.ncl.ziyu.query.queryImpl.Neo4jIndexImpl;
import uk.ac.ncl.ziyu.query.queryImpl.Neo4jQueryImpl;


import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class Operation {
	
	private static Neo4jIndex neo4jIndex= new Neo4jIndexImpl();
	private static Neo4jQuery neo4jQuery=new Neo4jQueryImpl();
	private static Statics statics=new StaticsImpl(neo4jQuery);
	private static Vandalism vandalism=new VandalismImpl(neo4jIndex,neo4jQuery,statics);
	private static VandalismDisplay vandalismDisplay=new VandalismDisplayImpl(neo4jQuery,vandalism);
	private static Count count=new CountImpl(statics, vandalismDisplay);
	private static ContributionAndRevision contributionAndrevision=new ContributionAndRevisionImpl(neo4jIndex,neo4jQuery,statics);
	private static NodeNumber nodeNumber=new NodeNumberImpl(neo4jIndex,neo4jQuery,statics); 
	private static CollaborativeFiltering collaborativeFiltering=new CollaborativeFilteringImpl(count,neo4jIndex,neo4jQuery,statics);
	private static Delete delete=new DeleteImpl(neo4jIndex,neo4jQuery);
	
	
	
	public static void queryByArticle(String title, String rvlimit, int depth, String uclimit) throws Exception{
		neo4jIndex.createIndex();
		ReadPageXMLImpl.startWithPage(title, rvlimit, depth, uclimit);
	}
	
	public static void queryByUser(String user, String uclimit, int depth, String rvlimit) throws Exception{
		neo4jIndex.createIndex();
		ReadUserXMLImpl.startWithUser(user, uclimit,depth, rvlimit);
	}
	
	public static void GetContributionsByUserOffline(String user, String folderPath) throws Exception{
		contributionAndrevision.getUserContribsTxtOffline(user, folderPath);
	}
	
	public static void getArticleInfoByTitleOffline(String title, String folderPath) throws Exception{
		contributionAndrevision.getArticleRevisionTxtOffline(title, folderPath);
	}
	
	public static List<Object[]> showContrisByUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
//		Map<String, String> userContribsAndCount = GenerateTxt.showContrisByUser(user);
		List<Object[]> objectArray=contributionAndrevision.showContrisByUser(user);
//		return userContribsAndCount;
		return objectArray;
	}
	
	public static List<Object[]> showRevisionInfoByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		List<Object[]> articleInfoAndCount=new ArrayList<Object[]>();
		articleInfoAndCount = contributionAndrevision.showRevisionsInfoByTitle(title);
		return articleInfoAndCount;
	}
	
	public static void GetVandalism(String folderPath) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException, URISyntaxException{
		vandalismDisplay.getVandalismTxt(folderPath);
	}
	
	public static void getVandalismByUser(String folderPath, String user) throws ClientHandlerException, UniformInterfaceException, IOException, JSONException, URISyntaxException{
		vandalismDisplay.getVandalismTxtByUser(user, folderPath);
	}
	
	public static void getVandalismByTitle(String folderPath, String title) throws ClientHandlerException, UniformInterfaceException, IOException, JSONException, URISyntaxException{
		vandalismDisplay.getVandalismTxtByTitle(title, folderPath);
	}
	
	public static List<Object[]> showAllVandalism() throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		List<Object[]> vandalismAndCount = new ArrayList<Object[]>();
		vandalismAndCount = vandalismDisplay.showVandalism("all", null);
		return vandalismAndCount;
	}
	
	public static List<Object[]> showVandalismByUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		List<Object[]> vandalismAndCount = new ArrayList<Object[]>();
		vandalismAndCount = vandalismDisplay.showVandalism("user", user);
		return vandalismAndCount;
	}
	
	public static List<Object[]> showVandalismByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		List<Object[]> vandalismAndCount = new ArrayList<Object[]>();
		vandalismAndCount = vandalismDisplay.showVandalism("title", title);
		return vandalismAndCount;
	}
	
	public static List<Object[]> listTitle(){
		List<Object[]> title = statics.listAllTitle();
		return title;
	}
	
	public static String countTitle(){
		String countTitle = statics.listTitleOrGetNumber("count");
		return countTitle;
	}
	
	public static String getTheNumberOfRevision(){
		return statics.getNumberOfRevision();
	}
	
	public static Map<String, String> getUserListByTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Map<String, String> userStatistics = statics.getUserList(title);
		return userStatistics;
	}

	
	public static String findNodeNumberByUserName(String user){
		String returnNodeNumber = nodeNumber.findNodeNumberByUser(user);
		return returnNodeNumber;
	}
	
	public static ArrayList<String> findUserByRevid(String revid) throws ClientHandlerException, UniformInterfaceException, JSONException{
		ArrayList<String> userList = nodeNumber.findUserInfoByRevid(revid);
		return userList;
	}
	
	public static String findUserLastestInfo(String user) throws ClientHandlerException, UniformInterfaceException, JSONException, IOException{
		String userLatestEdit = nodeNumber.getUserLatestEdit(user);
		return userLatestEdit;
	}
	
	public static String findArticleLatestInfo(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String revisionLatestEdit = nodeNumber.getArticleLatestEdit(title);
		return revisionLatestEdit;
	}
	
	public static String findRevisionInfoByRevid(String revid) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String revInfo = nodeNumber.getRevisonInfoByRevid(revid);
		return revInfo;
	}
	
	public static String findRevisionByTitleAndUser(String title, String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String revInfoByTitleAndUser = nodeNumber.getRevisonByTitleAndUser(title, user);
		return revInfoByTitleAndUser;
	}
	
	public static List<Object[]> countTitleUserNumber() throws ClientHandlerException, UniformInterfaceException, JSONException{
		List<Object[]> countTitleNumber = count.countByTitleToString();
		return countTitleNumber;
	}
	
	public static List<Object[]> countTitleVandalismNumber() throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		List<Object[]> countVandalismNumber = count.countByVandalismToString();
		return countVandalismNumber;
	}
	
	
	public static List<Object[]> recommendTitle(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		List<Object[]> recommendTitle = collaborativeFiltering.getRecommendTitle(user);
		return recommendTitle;
	}
	
	public static List<Object[]> InterestTitle(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		List<Object[]> interestTitle = collaborativeFiltering.getInterestTitle(user);
		return interestTitle;
	}
	
	
	public static void deleteIndex(){
		delete.deleteAllIndex();
	}
	
	public static void deleteRelationshipAndNode(){
		delete.deleteRelationship();
		delete.deleteNode();
	}

}
