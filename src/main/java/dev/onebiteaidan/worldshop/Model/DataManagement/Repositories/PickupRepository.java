package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;

import java.io.File;
import java.util.List;

public class PickupRepository extends TradeRepository<Integer, Pickup> {

    public PickupRepository(File filePath) {
        super(filePath);
    }

    @Override
    protected void initializeTable() {

    }

    @Override
    public void save(Integer id, Pickup value) {

    }

    @Override
    public Pickup find(Integer id) {
        return null;
    }

    @Override
    public List<Pickup> findAll() {
        return null;
    }

    public int getNextTradeID() {
        return -1;
    }
}
