package rss;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class RssFetchTest {
    public static void main(String[] args) {
        String url = "http://golazogoal.com/feed/";
        try {

            Client client = Client.create();

            WebResource webResource = client
                    .resource(url);

            ClientResponse response = webResource.accept("application/rss+xml")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            String rss = response.getEntity(String.class);

            InputSource source = new InputSource(new StringReader(rss));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(source);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            XPathExpression expression = xpath.compile("/rss/channel/item");
            XPathExpression expTitle = xpath.compile("title");
            XPathExpression expPubDate = xpath.compile("pubDate");

            XPathExpression expCategory = xpath.compile("category");
            XPathExpression expGuid = xpath.compile("guid");
            XPathExpression expDescription = xpath.compile("description");
            XPathExpression expContent = xpath.compile("encoded");
            NodeList nodeList = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                //book[price>35.00]

                Node titleNode = (Node)expTitle.evaluate(item, XPathConstants.NODE);
                String title = titleNode.getFirstChild().getNodeValue();

                Node pubDateNode = (Node)expPubDate.evaluate(item, XPathConstants.NODE);
                String pubDate = pubDateNode.getFirstChild().getNodeValue();

                NodeList categories = (NodeList)expCategory.evaluate(item, XPathConstants.NODESET);
                for (int x=0; x < categories.getLength(); x++) {
                    Node item1 = categories.item(x);
                    String category = item1.getFirstChild().getNodeValue();
                }

                Node guidNode = (Node)expGuid.evaluate(item, XPathConstants.NODE);
                String guid = guidNode.getFirstChild().getNodeValue();

                Node descriptionNode = (Node)expDescription.evaluate(item, XPathConstants.NODE);
                String description = descriptionNode.getFirstChild().getNodeValue();

                Node contetnNode = (Node)expContent.evaluate(item, XPathConstants.NODE);
                String content = contetnNode.getFirstChild().getNodeValue();



                System.out.println(nodeList.item(i).getNodeValue());
            }
            //System.out.println(output);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
