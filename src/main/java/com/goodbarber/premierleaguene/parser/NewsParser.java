package com.goodbarber.premierleaguene.parser;

import com.goodbarber.premierleaguene.domain.Category;
import com.goodbarber.premierleaguene.domain.CategoryUpdateState;
import com.goodbarber.premierleaguene.domain.CategoryUpdateType;
import com.goodbarber.premierleaguene.domain.News;
import com.goodbarber.premierleaguene.repository.CategoryRepository;
import com.goodbarber.premierleaguene.repository.NewsRepository;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewsParser implements Runnable{

    public static final Logger LOGGER = LoggerFactory.getLogger(NewsParser.class);
    private String categoryName;

    public NewsParser(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public void run() {
        updateCategories();
    }

    private void updateCategories() {
        List<Category> categories = new ArrayList<>(1);
        if (categoryName != null) {
            Category category = CategoryRepository.get(categoryName);
            categories.add(category);
        } else {
            categories = CategoryRepository.list(0, Integer.MAX_VALUE, false);
        }

        for (Category category : categories) {
            try {

                Client client = Client.create();

                WebResource webResource = client
                        .resource(category.rssFeed);

                ClientResponse response = webResource.accept("application/rss+xml")
                        .get(ClientResponse.class);

                if (response.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatus());
                }

                String output = response.getEntity(String.class);

                List<News> convert = convert(output);

                LOGGER.debug("------- Starting Saving Category " + category.name + " Newses");
                NewsRepository.saveRss(convert);

                LOGGER.debug("------- Finish Saving Category " + category.name + " Newses");
                CategoryRepository.update(category.name, CategoryUpdateState.SUCCESS, CategoryUpdateType.JOB);
            } catch (Exception e) {
                CategoryRepository.update(category.name, CategoryUpdateState.FAILED, CategoryUpdateType.JOB);
                LOGGER.error(e.getMessage(), e);
            }
        }

    }
    private static List<News> convert(String rss) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        LOGGER.debug("------- Starting Rss Transformation...");
        InputSource source = new InputSource(new StringReader(rss));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        LOGGER.debug("------- Starting Rss Parse...");
        Document document = db.parse(source);

        LOGGER.debug("------- Finish Rss Parse");
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

        List<News> newses = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            News news = new News();

            Node item = nodeList.item(i);

            Node titleNode = (Node)expTitle.evaluate(item, XPathConstants.NODE);
            String title = titleNode.getFirstChild().getNodeValue();
            news.title = title;

            Node pubDateNode = (Node)expPubDate.evaluate(item, XPathConstants.NODE);
            String pubDate = pubDateNode.getFirstChild().getNodeValue();
            news.pubDate = pubDate;


            LocalDateTime parse = parse(pubDate);
            news.isoDate = formatt(parse);

            NodeList categories = (NodeList)expCategory.evaluate(item, XPathConstants.NODESET);
            Set<String> categorySet = new HashSet<>();
            for (int x=0; x < categories.getLength(); x++) {
                Node item1 = categories.item(x);
                String category = item1.getFirstChild().getNodeValue();
                categorySet.add(category);
            }

            news.categories = categorySet;

            Node guidNode = (Node)expGuid.evaluate(item, XPathConstants.NODE);
            String guid = guidNode.getFirstChild().getNodeValue();
            news.guid = getIdFromUrl(guid);

            Node descriptionNode = (Node)expDescription.evaluate(item, XPathConstants.NODE);
            String description = descriptionNode.getFirstChild().getNodeValue();
            news.description = description;

            Node contetnNode = (Node)expContent.evaluate(item, XPathConstants.NODE);
            String content = contetnNode.getFirstChild().getNodeValue();
            news.content = content;

            newses.add(news);
        }

        LOGGER.debug("------- Finish Rss Transformation");
        return newses;
    }

    private static String getIdFromUrl(String url){
        return url.split("=")[1];
    }

    private static LocalDateTime parse(String dateStr){
        return LocalDateTime.parse(dateStr, DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    private static String formatt(LocalDateTime dateTime){
        return DateTimeFormatter.ISO_DATE_TIME.format(dateTime);
    }
}
