package com.georgev22.skinoverlay.storage.manager.sql;

import com.georgev22.skinoverlay.storage.ManagedEntity;
import com.georgev22.skinoverlay.storage.data.Entity;
import com.georgev22.skinoverlay.storage.manager.AbstractEntityManager;
import com.georgev22.skinoverlay.utilities.GsonUtils;
import com.georgev22.skinoverlay.utilities.config.OptionsUtil;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLEntityManager<E extends Entity> extends AbstractEntityManager<E> {
    private final DataSource dataSource;

    public SQLEntityManager(ManagedEntity<E> managedEntity, @NonNull DataSource dataSource) {
        super(managedEntity);
        this.dataSource = dataSource;
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS %1$s_data (
                            %1$s_uuid CHAR(36) PRIMARY KEY,
                            data TEXT NOT NULL
                        )
                    """.formatted(this.getManagedEntity().key()));
        } catch (SQLException e) {
            main.getLogger().log(Level.SEVERE, "Failed to create %s_data table".formatted(this.getManagedEntity().key()), e);
        }
    }

    @Override
    public void delete0(@NonNull E entity) {
        executorService.execute(() -> {
            UUID id = entity.getUniqueId();
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "DELETE FROM %1$s_data WHERE %1$s_uuid = ?"
                                 .formatted(getManagedEntity().key()))) {

                stmt.setString(1, id.toString());
                stmt.executeUpdate();

            } catch (SQLException e) {
                main.getLogger().log(Level.SEVERE,
                        "Failed to delete " + getManagedEntity().key() + " data for " + id, e);
            }
        });
    }

    @Override
    protected Optional<E> load0(@NonNull String id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT data FROM %1$s_data WHERE %1$s_uuid = ?".formatted(this.getManagedEntity().key()))) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String json = rs.getString("data");
                E entity = GsonUtils.fromJson(json, this.getEntityClass());
                if (entity == null) {
                    this.main.getLogger().log(Level.SEVERE, "Failed to load " + this.getManagedEntity().key() + " " + id);
                    return Optional.empty();
                }
                loadedEntities.append(id, entity);
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            main.getLogger().log(Level.SEVERE, "Error loading " + this.getManagedEntity().key() + " data for " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void loadAll() {
        List<String> successList = Collections.synchronizedList(new ArrayList<>());
        List<String> failedList = Collections.synchronizedList(new ArrayList<>());
        long startTime = System.nanoTime();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT %1$s_uuid, data FROM %1$s_data".formatted(this.getManagedEntity().key()))) {

            while (rs.next()) {
                String id = rs.getString("%s_uuid".formatted(this.getManagedEntity().key()));
                String json = rs.getString("data");

                E entity = GsonUtils.fromJson(json, this.getEntityClass());
                if (entity != null) {
                    loadedEntities.put(id, entity);
                    successList.add(id);
                    if (OptionsUtil.DEBUG.getBooleanValue()) {
                        main.getLogger().info("Loaded entity " + entity.getUniqueId());
                    }
                } else {
                    failedList.add(id);
                }
            }
        } catch (SQLException e) {
            main.getLogger().log(Level.SEVERE, "Failed to load all " + this.getManagedEntity().key() + " data", e);
        }
        logLoadAll(successList, failedList, startTime);
    }

    @Override
    public boolean exists(@NonNull String id) {
        if (loadedEntities.containsKey(id)) {
            return true;
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM %1$s_data WHERE %1$s_uuid = ?".formatted(this.getManagedEntity().key()))) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            main.getLogger().log(Level.SEVERE, "Error checking existence for " + this.getManagedEntity().key() + " " + id, e);
            return false;
        }
    }

    @Override
    protected boolean save0(@NonNull E entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "REPLACE INTO %1$s_data (%1$s_uuid, data) VALUES (?, ?)"
                             .formatted(getManagedEntity().key()))) {

            stmt.setString(1, entity.getUniqueId().toString());
            stmt.setString(2, GsonUtils.toJson(entity, false));
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            main.getLogger().log(Level.SEVERE,
                    "Error saving " + getManagedEntity().key() + " data for " + entity.getUniqueId(), e);
            return false;
        }
    }
}
