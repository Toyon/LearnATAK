/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.foodclassifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.coremap.log.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.toyon.foodclassifier.plugin.R;
import com.toyon.foodclassifier.plugin.ml.LiteModelAiyVisionClassifierFoodV11;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

/**
 * This is the pane that is opened after the user takes the picture and presses "OK." The user
 * reviews the food with a 1-5 star review and can change the name of the food if they disagree
 * with the label provided by TF model
 */
public class ReviewPageDropDown extends DropDownReceiver implements OnStateListener{

    private static final String TAG = ReviewPageDropDown.class.getSimpleName();
    public static final String SAMPLE_ACTION = "com.toyon.foodclassifier.ReviewPageDropDownReceiver.SAMPLE_ACTION";
    private final Context pluginCtx;
    private final View paneView;

    private String foodItemName;
    private int foodItemScore;
    private Bitmap foodBitmap;

    protected ReviewPageDropDown(MapView mapView, final Context pluginContext){
        super(mapView);
        pluginCtx = pluginContext;
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.review_pane);
        foodItemScore = -1;
        Button[] stars = initStarRatingButtons();
        initSubmitReviewButton(stars);
        initChangeFoodItemNameComponents();
    }

    /** Setup star buttons to assign food score and properly color stars according to rating */
    private Button[] initStarRatingButtons() {
        Button[] stars = new Button[] {
                paneView.findViewById(R.id.star1),
                paneView.findViewById(R.id.star2),
                paneView.findViewById(R.id.star3),
                paneView.findViewById(R.id.star4),
                paneView.findViewById(R.id.star5)
        };
        for (int i = 0; i < stars.length; i++) {
            selectStarTint(stars[i], pluginCtx, false);
            int score = i+1;
            stars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    foodItemScore = score;
                    for (int j = 0; j < stars.length; j++) {
                        selectStarTint(stars[j], pluginCtx, j<score);
                    }
                }
            });
        }
        return stars;
    }

    /** Setup submit button to send intents to open the main pane and record the food review info */
    private void initSubmitReviewButton(Button[] stars) {
        Button submitButton = paneView.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foodItemScore != -1) {
                    Log.d(TAG, "Send intent for Main DropDown to record review and open ui");
                    Intent intentRecordReview = new Intent()
                            .setAction(MainDropDown.ADD_TO_LIST)
                            .putExtra("bitmap", foodBitmap)
                            .putExtra("name", foodItemName)
                            .putExtra("review", String.valueOf(foodItemScore));
                    // reset food item review UI and values before sending intent
                    for (Button star : stars) { selectStarTint(star, pluginCtx, false); }
                    foodItemScore = -1;
                    foodBitmap = null;
                    foodItemName = "";
                    AtakBroadcast.getInstance().sendBroadcast(intentRecordReview);
                }
            }
        });
    }

    /** Setup UI components on review page to enable modification of review food item name */
    private void initChangeFoodItemNameComponents() {
        // Consume the event (returning true) to prevent "Enter" key press
        final EditText changeNameEditText = paneView.findViewById(R.id.change_name_edit_text);
        changeNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                return actionId == EditorInfo.IME_ACTION_DONE;
            }
        });

        Button changeNameButton = paneView.findViewById(R.id.change_name_submit);
        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredText = changeNameEditText.getText().toString();
                TextView edittext = paneView.findViewById(R.id.img_label_txt);
                edittext.setText(enteredText);
                foodItemName = enteredText;
                changeNameEditText.setText("");
            }
        });
    }

    /**
     * This TF model gets the picture of the food (one item) and classifies it based on the Food
     * Classifier model. It takes the picture and outputs the food item with the highest
     * probability match.
     * The model is found here: https://tfhub.dev/google/aiy/vision/classifier/food_V1/1
     */
    private void modelInference(Bitmap myBitmap) {
        // perform model inference on bitmap and store results in outputs
        LiteModelAiyVisionClassifierFoodV11.Outputs outputs;
        try {
            LiteModelAiyVisionClassifierFoodV11 model = LiteModelAiyVisionClassifierFoodV11
                    .newInstance(pluginCtx);
            TensorImage image = TensorImage.fromBitmap(myBitmap);
            outputs = model.process(image);
            model.close(); // release model resources that are no longer used
        } catch (IOException e) {
                Log.e(TAG, "Failed to perform inference on image.\n" + e);
                return;
        }

        // get the highest scored label from the inference results
        List<Category> probability = outputs.getProbabilityAsCategoryList();
        Category mostLikelyCategory = Collections.max(probability, new Comparator<Category>() {
            @Override
            public int compare(Category first, Category second) {
                try {
                    // negative: first < second, zero: first == second, positive: first > second
                    return Float.compare(first.getScore(), second.getScore());
                } catch (NullPointerException | ClassCastException e) {
                    return 0;
                }
            }
        });

        // set food item name and UI display to predicted food label
        try {
            Log.d(TAG, String.format(Locale.US, "Best label: %s, Score: (%.2f)",
                    mostLikelyCategory.getLabel(), mostLikelyCategory.getScore()));
            TextView edittext = paneView.findViewById(R.id.img_label_txt);
            foodItemName = mostLikelyCategory.getLabel();
            edittext.setText(mostLikelyCategory.getLabel());
        } catch (NullPointerException e) {
            Log.e(TAG, "UNABLE TO CLASSIFY FOOD ITEM FROM IMAGE");
        }
    }

    /** Set the tint of a star button to active (selected) or inactive (deselected) color */
    private void selectStarTint(Button starButton, Context context, boolean active) {
        starButton.setBackgroundTintList(
                ContextCompat.getColorStateList(context, active ?
                        R.color.star_selected : R.color.star_unselected)
        );
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SAMPLE_ACTION)) {
            showDropDown(paneView,
                    HALF_WIDTH, FULL_HEIGHT, // Landscape Dimensions
                    FULL_WIDTH, THIRD_HEIGHT, // Portrait Dimensions
                    false, this);
            foodBitmap = intent.getParcelableExtra("imageBitmap");
            ImageView imageView = paneView.findViewById(R.id.img_preview);
            imageView.setImageBitmap(foodBitmap);
            modelInference(foodBitmap);
        }
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownVisible(boolean v) { }

    @Override
    public void onDropDownSizeChanged(double width, double height) { }

    @Override
    public void onDropDownClose() { }

    protected void disposeImpl() { }

}
