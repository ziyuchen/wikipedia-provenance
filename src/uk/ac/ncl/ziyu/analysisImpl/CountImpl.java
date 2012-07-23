/**
 * 
 */
package uk.ac.ncl.ziyu.analysisImpl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONException;

import uk.ac.ncl.ziyu.analysis.Count;
import uk.ac.ncl.ziyu.analysis.Statics;
import uk.ac.ncl.ziyu.analysis.VandalismDisplay;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class CountImpl implements Count {
	
	private Statics statics;
	private VandalismDisplay vandalismDiaplay;
	
	public CountImpl(){
	}
	
	public CountImpl(Statics statics, VandalismDisplay vandalismDiaplay){
		this.statics = statics;
		this.vandalismDiaplay = vandalismDiaplay;
	}
	
	public List<Object[]> countByVandalismToString() throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		ArrayList<Entry<String,Integer>> l = countByVandalism();
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
	 * @see uk.ac.ncl.ziyu.analysisImpl.Count#countByVandalism()
	 */
	@Override
	public ArrayList<Entry<String,Integer>> countByVandalism() throws ClientHandlerException, UniformInterfaceException, JSONException, URISyntaxException{
		Set<String> titleArray = this.statics.getAllTitle();
		Iterator<String> itTitle = titleArray.iterator();
		String title = new String();
		
		Map<String, Integer> orderVandalism = new HashMap<String, Integer>();
		while (itTitle.hasNext()) {
		  title = itTitle.next();
		  Map<String, String> vandalismAndCount = this.vandalismDiaplay.showVandalismTxt("title", title); 
		  String vandalismNumber = vandalismAndCount.get("count");
		  Integer vandalismCountNumber = Integer.parseInt(vandalismNumber);
		  if(vandalismCountNumber!= 0){
			  orderVandalism.put(title, vandalismCountNumber);
		  }
		}
		ArrayList<Entry<String,Integer>> l = order(orderVandalism);
		return l;	
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Count#countByTitleToString()
	 */
	@Override
	public List<Object[]> countByTitleToString() throws ClientHandlerException, UniformInterfaceException, JSONException{
		ArrayList<Entry<String,Integer>> l = countByTitle();
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
	 * @see uk.ac.ncl.ziyu.analysisImpl.Count#countByTitle()
	 */
	@Override
	public ArrayList<Entry<String,Integer>> countByTitle() throws ClientHandlerException, UniformInterfaceException, JSONException{
		Set<String> titleArray = this.statics.getAllTitle();
		Iterator<String> itTitle = titleArray.iterator();
		String title = new String();
		
		Map<String, Integer> orderTitle = new HashMap<String, Integer>();
		while (itTitle.hasNext()) {
		  title = itTitle.next();
		  Map<String, String> userStatistics = this.statics.getUserList(title);
		  String userNumber = userStatistics.get("userNumber");	  
		  Integer userNum = Integer.parseInt(userNumber);
		  orderTitle.put(title, userNum);
		}
		ArrayList<Entry<String,Integer>> l = order(orderTitle);
	
		return l;
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.Count#order(java.util.Map)
	 */
	
	public ArrayList<Entry<String,Integer>> order(Map<String, Integer> orderMap){
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
