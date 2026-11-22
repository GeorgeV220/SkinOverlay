package com.georgev22.skinoverlay.storage.manager.mongo;

import com.georgev22.skinoverlay.storage.ManagedEntity;
import com.georgev22.skinoverlay.storage.data.Entity;
import com.georgev22.skinoverlay.storage.manager.AbstractEntityManager;
import com.georgev22.skinoverlay.utilities.GsonUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class MongoEntityManager<E extends Entity> extends AbstractEntityManager<E> {

    private MongoCollection<Document> collection;

    public MongoEntityManager(ManagedEntity<E> managedEntity, @NonNull MongoDatabase database) {
        super(managedEntity);
        String collectionName = managedEntity.key();
        try {
            this.collection = database.getCollection(collectionName);
        } catch (Throwable throwable) {
            database.createCollection(collectionName);
            this.collection = database.getCollection(collectionName);
        }
        this.collection.createIndex(new Document("_id", 1));
    }

    @Override
    protected void delete0(@NonNull E entity) {
        executorService.execute(() -> collection.deleteOne(Filters.eq("_id", entity.getUniqueId().toString())));
    }

    @Override
    public Optional<E> load0(@NonNull String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc == null) return Optional.empty();

        try {
            E entity = GsonUtils.fromJson(doc.toJson(), getEntityClass());
            if (entity != null) {
                loadedEntities.append(id, entity);
            }
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Error loading " + this.getManagedEntity().key() + " " + id + " from MongoDB", e);
            return Optional.empty();
        }
    }

    @Override
    public void loadAll() {
        List<String> successList = Collections.synchronizedList(new ArrayList<>());
        List<String> failedList = Collections.synchronizedList(new ArrayList<>());
        long startTime = System.nanoTime();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                try {
                    E entity = GsonUtils.fromJson(doc.toJson(), getEntityClass());
                    if (entity != null) {
                        loadedEntities.append(entity.getUniqueId().toString(), entity);
                        successList.add(entity.getUniqueId().toString());
                    } else {
                        failedList.add(doc.get("_id").toString());
                    }
                } catch (Exception e) {
                    main.getLogger().log(Level.SEVERE, "Error loading document: " + doc.toJson(), e);
                }
            }
        }
        logLoadAll(successList, failedList, startTime);
    }

    @Override
    public boolean exists(@NonNull String id) {
        return loadedEntities.containsKey(id);
    }

    @Override
    protected boolean save0(@NonNull E entity) {
        try {
            String json = GsonUtils.toJson(entity, false);
            Document doc = Document.parse(json);
            doc.put("_id", entity.getUniqueId().toString());

            collection.replaceOne(
                    Filters.eq("_id", entity.getUniqueId().toString()),
                    doc,
                    new ReplaceOptions().upsert(true)
            );
            return true;
        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Error saving " +
                    this.getManagedEntity().key() + " data to MongoDB for " + entity.getUniqueId(), e);
            return false;
        }
    }
}
