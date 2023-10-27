/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier.database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

/** The repository class provides a clean API for data access */
public class PlantsRepository {

    private final PlantsDao dao;
    private final LiveData<List<PlantEntity>> allPlants;

    public PlantsRepository(Application application){
        PlantsDatabase database = PlantsDatabase.getDatabase(application);
        dao = database.Dao();
        allPlants = dao.getPlantsList();
    }

    public void insert(PlantEntity plant){
        PlantsDatabase.databaseWriteExecutor.execute(() ->{
            dao.insert(plant);
        });
    }
    public void update(PlantEntity plant) {
        PlantsDatabase.databaseWriteExecutor.execute(() ->{
            dao.update(plant);
        });
    }
    public void delete(PlantEntity plant) {
        PlantsDatabase.databaseWriteExecutor.execute(()->{
            dao.delete(plant);
        });
    }
    // below method is to read all the courses.
    LiveData<List<PlantEntity>> getAllPlants(){
        return allPlants;
    }
}

