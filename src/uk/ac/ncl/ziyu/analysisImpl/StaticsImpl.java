/**
 * 
 */
package uk.ac.ncl.ziyu.analysisImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import uk.ac.ncl.ziyu.analysis.Count;
import uk.ac.ncl.ziyu.analysis.Statics;
import uk.ac.ncl.ziyu.query.query.Neo4jQuery;


import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;


/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class StaticsImpl implements Statics {
	
	private Neo4jQuery neo4jQuery;
	
	public StaticsImpl(){
	}
	
	public StaticsImpl(Neo4jQuery neo4jQuery){
		this.neo4jQuery = neo4jQuery;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#getNumberOfRevision()
	 */
	@Override
	public String getNumberOfRevision(){
		ClientResponse response = this.neo4jQuery.countRevisionNumber();
		String numberOfRevision = new String();
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			if(json.has("data")){
			JSONArray getData = json.getJSONArray("data");
			JSONArray test = new JSONArray();
			if(!getData.isNull(0)){
				test = getData.getJSONArray(0);
				numberOfRevision = test.getString(0);
			}else{
				numberOfRevision = "0";
			}
			}else{numberOfRevision = "0";}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		response.close();
		return numberOfRevision;
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#listTitleOrGetNumber(java.lang.String)
	 */
	@Override
	public String listTitleOrGetNumber(String flagTitle){
		Set<String> titleArray = getAllTitle();
		Iterator<String> itTitle = titleArray.iterator();
		int count = 0;
		String title = new String();
		while (itTitle.hasNext()) {
		  title = title + itTitle.next()+ "\n" ;		  
		  count++;
		}
		if(flagTitle == "title") {
			return title;
			}else{
				return Integer.toString(count);
			}		
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#listAllTitle()
	 */
	@Override
	public List<Object[]> listAllTitle(){
		Set<String> titleArray = getAllTitle();
		Iterator<String> itTitle = titleArray.iterator();
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
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#getAllTitle()
	 */
	@Override
	public Set<String> getAllTitle(){	
		ClientResponse response = this.neo4jQuery.getAllArticlesInfo();
		Set<String> titleArray = findAllTitle(response);
		return titleArray;	
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#findAllTitle(com.sun.jersey.api.client.ClientResponse)
	 */
	@Override
	public Set<String> findAllTitle(ClientResponse response){
		Set<String> titleArray = new HashSet<String>();
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
					
			for(int count = 0; count < getData.length(); count++){
				JSONArray getNode;
				getNode = getData.getJSONArray(count);
			
				JSONObject getNodeInfo = getNode.getJSONObject(0);
				JSONObject getNodeData = getNodeInfo.getJSONObject("data");
				String getNodeTitle = getNodeData.getString("title");
				titleArray.add(getNodeTitle);
			}
		} catch (Exception e) {
//			System.err.println(e.getMessage());			// these two lines should be commented , otherwise " Index: 0, Size: 0 "
//			System.err.println(e.getStackTrace());		
		}
		response.close();		
		return titleArray;
	}
	
//===========================Find user name list and number by article title========================================================================================	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#getUserList(java.lang.String)
	 */
	@Override
	public Map<String, String> getUserList(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		String userList = new String();
		ArrayList<Entry<String, Integer>> userOrderList = findUserList(title);	
		int countUserNumber = 0;
		for(Entry<String,Integer> e : userOrderList) { 
			userList = userList + e.getKey() + "    " + e.getValue() + "\n";
			countUserNumber++;
        }
		

		Map<String, String> statisticsMap = new HashMap<String, String>();
		statisticsMap.put("userList", userList);
		statisticsMap.put("userNumber", Integer.toString(countUserNumber));
		return statisticsMap;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#findUserList(java.lang.String)
	 */
	@Override
	public ArrayList<Entry<String, Integer>> findUserList(String title) throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> titleArray = getTitles(title);
		Iterator<String> itTitle = titleArray.iterator();
		Map<String, Integer> userMap = new HashMap<String, Integer>();
		while (itTitle.hasNext()) {
		  String nodeNumber = itTitle.next();
		  String user = this.neo4jQuery.queryUserNode(nodeNumber);		
			if(userMap.containsKey(user) && (user!=null) ){
				Integer countNumber = userMap.get(user);
				countNumber++;
				userMap.put(user, countNumber);
			}else if(user!=null){				
				userMap.put(user, 1);
				}	  
		}
		ArrayList<Entry<String,Integer>> l = this.order(userMap);
		return l;
	}	
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#getTitles(java.lang.String)
	 */
	@Override
	public Set<String> getTitles(String title){	
		ClientResponse response = this.neo4jQuery.getAllArticlesInfo();
		Set<String> titleArray = IteratorAllRevision(response, title);
		return titleArray;	
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Statics#IteratorAllRevision(com.sun.jersey.api.client.ClientResponse, java.lang.String)
	 */
	@Override
	public Set<String> IteratorAllRevision(ClientResponse response, String title){
		Set<String> titleRevisionArray = new HashSet<String>();
		try {
			JSONObject json = new JSONObject(response.getEntity(String.class));
			JSONArray getData = json.getJSONArray("data");
					
			for(int count = 0; count < getData.length(); count++){
				JSONArray getNode;
				getNode = getData.getJSONArray(count);
			
				JSONObject getNodeInfo = getNode.getJSONObject(0);
				JSONObject getNodeData = getNodeInfo.getJSONObject("data");
				String getNodeUri = getNodeInfo.getString("self");
				String getNodeTitle = getNodeData.getString("title");
				String[] uriArray=getNodeUri.split("/");
				String NodeNumber=uriArray[6];
				if(getNodeTitle.equals(title)) titleRevisionArray.add(NodeNumber);
				
			}
		} catch (Exception e) {
		}
		response.close();		
		return titleRevisionArray;
	}
	
	private ArrayList<Entry<String,Integer>> order(Map<String, Integer> orderMap){
		Map<String, Integer> keyfreqs = orderMap;  
		ArrayList<Entry<String,Integer>> l = new ArrayList<Entry<String,Integer>>(keyfreqs.entrySet());    	          
	    Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {    
	        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {    
	            return (o2.getValue() - o1.getValue());    
	        }    
	    });      
	        return l;
	}
}
