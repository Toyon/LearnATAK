/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.foodclassifier.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/** This interface defines the actions and queries we can perform on the database */
@Dao
public interface PictureReviewDao {

    @Insert
    void insert(PictureReview pictureReview);

    @Update
    void update(PictureReview pr);

    @Delete
    void delete(PictureReview pr);

    @Query("SELECT * FROM picture_reviews")
    LiveData<List<PictureReview>> getAllPictureReviews();

    @Query("SELECT COUNT(1) FROM picture_reviews WHERE uid LIKE :mapUid ")
    int recordExists(String mapUid);

}
