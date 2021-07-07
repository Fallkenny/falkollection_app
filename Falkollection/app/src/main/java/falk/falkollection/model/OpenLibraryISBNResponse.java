package falk.falkollection.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenLibraryISBNResponse {


    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("isbn_13")
    @Expose
    private List<String> isbn13 = null;
    @SerializedName("covers")
    @Expose
    private List<Integer> covers = null;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(List<String> isbn13) {
        this.isbn13 = isbn13;
    }

    public List<Integer> getCovers() {
        return covers;
    }

    public void setCovers(List<Integer> covers) {
        this.covers = covers;
    }



}