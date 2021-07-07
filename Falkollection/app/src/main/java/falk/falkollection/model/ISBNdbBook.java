package falk.falkollection.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ISBNdbBook {

    @SerializedName("isbn13")
    @Expose
    private String isbn13;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image")
    @Expose
    private String image;

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}