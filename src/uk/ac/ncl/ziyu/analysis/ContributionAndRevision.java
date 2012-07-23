/**
 * 
 */
package uk.ac.ncl.ziyu.analysis;

import java.io.IOException;
import java.util.List;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface ContributionAndRevision {

	public void getUserContribsTxtOffline(String user, String folderPath)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, IOException;

	public List<Object[]> showContrisByUser(String user)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

	public void getArticleRevisionTxtOffline(String title, String folderPath)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, IOException;

	public void generateArticleRevisionTxtOffline(String src,
			List<List> ArticlesInfo) throws IOException;

	public List<Object[]> showRevisionsInfoByTitle(String title)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException;

}