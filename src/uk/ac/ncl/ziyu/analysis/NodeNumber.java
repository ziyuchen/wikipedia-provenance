/**
 * 
 */
package uk.ac.ncl.ziyu.analysis;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface NodeNumber {

	public String findNodeNumberByUser(String user);

	public ArrayList<String> findUserInfoByRevid(String revid)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public String splitNodeUri(String specialNodeUri);

	public String getUserLatestEdit(String user) throws ClientHandlerException,
			UniformInterfaceException, JSONException, IOException;

	public String getArticleLatestEdit(String title)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public String getRevisonInfoByRevid(String revid)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public String getRevisonByTitleAndUser(String title, String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

}