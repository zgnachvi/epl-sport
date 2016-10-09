package com.goodbarber.premierleaguene.repository;

import com.goodbarber.premierleaguene.domain.Category;
import com.goodbarber.premierleaguene.domain.CategoryUpdateState;
import com.goodbarber.premierleaguene.domain.CategoryUpdateType;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class CategoryRepository  extends ConnectionManager implements CollectionName {

    @SuppressWarnings("unused")
    public static final Logger LOGGER = LoggerFactory.getLogger(CategoryRepository.class);

    public static List<Category> list(Integer start, Integer limit, boolean withImage) {
        MongoCursor<Document> iterator = database
                .getCollection(CollectionName.categories)
                .find()
                .sort(new BasicDBObject("name", 1))
                .skip(start)
                .limit(limit)
                .iterator();

        List<Category> categories = new ArrayList<>();
        while (iterator.hasNext()) {
            Category category = new Category();

            Document next = iterator.next();
            category.name = next.getString("name");
            category.rssFeed = next.getString("rssFeed");
            category.imageBase64 = withImage?next.getString("imageBase64") : null;
            category.updateTime = next.getString("updateTime");
            category.updateState = next.getString("updateState");
            category.updateType = next.getString("updateType");
            categories.add(category);
        }

        return categories;
    }


    public static Category get(String categoryName) {
        Document document = database
                .getCollection(CollectionName.categories)
                .find(new BasicDBObject("name", categoryName))
                .limit(1)
                .first();

        if (document != null) {
            Category category = new Category();


            category.name = document.getString("name");
            category.rssFeed = document.getString("rssFeed");
            category.imageBase64 = document.getString("imageBase64");
            category.updateTime = document.getString("updateTime");
            category.updateState = document.getString("updateState");
            category.updateType = document.getString("updateType");

            return category;
        }
        return null;

    }

    public static ResponseEntity<Category> add(Category category) {
            if ( ! alreadyInserted(category.name)) {
                MongoCollection<Document> collection = database.getCollection(CollectionName.categories);
                Document doc = new Document("rssFeed", category.rssFeed)
                        .append("name", category.name)
                        .append("imageBase64", category.imageBase64)
                        .append("updateTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .append("updateState", CategoryUpdateState.FAILED.toString())
                        .append("updateType", CategoryUpdateType.MANUAL.toString());
                collection.insertOne(doc);
                return new ResponseEntity(category, HttpStatus.OK);
            }

        return new ResponseEntity(HttpStatus.FOUND);

    }

    public static ResponseEntity<Category> update(String name, Category category) {
        if ( ! alreadyInserted(category.name)) {
            Document doc = new Document("name", category.name);
            if (category.rssFeed != null) {
                doc.append("rssFeed", category.rssFeed);
            }
            if (category.imageBase64 != null){
               doc.append("imageBase64", category.imageBase64);
            }

            database.getCollection(CollectionName.categories).updateOne(new Document("name", name),
                    new Document("$set", doc));

            return new ResponseEntity(category, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.FORBIDDEN);

    }

    public static void update(String name,  CategoryUpdateState state, CategoryUpdateType type) {
        if (alreadyInserted(name)) {
            Document doc = new Document()
                    .append("updateTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .append("updateState", state.toString())
                    .append("updateType", type.toString());


            database.getCollection(CollectionName.categories).updateOne(new Document("name", name),
                    new Document("$set", doc));
        }
    }

    public static ResponseEntity delete(String name) {
        if (alreadyInserted(name)) {
            database.getCollection(CollectionName.categories).deleteOne(new Document("name", name));
            return new ResponseEntity(HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);

    }

    public static Boolean alreadyInserted(String name) {
        Document first = database.getCollection(CollectionName.categories)
                .find(new BasicDBObject("name", name))
                .limit(1)
                .first();
        return  first != null;

    }
}
