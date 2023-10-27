/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.foodclassifier.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

/** The repository class provides a clean API for access to database records */
public class PictureReviewRepository {
    private final static String TAG = PictureReviewRepository.class.getSimpleName();
    private final PictureReviewDao dao;
    private final LiveData<List<PictureReview>> allPictureReviews;

    public PictureReviewRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application);
        dao = database.Dao();
        allPictureReviews = dao.getAllPictureReviews();
    }

    public void insert(PictureReview pr){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // no record if query is zero count
            if (dao.recordExists(pr.getUid()) == 0)
                dao.insert(pr);
        });
    }

    public void update(PictureReview pr) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.update(pr);
        });
    }

    public void delete(PictureReview pr) {
        Log.d(TAG, "deleting picture review # " + pr.getId());
        AppDatabase.databaseWriteExecutor.execute(()-> {
            dao.delete(pr);
        });
    }

    LiveData<List<PictureReview>> getAllPictureReviews(){
        return allPictureReviews;
    }

}
