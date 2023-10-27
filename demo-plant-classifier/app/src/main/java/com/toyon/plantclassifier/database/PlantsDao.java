/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


/** This interface defines the actions and queries we can perform on the database */
@Dao
public interface PlantsDao {

    @Insert
    void insert(PlantEntity plant);
    @Update
    void update(PlantEntity plant);
    @Delete
    void delete(PlantEntity plantEntity);
    @Query("SELECT * FROM plants")
    LiveData<List<PlantEntity>> getPlantsList();
}
