package com.example.finalgroupproject.recipe;


public class Recipe{
    private Long id;
    private String title;
    private String imageURL;
    private String sourceURL;


    public Recipe(String title, String imageURL, String sourceURL){
        this.setTitle(title);
        this.setImageURL(imageURL);
        this.setSourceURL(sourceURL);

    }
    public Recipe(Long id, String title, String imageURL, String sourceURL) {
        this.setID(id);
        this.setTitle(title);
        this.setImageURL(imageURL);
        this.setSourceURL(sourceURL);
    }

    public Long getID() {
        return id;
    }
    public void setID(Long id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL){
        this.imageURL = imageURL;
    }

    public String getSourceURL() {
        return sourceURL;
    }
    public void setSourceURL(String sourceURL){
        this.sourceURL = sourceURL;
    }


}
