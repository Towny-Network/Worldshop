package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.DataManagement.Adapters.Adapter;
import dev.onebiteaidan.worldshop.Utils.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteRepository<K, V> implements Repository<K, V> {

    private final Adapter<V> adapter;
    private Connection database;

    public SQLiteRepository(Adapter<V> adapter, File filePath, String tableInitCommand) {
        this.adapter = adapter;

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

    }

    @Override
    public List<V> findAll() {
        return List.of();
    }

    @Override
    public void delete(K key) {

    }

    private void initializeTable() throws SQLException {
        database.execute("CREATE TABLE IF NOT EXISTS TRADES" +
                        "(" +
                        "TRADE_ID UNIQUE AUTO_INCREMENT," +
                        "SELLER_UUID varchar(36)," +
                        "BUYER_UUID varchar(36)," +
                        "ITEM_OFFERED BLOB," +
                        "ITEM_REQUESTED BLOB," +
                        "TRADE_STATUS int," +
                        "LISTING_TIMESTAMP BIGINT," +
                        "COMPLETION_TIMESTAMP BIGINT" +
                        ");"
                , null);
    }

    private void initializePickupsTable() throws SQLException {
        this.execute("CREATE TABLE IF NOT EXISTS PICKUPS" +
                        "(" +
                        "PLAYER_UUID varchar(36)," +
                        "PICKUP_ITEM BLOB," +
                        "COLLECTED boolean," +
                        "TIME_COLLECTED BIGINT" +
                        ");"
                , null);
    }

    private void initializePlayersTable() throws SQLException {
        this.execute("CREATE TABLE IF NOT EXISTS PLAYERS" +
                        "(" +
                        "PLAYER_UUID varchar(36)," +
                        "PURCHASES int," +
                        "SALES int" +
                        ");"
                , null);
    }

    private String generateTableInit(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Storable.class)) {
            Storable annotation = clazz.getAnnotation(Storable.class);

            StringBuilder sb = new StringBuilder();

            sb.append("CREATE IF NOT EXISTS ");
            sb.append(annotation.collectionName());
            sb.append(" (");

            // Get all declared fields
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
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
                            sb.append(" INTEGER,");
                            break;

                        case "OfflinePlayer":
                            sb.append(" VARCHAR(36),");
                            break;

                        case "ItemStack":
                            sb.append("BLOB,");
                            break;

                        case "long":
                            sb.append("BIGINT,");
                            break;

                        case "boolean":
                            sb.append("BOOLEAN,");
                            break;
                    }
                }
            }

            // Convert all strings to standard format
            // UPPERCASE_WITH_UNDERSCORES
            List<String> modifiedFieldNames = new ArrayList<>();

            for (String string : fieldNames) {
                // Using regex to add underscores before uppercase letters, except at the start
                String result = string.replaceAll("([a-z])([A-Z])", "$1_$2");

                // Convert the entire string to uppercase
                modifiedFieldNames.add(result.toUpperCase());
            }


            // Build the command









        }
    }
}
