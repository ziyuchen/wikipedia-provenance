/**
 * 
 */
package uk.ac.ncl.ziyu.create.createImpl;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import uk.ac.ncl.ziyu.create.create.CreateGraph;
import uk.ac.ncl.ziyu.query.queryImpl.Neo4jIndexImpl;


/**
 * @author Ziyu CHEN Jul 18, 2012
 * 
 */
public class ReadUserXMLImpl {

	private static CreateGraph graphCreator = new CreateGraphImpl(new Neo4jIndexImpl());

	// private ReadPageXML readPageXML;

	// public ReadUserXMLImpl(CreateGraph graphCreator) {
	// this.graphCreator = graphCreator;
	// }
	//
	// public void setReadPageXML(ReadPageXML readPageXML){
	// this.readPageXML = readPageXML;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ncl.ziyu.create.createImpl.ReadUserXML#startWithUser(java.lang.
	 * String, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	public static List<String> startWithUser(String user, String uclimit,
			int depth, String rvlimit) throws Exception {
		user = user.replaceAll(" ", "%20");
		user = user.replaceAll("=", "%3D");
		user = user.replaceAll("[+]", "%2B");
		List<String> userTxt = new ArrayList<String>();
		if (depth > 0)
			userTxt = readXMLbyURL(
					"http://en.wikipedia.org/w/api.php?action=query&list=usercontribs&ucuser="
							+ user + "&uclimit=" + uclimit + "&format=xml",
					depth, uclimit, rvlimit);
		return userTxt;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ncl.ziyu.create.createImpl.ReadUserXML#readXMLbyURL(java.lang.String
	 * , java.lang.String, int, java.lang.String, java.lang.String)
	 */
	private static List<String> readXMLbyURL(String fileName, int depth,
			String uclimit, String rvlimit) throws Exception {
		SAXBuilder builder = new SAXBuilder();

		Document doc = builder.build(fileName);
		List<String> userTxt = readXML(doc, depth, uclimit, rvlimit);
		return userTxt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ncl.ziyu.create.createImpl.ReadUserXML#readXML(org.jdom2.Document,
	 * java.lang.String, int, java.lang.String, java.lang.String)
	 */
	private static List<String> readXML(Document doc, int depth,
			String uclimit, String rvlimit) throws Exception {

		List<String> userTxt = new ArrayList<String>();
		Element root = doc.getRootElement();

		List queryNo = root.getChildren("query");

		depth--;

		for (int i = 0; i < queryNo.size(); i++) {
			Element queryElenemt = (Element) queryNo.get(i);

			List pagesNo = queryElenemt.getChildren("usercontribs");
			String title = "";
			for (int j = 0; j < pagesNo.size(); j++) {
				Element usercontribsElement = (Element) pagesNo.get(j);

				List revNo = usercontribsElement.getChildren("item");

				for (int b = 0; b < revNo.size(); b++) {
					Element revElement = (Element) revNo.get(b);
					String userid = revElement.getAttribute("userid")
							.getValue();
					String user = revElement.getAttribute("user").getValue();
					String pageid = revElement.getAttribute("pageid")
							.getValue();
					String revid = revElement.getAttribute("revid").getValue();
					title = revElement.getAttribute("title").getValue();
					String time = revElement.getAttribute("timestamp")
							.getValue();
					String comment = new String();
					try {

						comment = revElement.getAttribute("comment").getValue();
					} catch (Exception e1) {
						comment = "null";
					}
					String size = new String();
					try {
						size = revElement.getAttribute("size").getValue();
					} catch (Exception e1) {
						size = "0";
					}

					graphCreator.getUserData(title, revid, user, time, comment,
							size, pageid);
					userTxt.add("userid:" + userid + "  user:" + user
							+ "  pageid:" + pageid + "  revid:" + revid
							+ " title:" + title + " time:" + time + " comment:"
							+ comment + " size:" + size + "\n");
					ReadPageXMLImpl.startWithPage(title, rvlimit, depth,
							uclimit);
				}
			}
			List query_continueNo = root.getChildren("query-continue");
			for (int c = 0; c < query_continueNo.size(); c++) {
				Element qc_revisionsElement = (Element) query_continueNo.get(c);
				List qc_revisionsNo = qc_revisionsElement
						.getChildren("usercontribs");
				for (int d = 0; d < qc_revisionsNo.size(); d++) {
					Element rvstartidElement = (Element) qc_revisionsNo.get(d);
					String ucstart = rvstartidElement.getAttribute("ucstart")
							.getValue();
				}
			}
		}

		return userTxt;
	}

}
