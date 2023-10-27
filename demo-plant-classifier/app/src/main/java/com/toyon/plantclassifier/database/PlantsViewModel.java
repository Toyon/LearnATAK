/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * This ViewModel class helps provide data to the UI and acts as a messenger
 * between the Repository and the UI.
 */
public class PlantsViewModel extends AndroidViewModel {
    private final PlantsRepository repository;
    private final LiveData<List<PlantEntity>> allPlants;

    public PlantsViewModel(@NonNull Application application) {
        super(application);
        repository = new PlantsRepository(application);
        allPlants = repository.getAllPlants();
    }
    public LiveData<List<PlantEntity>> getAllPlants() { return allPlants; }


    public void insert(PlantEntity plantEntity) {
        repository.insert(plantEntity);
    }

    public void update(PlantEntity plantEntity) {
        repository.update(plantEntity);
    }

    public void delete(PlantEntity plantEntity) {
        repository.delete(plantEntity);
    }

}
