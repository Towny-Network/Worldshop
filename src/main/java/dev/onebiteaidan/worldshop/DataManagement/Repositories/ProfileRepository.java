package dev.onebiteaidan.worldshop.DataManagement.Repositories;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Profile;

import java.util.List;
import java.util.UUID;

public interface ProfileRepository {
    Profile findById(UUID uuid);
    List<Profile> findAll();
    void save(Profile profile);
    void delete(UUID uuid);
}
