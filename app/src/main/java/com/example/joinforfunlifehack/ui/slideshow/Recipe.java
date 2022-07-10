package com.example.joinforfunlifehack.ui.slideshow;

public class Recipe {
    private String title;
    private String recipeImageUrl;

    public Recipe() {}
    public Recipe(String title, String recipeImageUrl) {
        this.title = title;
        this.recipeImageUrl = recipeImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }

    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
    }
}