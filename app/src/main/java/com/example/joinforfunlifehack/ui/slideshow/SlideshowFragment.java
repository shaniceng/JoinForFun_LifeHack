package com.example.joinforfunlifehack.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.joinforfunlifehack.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SlideshowFragment extends Fragment {

    RecyclerView recyclerView;
    List<Recipe> recipes;
    RecipeAdapter adapter;

    private static String api_url = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=6b50988925034adfbb0437f1efab5d0c&ingredients=apple";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        recyclerView = view.findViewById(R.id.recipeList);
        recipes = new ArrayList<>();
        retrieveRecipes();

        return view;
    }

    private void retrieveRecipes() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, api_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject recipeObject = response.getJSONObject(i);
                        Recipe recipe = new Recipe();
                        recipe.setTitle(recipeObject.getString("title").toString());
                        recipe.setRecipeImageUrl(recipeObject.getString("image"));

                        recipes.add(recipe);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                adapter = new RecipeAdapter(getActivity().getApplicationContext(), recipes);
                recyclerView.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}