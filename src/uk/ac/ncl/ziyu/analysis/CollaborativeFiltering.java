/**
 * 
 */
package uk.ac.ncl.ziyu.analysis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface CollaborativeFiltering {

	public List<Object[]> getInterestTitle(String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public List<Object[]> getRecommendTitle(String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public Map<String, Integer> recommendTitle(String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public Map<String, Integer> CalculateAllTitle(String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public Map<String, Integer> CalculateTitle(String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public Set<String> setUser(String user) throws ClientHandlerException,
			UniformInterfaceException, JSONException;

	public Set<String> CollectTitleFromUser(String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public Set<String> CollectUserFromTitle(String title)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

}