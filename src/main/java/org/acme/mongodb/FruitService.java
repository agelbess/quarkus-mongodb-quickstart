package org.acme.mongodb;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.mongodb.runtime.MongoClientName;
import io.quarkus.runtime.Startup;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Startup
public class FruitService {

    private static final Logger log = LoggerFactory.getLogger(FruitService.class);

    @Inject
    @MongoClientName("fruits")
    MongoClient mongoClient;

    @PostConstruct
    void init() {
        log.info("creating index");
        MongoCollection<Document> collection = mongoClient.getDatabase("fruits").getCollection("fruit");
        collection.createIndex(new Document("t", 1));
        log.info("created index");
    }

    public List<Fruit> list() {
        List<Fruit> list = new ArrayList<>();
        MongoCursor<Document> cursor = getCollection().find().iterator();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Fruit fruit = new Fruit();
                fruit.setName(document.getString("name"));
                fruit.setDescription(document.getString("description"));
                list.add(fruit);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public void add(Fruit fruit) {
        Document document = new Document()
                .append("name", fruit.getName())
                .append("description", fruit.getDescription());
        getCollection().insertOne(document);
    }

    private MongoCollection getCollection() {
        return mongoClient.getDatabase("fruit").getCollection("fruit");
    }
}
