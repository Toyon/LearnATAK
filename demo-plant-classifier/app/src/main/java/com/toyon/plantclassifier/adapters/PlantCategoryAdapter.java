/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.plantclassifier.adapters;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atakmap.android.maps.MapView;
import com.toyon.plantclassifier.MainDropDown;
import com.toyon.plantclassifier.database.PlantEntity;
import com.toyon.plantclassifier.database.PlantsViewModel;
import com.toyon.plantclassifier.plugin.R;
import com.atakmap.coremap.log.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This adapter is used to organize the main pane plant records into the 3 categories and update
 * the lists if a database entry is modified. This is the parent recycler view that uses
 * plants adapter to populate each horizontally scrolled plants adapter.
 */
public class PlantCategoryAdapter extends RecyclerView.Adapter<PlantCategoryAdapter.CategoryViewHolder> {

    private final static String TAG = PlantCategoryAdapter.class.getSimpleName();
    private final List<PlantCategory> itemList;
    private final PlantsViewModel viewPlants;



    public PlantCategoryAdapter(PlantsViewModel viewModel) {
        this.itemList = new ArrayList<>();
        this.viewPlants = viewModel;

        try {
            this.viewPlants.getAllPlants().observe(
                    Objects.requireNonNull(ViewTreeLifecycleOwner.get(MapView.getMapView())),
                    handleDatabaseChange());
        } catch (Exception e){
            Log.d(TAG, "issue observing plants database "+ e);
        }
    }

    /**
     * Observe live data updates connected to plants table to update plant category lists.
     * Any modifications to the database will trigger a UI update to the recyclerview
     */
    private Observer<List<PlantEntity>> handleDatabaseChange() {
        List<PlantEntity> edible = new ArrayList<>(), poisonous = new ArrayList<>(), general = new ArrayList<>();
        return new Observer<List<PlantEntity>>() {
            @Override
            public void onChanged(List<PlantEntity> plants) {
                edible.clear(); poisonous.clear(); general.clear();
                for(int i = 0; i < plants.size(); i++ ) {
                    if(PlantCategory.isPoison(plants.get(i).edible))
                        poisonous.add(plants.get(i));
                    else if(PlantCategory.isEdible(plants.get(i).edible))
                        edible.add(plants.get(i));
                    else
                        general.add(plants.get(i));
                }
                updateData(Arrays.asList(
                        new PlantCategory(PlantCategory.EDIBLE, edible),
                        new PlantCategory(PlantCategory.POISON, poisonous),
                        new PlantCategory(PlantCategory.GENERAL, general)));
            }
        };
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.plant_category_list,
                viewGroup, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int position) {
        PlantCategory category = itemList.get(position);
        PlantsAdapter categoryAdapter = new PlantsAdapter(new ArrayList<>(), viewPlants);

        categoryViewHolder.categoryTitle.setText(category.getCategoryName());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                categoryViewHolder.plantRecyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
//        layoutManager.setInitialPrefetchItemCount(category.getPlantList().size());

        categoryViewHolder.plantRecyclerView.setAdapter(categoryAdapter);
        categoryViewHolder.plantRecyclerView.setLayoutManager(layoutManager);
        categoryAdapter.updateData(category.getPlantList());
    }

    public void updateData(List<PlantCategory> categories){
        itemList.clear();
        itemList.addAll(categories);
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @SuppressLint(("NotifyDataSetChanged"))
            @Override
            public void run(){ notifyDataSetChanged(); }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /** Object to describe UI elements of each nested categorized plant list recycler view */
    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryTitle;
        private final RecyclerView plantRecyclerView;

        CategoryViewHolder(final View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.parent_item_title);
            plantRecyclerView = itemView.findViewById(R.id.child_recyclerview);
        }
    }
}
