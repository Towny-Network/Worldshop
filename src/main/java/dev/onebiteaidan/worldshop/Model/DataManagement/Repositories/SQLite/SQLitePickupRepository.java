package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.PickupRepository;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;

import java.sql.Connection;
import java.util.List;

public class SQLitePickupRepository implements PickupRepository {
    public SQLitePickupRepository(Connection connection) {

    }

    @Override
    public Pickup findById(int id) {
        return null;
    }

    @Override
    public List<Pickup> findAll() {
        return List.of();
    }

    @Override
    public void save(Pickup pickup) {

    }

    @Override
    public void delete(int id) {

    }
}
