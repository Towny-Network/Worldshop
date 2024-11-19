package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SQL-type database driver interface.
 * All databases run using the JDBC database driver.
 */
public interface Database {

   void connect() throws SQLException;
   void disconnect() throws SQLException;
   boolean isConnected();

   Connection getConnection();


   ResultSet executeQuery(String command, Object[] parameters) throws SQLException;
   int executeUpdate(String command, Object[] parameters) throws SQLException;
   boolean execute(String command, Object[] parameters) throws SQLException;

}
