package falk.falkollection.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ISBNdbResponse {

    @SerializedName("book")
    @Expose
    private ISBNdbBook book;

    public ISBNdbBook getBook() {
        return book;
    }

    public void setBook(ISBNdbBook book) {
        this.book = book;
    }

}