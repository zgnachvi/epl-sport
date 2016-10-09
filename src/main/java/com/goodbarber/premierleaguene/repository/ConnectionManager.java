package com.goodbarber.premierleaguene.repository;

import com.goodbarber.premierleaguene.utils.ProjectConfig;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class ConnectionManager {
    private static MongoClient dataSource;

    protected static MongoDatabase database;

    public static void init() {
        String host = ProjectConfig.get("mongo.host");

        int port = Integer.valueOf((String)ProjectConfig.get("mongo.port"));

        String dbName = ProjectConfig.get("mongo.db.name");

        dataSource = new MongoClient(host, port);

        database = dataSource.getDatabase(dbName);
    }

    public static void close(){
        dataSource.close();
    }
}
