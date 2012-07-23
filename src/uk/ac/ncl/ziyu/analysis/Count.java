/**
 * 
 */
package uk.ac.ncl.ziyu.analysis;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN Jul 18, 2012
 * 
 */
public interface Count {

	public List<Object[]> countByVandalismToString()
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, URISyntaxException;

	public ArrayList<Entry<String, Integer>> countByVandalism()
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, URISyntaxException;

	public List<Object[]> countByTitleToString() throws ClientHandlerException,
			UniformInterfaceException, JSONException;

	public ArrayList<Entry<String, Integer>> countByTitle()
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public ArrayList<Entry<String, Integer>> order(Map<String, Integer> orderMap);

}