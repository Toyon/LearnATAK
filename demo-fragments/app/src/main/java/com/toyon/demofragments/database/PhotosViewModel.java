/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.demofragments.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * This ViewModel class helps provide data to the UI and acts as a messenger
 * between the Repository and the UI.
 */
public class PhotosViewModel extends AndroidViewModel {
    private final PhotosRepository repository;
    private final LiveData<List<PhotosEntity>> allPhotos;

    public PhotosViewModel(@NonNull Application application) {
        super(application);
        repository = new PhotosRepository(application);
        allPhotos = repository.getAllPhotos();
    }
    public LiveData<List<PhotosEntity>> getAllPhotos() { return allPhotos; }


    public void insert(PhotosEntity photosEntity) {
        repository.insert(photosEntity);
    }

    public void update(PhotosEntity photosEntity) { repository.update(photosEntity);
    }

    public void delete(PhotosEntity photosEntity) {
        repository.delete(photosEntity);
    }

}
