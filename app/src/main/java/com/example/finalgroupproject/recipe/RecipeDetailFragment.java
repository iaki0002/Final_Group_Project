package com.example.finalgroupproject.recipe;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalgroupproject.R;
import com.squareup.picasso.Picasso;

import com.google.android.material.snackbar.Snackbar;

import static android.os.FileUtils.copy;

/**
 * A single item page with title, image, Favorite icon, Open web page icon and Close icon
 */
public class RecipeDetailFragment extends Fragment {

    private Bundle dataFromActivity;
    private Button gotoWebpageBtn;
    private Button saveToFavoriteBtn;
    private Button closeBtn;
    private ImageView imageView;
    private TextView titleText;
    private Bitmap image, bitmap;
    private RecipeFavoriteActivity recipeFavoriteActivity;
    //private static final int IO_BUFFER_SIZE = 4 * 1024;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        dataFromActivity = getArguments();
        initView(view);
        return view;
    }

    private void initView(View view){
        gotoWebpageBtn = view.findViewById(R.id.recipe_detail_fragment_webview_btn);
        saveToFavoriteBtn = view.findViewById(R.id.recipe_detail_fragment_favorite_btn);
        closeBtn = view.findViewById(R.id.recipe_detail_fragment_close_btn);
        imageView= view.findViewById(R.id.recipe_detail_fragment_imageview);
        titleText=view.findViewById(R.id.recipe_detail_fragment_title);
        //set the title
        titleText.setText(dataFromActivity.getString(MainRecipeActivity.TITLE));
//Picasso - load the URL image
        String url = dataFromActivity.getString(MainRecipeActivity.IMAGE_URL);
        Picasso.get().load(url).into(imageView);

//Goto the web page in RecipeWebViewActivity
        gotoWebpageBtn.setOnClickListener(k->{
            Toast.makeText(getActivity(), "Go to the Web Page!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), RecipeWebViewActivity.class);
           intent.putExtra(MainRecipeActivity.TITLE,dataFromActivity.getString(MainRecipeActivity.URL));
            startActivity(intent);
        });

//Save Favorite item in the DB
        saveToFavoriteBtn.setOnClickListener(k->{
            RecipeDBHelper dbHelper = new RecipeDBHelper(getContext());

         /*   int size = recipeFavoriteActivity.foodList.size();
            String selectFavorite = dataFromActivity.getString(MainRecipeActivity.TITLE);
            boolean noDuplicate = true;

//Check duplicate favorite
            for (int i = 0; i < size; i++) {
                if (selectFavorite.equals(recipeFavoriteActivity.foodList.get(i).getTitle())) {
                    //if Duplicate
                    noDuplicate = false;

                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.recipe_dialog_alert)
                            .setMessage(R.string.recipe_alert_duplicate)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            //.setNegativeButton("No", null)
                            .show();

                }
            }//end of for loop
            //if not duplicate
            if(noDuplicate) {

          */
               if(dbHelper.insert(
                       dataFromActivity.getString(MainRecipeActivity.TITLE),
                       dataFromActivity.getString(MainRecipeActivity.IMAGE_URL),
                       dataFromActivity.getString(MainRecipeActivity.URL))>0){
                       Toast.makeText(getActivity(), "saved successfully", Toast.LENGTH_LONG).show();
                       Log.d("RecipeDetailFragment","saved successfully");
                    }else{
               }
          //--  }
         });

//Close - Back to List View
        closeBtn.setOnClickListener(k->{
            Snackbar.make(closeBtn, "Go Back?     OK!   Bye!", Snackbar.LENGTH_LONG).show();
            getActivity().onBackPressed();
        });
    }

}
