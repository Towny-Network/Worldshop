package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteTradeSchema.Column.*;
import static dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite.SQLiteSchema.SQLiteProfileSchema.Column.*;

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
                TRADE_ID.getColumnName() + " " + TRADE_ID.getColumnType() + " PRIMARY KEY AUTOINCREMENT," +
                SELLER_UUID.getColumnName() + " " + SELLER_UUID.getColumnType() + "," +
                BUYER_UUID.getColumnName() + " " + BUYER_UUID.getColumnType() + "," +
                ITEM_OFFERED.getColumnName() + " " + ITEM_OFFERED.getColumnType() + "," +
                ITEM_REQUESTED.getColumnName() + " " + ITEM_REQUESTED.getColumnType() + "," +
                TRADE_STATUS.getColumnName() + " " + TRADE_STATUS.getColumnType() + "," +
                LISTING_TIMESTAMP.getColumnName() + " " + LISTING_TIMESTAMP.getColumnType() + "," +
                COMPLETION_TIMESTAMP.getColumnName() + " " + COMPLETION_TIMESTAMP.getColumnType() +
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
                PLAYER_UUID.getColumnName() + " " + PLAYER_UUID.getColumnType() + "," +
                PURCHASES.getColumnName() + " " + PURCHASES.getColumnType() + "," +
                SALES.getColumnName() + " " + SALES.getColumnType() +
                ");";
    }

    public static class SQLitePickupSchema {
        public static String PICKUPS_TABLE = "PICKUPS";

        public enum Column {
            PLAYER_UUID("PLAYER_UUID", "VARCHAR(36)"),
            PICKUP_ITEM("PICKUP_ITEM", "BLOB"),
            COLLECTED("COLLECTED", "BOOLEAN"),
            TIME_COLLECTED("TIME_COLLECTED", "BIGINT");

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

        public static String PICKUPS_INIT_COMMAND = "";
    }
}
