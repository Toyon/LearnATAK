/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.foodclassifier.database;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This ViewModel class helps provide data to the UI and acts as a messenger
 * between the API Repository and the UI.
 */
public class PictureReviewViewModel extends AndroidViewModel {
    private final static String TAG = PictureReviewViewModel.class.getSimpleName();
    public final PictureReviewRepository database;
    public final LiveData<List<PictureReview>> allPR;

    public PictureReviewViewModel(@NonNull Application application) {
        super(application);
        database = new PictureReviewRepository(application);
        allPR = database.getAllPictureReviews();
    }

    public void deleteByUid(String uid) {
        for (int i = 0; i < Objects.requireNonNull(allPR.getValue()).size(); i++) {
            if (allPR.getValue().get(i).getUid().equals(uid)) {
                this.database.delete(allPR.getValue().get(i));
                Log.d(TAG, "Deleted " + uid);
                return;
            }
        }
        Log.d(TAG, "FAILED TO DELETE Picture Review by UID");
    }

    public List<PictureReview> getCopyReviews() {
        List<PictureReview> reviews = allPR.getValue();
        return (reviews != null) ? reviews : new ArrayList<>();
    }

}

