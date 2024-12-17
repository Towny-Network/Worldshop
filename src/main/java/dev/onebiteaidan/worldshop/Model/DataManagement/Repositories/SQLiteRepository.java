package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.DataManagement.Adapters.Adapter;
import dev.onebiteaidan.worldshop.Utils.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteRepository<K, V> implements Repository<K, V> {

    private final Adapter<V> adapter;
    private Connection database;
    private final String tableName;
    private Field[] fields;

    public SQLiteRepository(Adapter<V> adapter, File filePath, Class<V> clazz) {
        this.adapter = adapter;

        // Ensure the class passed in implements storeable
        if (clazz.isAnnotationPresent(Storable.class)) {
            // Populate database information from the clazz
            Storable annotation = clazz.getAnnotation(Storable.class);
            this.tableName = annotation.collectionName();
            this.fields = clazz.getDeclaredFields();
        } else {
            throw new RuntimeException(clazz.getName() + " does not implement the @storable interface!");
        }

        // Attempt to connect to the SQLite database via the JDBC driver
        try {
            String connectionString = "jdbc:sqlite:" + filePath;
            this.database = DriverManager.getConnection(connectionString);
            Logger.info("Successfully connected to the SQLite Database");

        } catch (SQLException e) {
            Logger.severe("Failed to connect to the SQLite Database");
            Logger.logStacktrace(e);
        }

        // Initialize database table
        try {
            String tableInitCommand = generateTableInit(clazz);
            PreparedStatement ps = database.prepareStatement(tableInitCommand);
            ps.execute();
        } catch (SQLException e) {
            Logger.severe("SQL Exception when initializing the table");
            Logger.logStacktrace(e);
        }
    }

    @Override
    public void save(K id, V value) {
        adapter.serialize(value);
    }

    @Override
    public V find(K id) {
        String query = "SELECT * FROM " + tableName + " WHERE  id = ?";
        try (PreparedStatement ps = database.prepareStatement(query)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            Map<String, Object> map = new HashMap<>();

            for (Field field : fields) {
                if (rs.next()) {
                    switch(field.getType().getName()) {
                        case "int":
                        case "TradeStatus":
                            map.put(field.getName(), rs.getInt(field.getName()));
                            break;

                        case "OfflinePlayer":
                            map.put(field.getName(), rs.getString(field.getName()));
                            break;

                        case "ItemStack":
                            map.put(field.getName(), rs.getBlob(field.getName()));
                            break;

                        case "long":
                            map.put(field.getName(), rs.getLong(field.getName()));
                            break;

                        case "boolean":
                            map.put(field.getName(), rs.getBoolean(field.getName()));
                            break;
                        default:
                            throw new UnsupportedOperationException("Type " + field.getType().getName() + " is not supported by SQLiteRepository!");
                    }
                }
            }

            return adapter.deserialize(map);

        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }
        return null;
    }

    @Override
    public List<V> findAll() {
        String query = "SELECT * FROM " + tableName;
        List<V> result = new ArrayList<>();
        try (PreparedStatement ps = database.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            Map<String, Object> map = new HashMap<>();

            while (rs.next()) {
                for (Field field : fields) {
                    if (rs.next()) {
                        switch(field.getType().getName()) {
                            case "int":
                            case "TradeStatus":
                                map.put(field.getName(), rs.getInt(field.getName()));
                                break;

                            case "OfflinePlayer":
                                map.put(field.getName(), rs.getString(field.getName()));
                                break;

                            case "ItemStack":
                                map.put(field.getName(), rs.getBlob(field.getName()));
                                break;

                            case "long":
                                map.put(field.getName(), rs.getLong(field.getName()));
                                break;

                            case "boolean":
                                map.put(field.getName(), rs.getBoolean(field.getName()));
                                break;
                            default:
                                throw new UnsupportedOperationException("Type " + field.getType().getName() + " is not supported by SQLiteRepository!");
                        }
                    }
                }
                result.add(adapter.deserialize(map));
            }

        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }
        return result;
    }

    @Override
    public void delete(K key) {

    }

//    private void initializeTable() throws SQLException {
//        database.execute("CREATE TABLE IF NOT EXISTS TRADES" +
//                        "(" +
//                        "TRADE_ID UNIQUE AUTO_INCREMENT," +
//                        "SELLER_UUID varchar(36)," +
//                        "BUYER_UUID varchar(36)," +
//                        "ITEM_OFFERED BLOB," +
//                        "ITEM_REQUESTED BLOB," +
//                        "TRADE_STATUS int," +
//                        "LISTING_TIMESTAMP BIGINT," +
//                        "COMPLETION_TIMESTAMP BIGINT" +
//                        ");"
//                , null);
//    }
//
//    private void initializePickupsTable() throws SQLException {
//        this.execute("CREATE TABLE IF NOT EXISTS PICKUPS" +
//                        "(" +
//                        "PLAYER_UUID varchar(36)," +
//                        "PICKUP_ITEM BLOB," +
//                        "COLLECTED boolean," +
//                        "TIME_COLLECTED BIGINT" +
//                        ");"
//                , null);
//    }
//
//    private void initializePlayersTable() throws SQLException {
//        this.execute("CREATE TABLE IF NOT EXISTS PLAYERS" +
//                        "(" +
//                        "PLAYER_UUID varchar(36)," +
//                        "PURCHASES int," +
//                        "SALES int" +
//                        ");"
//                , null);
//    }

    private String generateTableInit(Class<V> clazz) {
        if (clazz.isAnnotationPresent(Storable.class)) {
            Storable annotation = clazz.getAnnotation(Storable.class);

            StringBuilder sb = new StringBuilder();

            sb.append("CREATE IF NOT EXISTS ");
            sb.append(annotation.collectionName());
            sb.append(" (");

            // Get all declared fields
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                // Only include instance variables (non-static)
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    // Turn field name into UPPERCASE_WITH_UNDERSCORES
                    String modifiedString = field.getName().replaceAll("([a-z])([A-Z])", "$1_$2");
                    sb.append(modifiedString.toUpperCase());
                    sb.append(" ");

                    // Append the field's corresponding type
                    switch(field.getType().getName()) {
                        case "int":
                        case "TradeStatus":
                            sb.append(" INTEGER");
                            break;

                        case "OfflinePlayer":
                            sb.append(" VARCHAR(36)");
                            break;

                        case "ItemStack":
                            sb.append("BLOB");
                            break;

                        case "long":
                            sb.append("BIGINT");
                            break;

                        case "boolean":
                            sb.append("BOOLEAN");
                            break;
                        default:
                            throw new UnsupportedOperationException("Type " + field.getType().getName() + " is not supported by SQLiteRepository!");
                    }

                    // If not last field, append a comma
                    if (i < fields.length - 1) {
                        sb.append(", ");
                    }
                }
            }

            sb.append(");");

            // Build the command
            return sb.toString();
        }
        return null;
    }


}
