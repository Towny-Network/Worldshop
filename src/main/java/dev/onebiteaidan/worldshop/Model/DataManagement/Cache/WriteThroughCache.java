package dev.onebiteaidan.worldshop.Model.DataManagement.Cache;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class WriteThroughCache<K, V> {

    //todo: Add a logger implementation. Errors should not go to stdout.
    //todo: Look into the usage of generate*Command() and generate*Parameters(). They may not be necessary.

    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    protected final Database database;

    public WriteThroughCache(Database database) {
        this.database = database;
    }

    // Add or update an item in the cache
    public void put(K key, V value) {
        cache.put(key, value);
        // Schedule database update asynchronously
        updateDatabase(key, value);
    }

    // Retrieve an item from the cache
    public V get(K key) {
        return cache.get(key);
    }

    // Remove an item from the cache
    public void remove(K key) {
        cache.remove(key);
        // Schedule database removal asynchronously
        removeFromDatabase(key);
    }

    // Method to retrieve all objects from the cache
    public Collection<V> getAll() {
        return cache.values();
    }

    // Update the database asynchronously
    private void updateDatabase(K key, V value) {
        new Thread(() -> {
            String command = generateUpdateCommand(key, value);
            Object[] parameters = generateUpdateParameters(key, value);
            try {
                database.executeUpdate(command, parameters);
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exceptions appropriately
            }
        }).start();
    }

    // Remove from the database asynchronously
    private void removeFromDatabase(K key) {
        new Thread(() -> {
            String command = generateDeleteCommand(key);
            Object[] parameters = generateDeleteParameters(key);
            try {
                database.executeUpdate(command, parameters);
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exceptions appropriately
            }
        }).start();
    }

    // Abstract methods to be implemented based on entity type
    protected String generateUpdateCommand(K key, V value) {
        throw new UnsupportedOperationException("Must be implemented by subclass.");
    }

    protected Object[] generateUpdateParameters(K key, V value) {
        throw new UnsupportedOperationException("Must be implemented by subclass.");
    }

    protected String generateDeleteCommand(K key) {
        throw new UnsupportedOperationException("Must be implemented by subclass.");
    }

    protected Object[] generateDeleteParameters(K key) {
        throw new UnsupportedOperationException("Must be implemented by subclass.");
    }

    protected void init() {
        throw new UnsupportedOperationException("Must be implemented by subclass.");
    }
}
