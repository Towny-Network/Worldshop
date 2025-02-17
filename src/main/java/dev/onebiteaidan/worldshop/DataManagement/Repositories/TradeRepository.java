package dev.onebiteaidan.worldshop.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;

import java.util.List;

public interface TradeRepository {
    Trade findById(int id);
    List<Trade> findAll();
    void save(Trade trade);
    void delete(int id);
}
