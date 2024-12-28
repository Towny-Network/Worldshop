package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.PickupRepository;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;

import java.util.List;

public class SQLitePickupRepository implements PickupRepository {
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
//    private void initializePickupsTable() throws SQLException {
//        this.execute("CREATE TABLE IF NOT EXISTS PICKUPS" +
//                        "(" +
//                        "PLAYER_UUID varchar(36)," +
//                        "PICKUP_ITEM BLOB," +
//                        "COLLECTED boolean," +
//                        "TIME_COLLECTED BIGINT" +
//                        ");"
//                , null);
//    }
}
