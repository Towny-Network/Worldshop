package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Profile;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ProfileRepository extends SQLiteRepository<UUID, Profile> {

    public ProfileRepository(File filePath) {
        super(filePath);
    }

    @Override
    protected void initializeTable() {

    }

    @Override
    public void save(UUID id, Profile value) {

    }

    @Override
    public Profile find(UUID id) {
        return null;
    }

    @Override
    public List<Profile> findAll() {
        return null;
    }
}
