package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.SQLite;

import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.ProfileRepository;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Profile;

import java.util.List;
import java.util.UUID;

public class SQLiteProfileRepository implements ProfileRepository {
    @Override
    public Profile findById(UUID uuid) {
        return null;
    }

    @Override
    public List<Profile> findAll() {
        return List.of();
    }

    @Override
    public void save(Profile profile) {

    }

    @Override
    public void delete(UUID uuid) {

    }
    //    private void initializePlayersTable() throws SQLException {
//        this.execute("CREATE TABLE IF NOT EXISTS PLAYERS" +
//                        "(" +
//                        "PLAYER_UUID varchar(36)," +
//                        "PURCHASES int," +
//                        "SALES int" +
//                        ");"
//                , null);
//    }
}
