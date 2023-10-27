/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.demofragments.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


/** This class creates a Photos Entity that will be stored in the database table "photos" */
@Entity(tableName = "photos")
public class PhotosEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public byte[] image;

    public PhotosEntity( byte[] image) {
        this.image = image;
    }
}
