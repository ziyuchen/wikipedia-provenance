/**
 * 
 */
package uk.ac.ncl.ziyu.analysisImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONException;

import uk.ac.ncl.ziyu.analysis.CollaborativeFiltering;
import uk.ac.ncl.ziyu.analysis.Count;
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
public class CollaborativeFilteringImpl implements CollaborativeFiltering {
	
	private Count count;
	private Neo4jIndex neo4jIndex;
	private Neo4jQuery neo4jQuery;
	private Statics statics;
	
	public CollaborativeFilteringImpl(){
	}
	
	public CollaborativeFilteringImpl(Count count, Neo4jIndex neo4jIndex, Neo4jQuery neo4jQuery, Statics statics){
		this.count=count;
		this.neo4jIndex = neo4jIndex;
		this.neo4jQuery = neo4jQuery;
		this.statics = statics;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.CollaborativeFiltering#getInterestTitle(java.lang.String)
	 */
	@Override
	public List<Object[]> getInterestTitle(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> getTitle = CollectTitleFromUser(user);
		Iterator<String> itTitle = getTitle.iterator();
		String title = new String();
		List<Object[]> resultList=new ArrayList<Object[]>();
		while (itTitle.hasNext()) {
		  title = itTitle.next();	
		  Object[] tmpArray=new Object[]{title};
		  resultList.add(tmpArray);	
		}
		return resultList;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.CollaborativeFiltering#getRecommendTitle(java.lang.String)
	 */
	@Override
	public List<Object[]> getRecommendTitle(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Map<String, Integer> getRecommendTitleMap = new HashMap<String, Integer>();

		getRecommendTitleMap = recommendTitle(user);
			
//		System.out.println("=========================================================");
		for(String strTitle : getRecommendTitleMap.keySet()){
			Integer value = getRecommendTitleMap.get(strTitle);
	//		System.out.println(strTitle+ "::::" +value);
			}
		
		ArrayList<Entry<String,Integer>> l = this.count.order(getRecommendTitleMap);
		
		List<Object[]> resultList=new ArrayList<Object[]>();
		for(Entry<String,Integer> e : l) { 
			String title = e.getKey();
			String value = Integer.toString(e.getValue());		
			Object[] tmpArray=new Object[]{title, value};
			resultList.add(tmpArray);
        }
		return resultList;
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.CollaborativeFiltering#recommendTitle(java.lang.String)
	 */
	@Override
	public Map<String, Integer> recommendTitle(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Map<String, Integer> getRecommendTitleMap = new HashMap<String, Integer>();
		
		Map<String, Integer> getAllTitleMap = new HashMap<String, Integer>();
		getAllTitleMap = CalculateAllTitle(user);
			
		Set<String> getTitle = new HashSet<String>();
		getTitle = CollectTitleFromUser(user);	
				
		for(String strTitle : getAllTitleMap.keySet()){
			if(!getTitle.contains(strTitle)){
				Integer value = getAllTitleMap.get(strTitle);
				getRecommendTitleMap.put(strTitle, value);
			}			
		}
		return getRecommendTitleMap;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.CollaborativeFiltering#CalculateAllTitle(java.lang.String)
	 */
	@Override
	public Map<String, Integer> CalculateAllTitle(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> getUser = new HashSet<String>();
		getUser = setUser(user);		
		Iterator<String> itUser = getUser.iterator();
		
		Map<String, Integer> getTitleMap = new HashMap<String, Integer>();	
		Map<String, Integer> getTotalTitleMap = new HashMap<String, Integer>();
		
		while(itUser.hasNext()){
			String listUser = itUser.next();
			getTitleMap = CalculateTitle(listUser);
			
			for(String strTitle : getTitleMap.keySet()){
				Integer value = getTitleMap.get(strTitle);
				
				if(getTotalTitleMap.containsKey(strTitle)){
					int counter = 0;
					Integer countNumber = getTotalTitleMap.get(strTitle);
					counter = countNumber+value;
					getTotalTitleMap.put(strTitle, counter);
				}else{				
					getTotalTitleMap.put(strTitle, value);
					}				
			}
		}
		
		return getTotalTitleMap;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.CollaborativeFiltering#CalculateTitle(java.lang.String)
	 */
	@Override
	public Map<String, Integer> CalculateTitle(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Map<String, Integer> titleMap = new HashMap<String, Integer>();	
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String[] uriArray=specialUserNodeUri.split("/");
		String userNodeNumber=uriArray[6];
		List<ArrayList> ArticlesInfo = this.neo4jQuery.getArticlesInfoByUserNode(userNodeNumber);	
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
			String title=(String) ArticlesInfo.get(i).get(0);
			if(titleMap.containsKey(title) && (title!=null) ){
				Integer countNumber = titleMap.get(title);
				countNumber++;
				titleMap.put(title, countNumber);
			}else if(title!=null){				
				titleMap.put(title, 1);
				}	 			
		}		
		return titleMap;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.CollaborativeFiltering#setUser(java.lang.String)
	 */
	@Override
	public Set<String> setUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> getTitle = new HashSet<String>();
		getTitle = CollectTitleFromUser(user);		
		Iterator<String> it = getTitle.iterator();
		Set<String> getUser = new HashSet<String>();
		Set<String> userSet = new HashSet<String>();
		while(it.hasNext()){
			String title = it.next();		
			getUser = CollectUserFromTitle(title);			
			Iterator<String> it2 = getUser.iterator();
			while(it2.hasNext()){
				String strUser = it2.next();
				userSet.add(strUser);
			}
		}	
		return userSet;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.CollaborativeFiltering#CollectTitleFromUser(java.lang.String)
	 */
	@Override
	public Set<String> CollectTitleFromUser(String user) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String specialUserNodeUri = this.neo4jIndex.queryNodeOrRelationship("node", "userNodeIndex", "username", user);
		String[] uriArray=specialUserNodeUri.split("/");
		String userNodeNumber=uriArray[6];
		List<ArrayList> ArticlesInfo = this.neo4jQuery.getArticlesInfoByUserNode(userNodeNumber);		
		Set<String> setTitle = new HashSet<String>();
		for(int i = 0 ; i < ArticlesInfo.size(); i++){
			String title=(String) ArticlesInfo.get(i).get(0);
			setTitle.add(title);
		}
		return setTitle;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.CollaborativeFiltering#CollectUserFromTitle(java.lang.String)
	 */
	@Override
	public Set<String> CollectUserFromTitle(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		ArrayList<Entry<String, Integer>> userOrderList = this.statics.findUserList(title);	
		Set<String> setUser = new HashSet<String>();
		for(Entry<String,Integer> e : userOrderList) { 
			setUser.add(e.getKey());
        }
		return setUser;
	}

}
