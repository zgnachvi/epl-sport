package com.goodbarber.premierleaguene.repository;

import com.goodbarber.premierleaguene.domain.News;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NewsRepository extends ConnectionManager implements CollectionName {

    public static final Logger LOGGER = LoggerFactory.getLogger(NewsRepository.class);

    public static List<News> list(Integer start, Integer limit, boolean withContent) {
        MongoCursor<Document> iterator = database
                .getCollection(CollectionName.news)
                .find()
                .sort(new BasicDBObject("pubDate", -1))
                .skip(start)
                .limit(limit)
                .iterator();

        List<News> newses = new ArrayList<>();
        while (iterator.hasNext()) {
            News news = new News();

            Document next = iterator.next();
            news.title = next.getString("title");
            news.pubDate = next.getString("pubDate");
            news.isoDate = next.getString("isoDate");
            news.guid = next.getString("guid");
            news.description = next.getString("description");
            news.content = withContent?next.getString("content") : null;
            news.categories = new HashSet<>();

            ArrayList categories = (ArrayList)next.get("categories");
            news.categories.addAll(categories);

            newses.add(news);
        }

        return newses;
    }


    public static News get(String id) {
        Document document = database
                .getCollection(CollectionName.news)
                .find(new BasicDBObject("guid", id))
                .limit(1)
                .first();

        if (document != null) {
            News news = new News();


            news.title = document.getString("title");
            news.pubDate = document.getString("pubDate");
            news.isoDate = document.getString("isoDate");
            news.guid = document.getString("guid");
            news.description = document.getString("description");
            news.content = document.getString("content");
            news.categories = new HashSet<>();

            ArrayList categories = (ArrayList)document.get("categories");
            news.categories.addAll(categories);

            return news;
        }
        return null;

    }
    public static synchronized void saveRss(List<News> newses) {
        int total = newses.size();
        int inserted = 0;
        int skipped = 0;

        MongoCollection<Document> collection = database.getCollection(CollectionName.news);
        for (News news :newses) {
            if (alreadyInserted(news.guid)) {
                skipped ++;
                continue;
            }
            Document doc = new Document("guid", news.guid)
                    .append("title", news.title)
                    .append("pubDate", news.pubDate)
                    .append("isoDate", news.isoDate)
                    .append("categories", news.categories)
                    .append("description", news.description)
                    .append("content", news.content);
            collection.insertOne(doc);

            inserted ++;
        }

        LOGGER.debug("------- News Total -> " + total);
        LOGGER.debug("------- News Inserted -> " + inserted);
        LOGGER.debug("------- News skipped -> " + skipped);
    }

    public static Boolean alreadyInserted(String guid) {
        Document first = database.getCollection(CollectionName.news)
                .find(new BasicDBObject("guid", guid))
                .projection(new BasicDBObject("_id", 0).append("id", 1)).limit(1).first();

        return  first != null;

    }
}
