/**
 * 
 */
package uk.ac.ncl.ziyu.query.queryImpl;

import javax.ws.rs.core.MediaType;

import uk.ac.ncl.ziyu.variable.Variable;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
class Neo4jRest extends Variable{

	 public static void getGeneralDeleteResponse(String cypherUri){
		 WebResource resource = Client.create().resource(cypherUri);
			resource.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class);	
	
	 }
	 
	 public static ClientResponse getGeneralPostResponse(String cypherPayload){
			final String cypherUri = SERVER_ROOT_URI + "cypher";
			WebResource resource = Client.create().resource(cypherUri);
			ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON).entity(cypherPayload)
					.post(ClientResponse.class);
			return response;
		}
	
}
