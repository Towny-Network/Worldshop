package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {

   enum Tables {
      TRADES,
      PICKUPS,
      PLAYERS
   }

   enum TradeColumns {
      ALL,              // Represents the '*' character
      TRADE_ID,
      SELLER_UUID,
      BUYER_UUID,
      ITEM_OFFERED,
      ITEM_REQUESTED,
      TRADE_STATUS,
      LISTING_TIMESTAMP,
      COMPLETION_TIMESTAMP
   }

   enum PickupColumns {
      ALL,
      PLAYER_UUID,
      TRADE_ID,
      COLLECTED,
      COLLECTION_TIMESTAMP
   }

   enum PlayerColumns {
      ALL,
      PLAYER_UUID,
      PURCHASES,
      SALES
   }

   void connect() throws SQLException;
   boolean isConnected();
   void disconnect();
   Connection getConnection();
   // Queries need to have the connection passed in because a ResultSet cannot be accessed after the database connection is closed.
   void createTradesTable();
   void createPickupsTable();
   void createPlayersTable();

}
