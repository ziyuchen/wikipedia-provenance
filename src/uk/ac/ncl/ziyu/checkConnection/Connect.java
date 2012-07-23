/**
 * 
 */
package uk.ac.ncl.ziyu.checkConnection;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import uk.ac.ncl.ziyu.variable.Variable;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public class Connect extends Variable{
	
	public static void checkDatabaseIsRunning(){
		WebResource resource = Client.create().resource(SERVER_ROOT_URI);
		ClientResponse response = resource.get(ClientResponse.class);
		
		System.out.println(String.format("GET on [%s], status code [%d]",SERVER_ROOT_URI, response.getStatus()));
		response.close();
	}

}
