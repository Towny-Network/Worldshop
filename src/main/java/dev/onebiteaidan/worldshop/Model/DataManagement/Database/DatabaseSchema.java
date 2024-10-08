package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

public final class DatabaseSchema {
    public enum Table {
        TRADES,
        PICKUPS,
        PLAYERS;

        private final String tableName;

        // Constructor for default name
        Table() {
            this.tableName = this.name();  // Use the enum constant name by default
        }

        // Constructor for custom name
        Table(String customName) {
            this.tableName = customName;  // Use custom name if provided
        }

        @Override
        public String toString() {
            return this.tableName;
        }
    }

    public interface Column {

    }

    public enum TradeColumn implements Column {
        ALL("*"),
        TRADE_ID,
        SELLER_UUID,
        BUYER_UUID,
        ITEM_OFFERED,
        ITEM_REQUESTED,
        TRADE_STATUS,
        LISTING_TIMESTAMP,
        COMPLETION_TIMESTAMP;

        private final String columnName;

        // Constructor for default name
        TradeColumn() {
            this.columnName = this.name();  // Use the enum constant name by default
        }

        // Constructor for custom name
        TradeColumn(String customName) {
            this.columnName = customName;  // Use custom name if provided
        }

        @Override
        public String toString() {
            return this.columnName;
        }
    }

    public enum PickupColumn implements Column {
        ALL("*"),
        PLAYER_UUID,
        PICKUP_ITEM,
        TRADE_ID,
        COLLECTED,
        TIME_COLLECTED;

        private final String columnName;

        // Constructor for default name
        PickupColumn() {
            this.columnName = this.name();  // Use the enum constant name by default
        }

        // Constructor for custom name
        PickupColumn(String customName) {
            this.columnName = customName;  // Use custom name if provided
        }

        @Override
        public String toString() {
            return this.columnName;
        }
    }

    public enum PlayerColumn implements Column {
        ALL("*"),
        PLAYER_UUID,
        PURCHASES,
        SALES;

        private final String columnName;

        // Constructor for default name
        PlayerColumn() {
            this.columnName = this.name();  // Use the enum constant name by default
        }

        // Constructor for custom name
        PlayerColumn(String customName) {
            this.columnName = customName;  // Use custom name if provided
        }

        @Override
        public String toString() {
            return this.columnName;
        }
    }
}
