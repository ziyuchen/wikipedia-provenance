/**
 * 
 */
package uk.ac.ncl.ziyu.create.create;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Ziyu CHEN
 * Jul 18, 2012
 *
 */
public interface Delete {

	public void deleteAllIndex();

	public void deleteRelationship();

	public void deleteNode();

	public void deleteAllNodeOrRelationship(ClientResponse response, String type);

}