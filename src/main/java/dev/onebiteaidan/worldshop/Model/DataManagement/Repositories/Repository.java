package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import java.util.List;

public interface Repository<K,V> {

    void save(K key, V value);
    V find(K key);
    List<V> findAll();
    void delete(K key);

}
