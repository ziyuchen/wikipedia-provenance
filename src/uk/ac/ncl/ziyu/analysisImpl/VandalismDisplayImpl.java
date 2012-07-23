/**
 * 
 */
package uk.ac.ncl.ziyu.analysisImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import uk.ac.ncl.ziyu.analysis.Vandalism;
import uk.ac.ncl.ziyu.analysis.VandalismDisplay;
import uk.ac.ncl.ziyu.query.query.Neo4jQuery;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * @author Ziyu CHEN Jul 18, 2012
 * 
 */
public class VandalismDisplayImpl implements VandalismDisplay {
	
	private Neo4jQuery neo4jQuery;
	private Vandalism vandalism;
	
	public VandalismDisplayImpl(){
	}
	
	public VandalismDisplayImpl(Neo4jQuery neo4jQuery, Vandalism vandalism){
		this.neo4jQuery = neo4jQuery;
		this.vandalism = vandalism;
	}

	// *** move to vandalism later *****
	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.VandalismDisplay#getVandalismTxt(java.lang.String)
	 */
	@Override
	public void getVandalismTxt(String folderPath)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, IOException, URISyntaxException {
		String src = folderPath + "/vandalism.txt ";
		generateVandalismTxt(src, null, "all");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.VandalismDisplay#getVandalismTxtByUser(java.lang.String, java.lang.String)
	 */
	@Override
	public void getVandalismTxtByUser(String user, String folderPath)
			throws IOException, ClientHandlerException,
			UniformInterfaceException, JSONException, URISyntaxException {
		String src = folderPath + "/vandalism" + user + ".txt";
		try {
			generateVandalismTxt(src, user, "user");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.VandalismDisplay#getVandalismTxtByTitle(java.lang.String, java.lang.String)
	 */
	@Override
	public void getVandalismTxtByTitle(String title, String folderPath)
			throws IOException, ClientHandlerException,
			UniformInterfaceException, JSONException, URISyntaxException {
		String src = folderPath + "/vandalism" + title + ".txt";
		try {
			generateVandalismTxt(src, title, "title");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ** used by the functions above as the private function ** move to
	// vandalism together with all these three above !

	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.VandalismDisplay#generateVandalismTxt(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void generateVandalismTxt(String src, String name, String flag)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, IOException, URISyntaxException {
		File f = new File(src);
		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter(f));

		Set<String> nodeNumberArray = new HashSet<String>();
		if (flag == "all") {
			nodeNumberArray = this.vandalism.getVandalismArticleNumber();
		} else if (flag == "user") {
			nodeNumberArray = this.vandalism
					.getVandalismArticleNumberByUser(name);
		} else {
			nodeNumberArray = this.vandalism
					.getVandalismArticleNumberByTitle(name);
		}
		for (String nodeNumber : nodeNumberArray) {
			List<String> articleInfoArray = this.neo4jQuery
					.queryArticleNode(nodeNumber);
			for (int i = 0; i < 3; i++) {
				bw.append(articleInfoArray.get(i) + " ");
				System.out.print(articleInfoArray.get(i) + " ");
			}
			bw.append("nodeNumber:" + nodeNumber + " ");
			bw.append("userName:" + this.neo4jQuery.queryUserNode(nodeNumber)
					+ "\n");
			System.out.print("nodeNumber:" + nodeNumber + " ");
			System.out.println("userName:"
					+ this.neo4jQuery.queryUserNode(nodeNumber));
		}
		bw.flush();
		bw.close();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.VandalismDisplay#showVandalism(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Object[]> showVandalism(String flag, String name)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, URISyntaxException {
		Set<String> nodeNumberArray = new HashSet<String>();
		if (flag == "all") {
			nodeNumberArray = this.vandalism.getVandalismArticleNumber();
		} else if (flag == "user") {
			nodeNumberArray = this.vandalism
					.getVandalismArticleNumberByUser(name);
		} else {
			nodeNumberArray = this.vandalism
					.getVandalismArticleNumberByTitle(name);
		}

		List<Object[]> resultList = new ArrayList<Object[]>();
		for (String nodeNumber : nodeNumberArray) {

			List<String> articleInfoArray = this.neo4jQuery
					.queryArticleNode(nodeNumber);
			String title = articleInfoArray.get(0);
			String revid = articleInfoArray.get(1);
			String time = articleInfoArray.get(2);
			String userName = this.neo4jQuery.queryUserNode(nodeNumber);
			Object[] tmpArray = new Object[] { title, revid, time, nodeNumber,
					userName };
			resultList.add(tmpArray);
		}
		return resultList;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ncl.ziyu.analysisImpl.VandalismDisplay#showVandalismTxt(java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, String> showVandalismTxt(String flag, String name)
			throws ClientHandlerException, UniformInterfaceException,
			JSONException, URISyntaxException {
		String vandalismText = new String();
		Set<String> nodeNumberArray = new HashSet<String>();
		if (flag == "all") {
			nodeNumberArray = this.vandalism.getVandalismArticleNumber();
		} else if (flag == "user") {
			nodeNumberArray = this.vandalism
					.getVandalismArticleNumberByUser(name);
		} else {
			nodeNumberArray = this.vandalism
					.getVandalismArticleNumberByTitle(name);
		}
		int count = 0;
		Map<String, String> vandalismAndCount = new HashMap<String, String>();
		for (String nodeNumber : nodeNumberArray) {

			List<String> articleInfoArray = this.neo4jQuery
					.queryArticleNode(nodeNumber);
			for (int i = 0; i < 3; i++) {
				vandalismText = vandalismText + articleInfoArray.get(i) + " ";
			}
			vandalismText = vandalismText + "nodeNumber:" + nodeNumber + " ";
			vandalismText = vandalismText + "userName:"
					+ this.neo4jQuery.queryUserNode(nodeNumber) + "\n";
			count++;
		}

		vandalismAndCount.put("vandalismText", vandalismText);
		vandalismAndCount.put("count", Integer.toString(count));
		return vandalismAndCount;
	}

}
