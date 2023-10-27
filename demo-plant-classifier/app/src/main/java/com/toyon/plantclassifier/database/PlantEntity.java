/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


/** This class creates a Plant Entity that will be stored in the Plants database */
@Entity(tableName = "plants")
public class PlantEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String plantType;
    public String commonName;
    public String edible;
    public String certainty;
    public byte[] image;
    public String location;
    public String locationName;
    public String notes;
    public String uid;

    public PlantEntity(String plantType, String commonName, byte[] image, String certainty,
                       String edible, String notes, String location, String locationName, String uid) {
        this.plantType = plantType;
        this.image = image;
        this.certainty = certainty;
        this.commonName = commonName;
        this.edible = edible;
        this.notes = notes;
        this.location = location;
        this.locationName = locationName;
        this.uid = uid;
    }
}
