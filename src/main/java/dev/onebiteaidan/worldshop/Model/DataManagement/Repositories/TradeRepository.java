package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;

import java.util.List;

public interface TradeRepository {
    Trade findById(int id);
    List<Trade> findAll();
    void save(Trade trade);
    void delete(int id);
}
