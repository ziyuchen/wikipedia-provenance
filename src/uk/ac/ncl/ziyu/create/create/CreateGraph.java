/**
 * 
 */
package uk.ac.ncl.ziyu.create.create;

import java.net.URISyntaxException;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface CreateGraph {

	public void getData(String title, String revid, String parentid,
			String user, String time, String comment, String size,
			String pageid) throws URISyntaxException,
			ClientHandlerException, UniformInterfaceException, JSONException;

	public void getUserData(String title, String revid, String user,
			String time, String comment, String size, String pageid) throws URISyntaxException,
			ClientHandlerException, UniformInterfaceException, JSONException;

}