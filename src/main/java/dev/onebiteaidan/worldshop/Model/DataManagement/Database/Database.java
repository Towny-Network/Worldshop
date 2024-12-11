package dev.onebiteaidan.worldshop.Model.DataManagement.Database;

/**
 * Database driver interface.
 * All databases are Type agnostic.
 * This interface is designed to be used for key,value relationships without much else.
 */
public interface Database {

   void connect();
   void disconnect();
   boolean isConnected();


}
