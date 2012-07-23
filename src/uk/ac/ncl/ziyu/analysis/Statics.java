/**
 * 
 */
package uk.ac.ncl.ziyu.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface Statics {

	public String getNumberOfRevision();

	public String listTitleOrGetNumber(String flagTitle);

	public List<Object[]> listAllTitle();

	public Set<String> getAllTitle();

	public Set<String> findAllTitle(ClientResponse response);

	//===========================Find user name list and number by article title========================================================================================	
	public Map<String, String> getUserList(String title)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public ArrayList<Entry<String, Integer>> findUserList(String title)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public Set<String> getTitles(String title);

	public Set<String> IteratorAllRevision(ClientResponse response, String title);

}