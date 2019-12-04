package com.example.finalgroupproject.recipe;
    /**
    * Recipe implements the basic requirements for the SQL table
    */
public class Recipe{
    private Long id;
    private String title;
    private String imageURL;
    private String sourceURL;
    /**
     * @param title, recipe title name
     * @param imageURL, image url
     * @param sourceURL, the source url
     */
    public Recipe(String title, String imageURL, String sourceURL){
        this.setTitle(title);
        this.setImageURL(imageURL);
        this.setSourceURL(sourceURL);
    }
    /**
     * @param id, database id
     * @param title, recipe title name
     * @param imageURL, image url
     * @param sourceURL, the source url
     */
    public Recipe(Long id, String title, String imageURL, String sourceURL) {
        this.setID(id);
        this.setTitle(title);
        this.setImageURL(imageURL);
        this.setSourceURL(sourceURL);
    }
    /**
      * report the database ID
      * @return id
      */
    public Long getID() {
        return id;
    }
    /**
     * set a database ID#
     * @param id the database to set
     */
        public void setID(Long id){
        this.id = id;
    }
    /**
     * report a title
     * @return title
     */
    public String getTitle() {
        return title;
    }
    /**
     * set a title
     * @param title the title to set
     */
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
