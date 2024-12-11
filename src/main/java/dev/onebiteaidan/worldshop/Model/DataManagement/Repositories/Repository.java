package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import java.util.List;

public interface Repository<K, V> {

    void save(K id, V value);
    V find(K id);
    List<V> findAll();
    void delete(K id);

}
