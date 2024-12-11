package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.DataManagement.Adapters.Adapter;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;

import java.util.List;

public class DatabaseRepository<K, V> implements Repository<K, V> {

    public DatabaseRepository(Adapter<V> adapter, Database database) {

    }

    @Override
    public void save(K id, V value) {

    }

    @Override
    public V find(K id) {
        return null;
    }

    @Override
    public List<V> findAll() {
        return List.of();
    }

    @Override
    public void delete(K id) {

    }
}
