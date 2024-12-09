package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.DataManagement.Adapters.Adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryRepository<K, V> implements Repository<K, V> {

    private final Adapter<V> adapter;
    private final Map<K, Map<String, Object>> storage = new HashMap<>();

    public InMemoryRepository(Adapter<V> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void save(K key, V value) {
        Map<String, Object> serializedData = adapter.serialize(value);
        storage.put(key, serializedData);
    }

    @Override
    public V find(K key) {
        Map<String, Object> serializedData = storage.get(key); // Retrieve serialized data
        return serializedData != null ? adapter.deserialize(serializedData) : null;
    }

    @Override
    public List<V> findAll() {
        return storage.values().stream()
                .map(adapter::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(K key) {
        storage.remove(key);
    }
}
