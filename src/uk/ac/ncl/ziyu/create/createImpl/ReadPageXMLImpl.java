/**
 * 
 */
package uk.ac.ncl.ziyu.create.createImpl;

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
public class ReadPageXMLImpl {

	private static CreateGraph graphCreator = new CreateGraphImpl(new Neo4jIndexImpl());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ncl.ziyu.create.createImpl.ReadPageXML#startWithPage(java.lang.
	 * String, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	public static void startWithPage(String title, String rvlimit, int depth,
			String uclimit) throws Exception {
		title = title.replaceAll(" ", "%20");
		title = title.replaceAll("=", "%3D");
		title = title.replaceAll("[+]", "%2B");
		System.out.println(title);
		if (depth > 0)
			readXMLbyURL(
					"http://en.wikipedia.org/w/api.php?action=query&titles="
							+ title
							+ "&prop=revisions&rvlimit="
							+ rvlimit
							+ "&rvprop=ids|flags|user|timestamp|comment|size&format=xml",
					depth, uclimit, rvlimit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ncl.ziyu.create.createImpl.ReadPageXML#readXMLbyURL(java.lang.String
	 * , java.lang.String, int, java.lang.String, java.lang.String)
	 */
	private static void readXMLbyURL(String fileName, int depth,
			String uclimit, String rvlimit) throws Exception {
		SAXBuilder builder = new SAXBuilder();

		Document doc = builder.build(fileName);
		readXML(doc, depth, uclimit, rvlimit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ncl.ziyu.create.createImpl.ReadPageXML#readXML(org.jdom2.Document,
	 * java.lang.String, int, java.lang.String, java.lang.String)
	 */
	private static void readXML(Document doc, int depth, String uclimit,
			String rvlimit) throws Exception {

		Element root = doc.getRootElement();

		List queryNo = root.getChildren("query");
		depth--;
		for (int i = 0; i < queryNo.size(); i++) {
			Element queryElenemt = (Element) queryNo.get(i);

			List pagesNo = queryElenemt.getChildren("pages");
			for (int j = 0; j < pagesNo.size(); j++) {
				Element pagesElement = (Element) pagesNo.get(j);

				List pageNo = pagesElement.getChildren("page");
				for (int k = 0; k < pageNo.size(); k++) {
					Element pageElement = (Element) pageNo.get(k);
					String pageid = pageElement.getAttribute("pageid")
							.getValue();
					String title = pageElement.getAttribute("title").getValue();
					System.out.println("pageid:" + pageid + "  title:" + title);

					List revisionNo = pageElement.getChildren("revisions");
					String user = "";
					for (int a = 0; a < revisionNo.size(); a++) {
						Element revisionElement = (Element) revisionNo.get(a);

						List revNo = revisionElement.getChildren("rev");

						for (int b = 0; b < revNo.size(); b++) {
							Element revElement = (Element) revNo.get(b);
							String revid = revElement.getAttribute("revid")
									.getValue();

							String parentid = revElement.getAttribute(
									"parentid").getValue();
							user = revElement.getAttribute("user").getValue();
							String time = revElement.getAttribute("timestamp")
									.getValue();

							String comment = new String();
							try {
								comment = revElement.getAttribute("comment")
										.getValue();
							} catch (Exception e1) {
								comment = "null";
							}
							String size = new String();
							try {
								size = revElement.getAttribute("size")
										.getValue();
							} catch (Exception e1) {
								size = "0";
							}

							graphCreator.getData(title, revid, parentid, user,
									time, comment, size, pageid);

							ReadUserXMLImpl.startWithUser(user, uclimit, depth,
									rvlimit);
						}

					}
				}
			}

			List query_continueNo = root.getChildren("query-continue");
			for (int c = 0; c < query_continueNo.size(); c++) {
				Element qc_revisionsElement = (Element) query_continueNo.get(c);
				List qc_revisionsNo = qc_revisionsElement
						.getChildren("revisions");
				for (int d = 0; d < qc_revisionsNo.size(); d++) {
					Element rvstartidElement = (Element) qc_revisionsNo.get(d);
					String rvstartid = rvstartidElement.getAttribute(
							"rvstartid").getValue();
					System.out.println("rvstartid:" + rvstartid);
				}
			}
		}

	}

}
