/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.demofragments.database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

/** The repository class provides a clean API for data access */
public class PhotosRepository {

    private final PhotosDao dao;
    private final LiveData<List<PhotosEntity>> allPhotos;

    public PhotosRepository(Application application){
        PhotosDatabase database = PhotosDatabase.getDatabase(application);
        dao = database.Dao();
        allPhotos = dao.getPhotos();
    }

    public void insert(PhotosEntity photos){
        PhotosDatabase.databaseWriteExecutor.execute(() ->{
            dao.insert(photos);
        });
    }
    public void update(PhotosEntity photos) {
        PhotosDatabase.databaseWriteExecutor.execute(() ->{
            dao.update(photos);
        });
    }
    public void delete(PhotosEntity photos) {
        PhotosDatabase.databaseWriteExecutor.execute(()->{
            dao.delete(photos);
        });
    }
    // below method is to read all the courses.
    LiveData<List<PhotosEntity>> getAllPhotos(){
        return allPhotos;
    }
}

