package dev.onebiteaidan.worldshop.Model.DataManagement;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {

   void connect() throws SQLException;
   boolean isConnected();
   void disconnect();
   Connection getConnection();
   // Queries need to have the connection passed in because a ResultSet cannot be accessed after the database connection is closed.
   void createTradesTable();
   void createPickupsTable();
   void createPlayersTable();

}
