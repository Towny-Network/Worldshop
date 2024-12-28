package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;

import java.util.List;

public interface PickupRepository {

    Pickup findById(int id);
    List<Pickup> findAll();
    void save(Pickup pickup);
    void delete(int id);

}
