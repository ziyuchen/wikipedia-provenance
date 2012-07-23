/**
 * 
 */
package uk.ac.ncl.ziyu.analysis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface VandalismDisplay {

	// *** move to vandalism later *****
	public void getVandalismTxt(String folderPath)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, IOException, URISyntaxException;

	public void getVandalismTxtByUser(String user, String folderPath)
			throws IOException, ClientHandlerException,
			UniformInterfaceException, JSONException, URISyntaxException;

	public void getVandalismTxtByTitle(String title, String folderPath)
			throws IOException, ClientHandlerException,
			UniformInterfaceException, JSONException, URISyntaxException;

	public void generateVandalismTxt(String src, String name, String flag)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, IOException, URISyntaxException;

	public List<Object[]> showVandalism(String flag, String name)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, URISyntaxException;

	public Map<String, String> showVandalismTxt(String flag, String name)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, URISyntaxException;

}