package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL-type database driver interface.
 * All databases run using the JDBC database driver.
 */
public interface Database {

   void connect() throws SQLException;
   void disconnect() throws SQLException;
   boolean isConnected();

   Connection getConnection();


   ResultSet executeQuery(PreparedStatement ps) throws SQLException;
   int executeUpdate(PreparedStatement ps) throws SQLException;
   boolean execute(PreparedStatement ps) throws SQLException;

}
