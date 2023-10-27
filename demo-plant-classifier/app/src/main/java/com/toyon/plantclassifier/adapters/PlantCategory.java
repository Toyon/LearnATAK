/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier.adapters;

import com.toyon.plantclassifier.database.PlantEntity;

import java.util.List;
import java.util.Locale;

/**
 * This object serves as a data structure to group a sublist of plant records from the database
 * to a category name. The category name is displayed as a section heading in the main pane.
 */
public class PlantCategory {

    public final static String EDIBLE = "Edible/Medicinal Plants";
    public final static String POISON = "Poisonous Plants";
    public final static String GENERAL = "General Category";

    private final String categoryName;
    private final List<PlantEntity> plantList;

    public PlantCategory(String categoryName, List<PlantEntity> plantList) {
        this.categoryName = categoryName;
        this.plantList = plantList;
    }

    public String getCategoryName() {
        return String.format(Locale.US, "%s (%d)", this.categoryName, plantList.size());
    }

    public List<PlantEntity> getPlantList() {
        return plantList;
    }

    /** Interpret edibility string to determine if plant is edible or medicinal */
    public static boolean isEdible(String str) {
        str = str.toLowerCase(Locale.US);
        return str.contains("edible") && !str.contains("no edible") || str.contains("medicinal");
    }

    /** Interpret edibility string to determine if plant is poisonous */
    public static boolean isPoison(String str) {
        str = str.toLowerCase(Locale.US);
        return str.contains("poisonous") || str.contains("toxic");
    }

}
