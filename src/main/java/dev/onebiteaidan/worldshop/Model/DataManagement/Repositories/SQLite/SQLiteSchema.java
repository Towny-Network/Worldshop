package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

public class SQLiteSchema {

    public static class SQLiteTradeSchema {
        public static String TRADES_TABLE = "TRADES";

        public enum Column {
            TRADE_ID("TRADE_ID", "INTEGER"),
            SELLER_UUID("SELLER_UUID", "VARCHAR(36)"),
            BUYER_UUID("BUYER_UUID", "VARCHAR(36)"),
            ITEM_OFFERED("ITEM_OFFERED", "BLOB"),
            ITEM_REQUESTED("ITEM_REQUESTED", "BLOB"),
            TRADE_STATUS("TRADE_STATUS", "INT"),
            LISTING_TIMESTAMP("LISTING_TIMESTAMP", "BIGINT"),
            COMPLETION_TIMESTAMP("COMPLETION_TIMESTAMP", "BIGINT");

            private final String columnName;
            private final String columnType;

            Column(String columnName, String columnType) {
                this.columnName = columnName;
                this.columnType = columnType;
            }

            public String getColumnName() {
                return columnName;
            }

            public String getColumnType() {
                return columnType;
            }

            @Override
            public String toString() {
                return columnName;
            }
        }

        public static String TRADES_INIT_COMMAND = "CREATE TABLE IF NOT EXISTS " + TRADES_TABLE +
                "(" +
                Column.TRADE_ID.getColumnName() + " " + Column.TRADE_ID.getColumnType() + " PRIMARY KEY AUTOINCREMENT," +
                Column.SELLER_UUID.getColumnName() + " " + Column.SELLER_UUID.getColumnType() + "," +
                Column.BUYER_UUID.getColumnName() + " " + Column.BUYER_UUID.getColumnType() + "," +
                Column.ITEM_OFFERED.getColumnName() + " " + Column.ITEM_OFFERED.getColumnType() + "," +
                Column.ITEM_REQUESTED.getColumnName() + " " + Column.ITEM_REQUESTED.getColumnType() + "," +
                Column.TRADE_STATUS.getColumnName() + " " + Column.TRADE_STATUS.getColumnType() + "," +
                Column.LISTING_TIMESTAMP.getColumnName() + " " + Column.LISTING_TIMESTAMP.getColumnType() + "," +
                Column.COMPLETION_TIMESTAMP.getColumnName() + " " + Column.COMPLETION_TIMESTAMP.getColumnType() +
                ");";
    }

    public static class SQLiteProfileSchema {
        public static String PROFILES_TABLE = "PLAYERS";

        public enum Column {
            PLAYER_UUID("PLAYER_UUID", "VARCHAR(36)"),
            PURCHASES("PURCHASES", "INT"),
            SALES("SALES", "INT");

            private final String columnName;
            private final String columnType;

            Column(String columnName, String columnType) {
                this.columnName = columnName;
                this.columnType = columnType;
            }

            public String getColumnName() {
                return columnName;
            }

            public String getColumnType() {
                return columnType;
            }

            @Override
            public String toString() {
                return columnName;
            }
        }

        public static String PROFILES_INIT_COMMAND = "CREATE TABLE IF NOT EXISTS " + PROFILES_TABLE +
                "(" +
                Column.PLAYER_UUID.getColumnName() + " " + Column.PLAYER_UUID.getColumnType() + " PRIMARY KEY," +
                Column.PURCHASES.getColumnName() + " " + Column.PURCHASES.getColumnType() + "," +
                Column.SALES.getColumnName() + " " + Column.SALES.getColumnType() +
                ");";
    }

    public static class SQLitePickupSchema {
        public static String PICKUPS_TABLE = "PICKUPS";

        public enum Column {
            PICKUP_ID("PICKUP_ID", "INT"),
            PLAYER_UUID("PLAYER_UUID", "VARCHAR(36)"),
            PICKUP_ITEM("PICKUP_ITEM", "BLOB"),
            TRADE_ID("TRADE_ID", "INT"),
            COLLECTED("COLLECTED", "BOOLEAN"),
            COLLECTION_TIMESTAMP("COLLECTION_TIMESTAMP", "BIGINT");

            private final String columnName;
            private final String columnType;

            Column(String columnName, String columnType) {
                this.columnName = columnName;
                this.columnType = columnType;
            }

            public String getColumnName() {
                return columnName;
            }

            public String getColumnType() {
                return columnType;
            }

            @Override
            public String toString() {
                return columnName;
            }
        }

        public static String PICKUPS_INIT_COMMAND = "CREATE TABLE IF NOT EXISTS " + PICKUPS_TABLE +
                "(" +
                Column.PICKUP_ID.getColumnName() + " " + Column.PICKUP_ID.getColumnType() + " PRIMARY KEY," +
                Column.PLAYER_UUID.getColumnName() + " " + Column.PLAYER_UUID.getColumnType() + "," +
                Column.PICKUP_ITEM.getColumnName() + " " + Column.PICKUP_ITEM.getColumnType() + ", " +
                Column.TRADE_ID.getColumnName() + " " + Column.TRADE_ID.getColumnType() + ", " +
                Column.COLLECTED.getColumnName() + " " + Column.COLLECTED.getColumnType() + ", " +
                Column.COLLECTION_TIMESTAMP.getColumnName() + " " + Column.COLLECTION_TIMESTAMP.getColumnType() +
                ");";
    }
}
