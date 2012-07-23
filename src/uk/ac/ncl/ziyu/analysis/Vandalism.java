/**
 * 
 */
package uk.ac.ncl.ziyu.analysis;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface Vandalism {

	public Set<String> getVandalismArticleNumberByUser(String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, URISyntaxException;

	public Set<String> getVandalismArticleNumber();

	public Set<String> getVandalismArticleNumberByTitle(String title)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public Set<String> runVandalismByTitle(List<List> articlesInfo)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public Set<String> runVandalismArticleNumber(ClientResponse response,
			String flagKind);

	public boolean compareSize(String nodeNumber, String size);

	public String checkSpecialString(String nodeNumber)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

}