package dev.onebiteaidan.worldshop.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Pickup;

import java.util.List;

public interface PickupRepository {

    Pickup findById(int id);
    List<Pickup> findAll();
    void save(Pickup pickup);
    void delete(int id);

}
