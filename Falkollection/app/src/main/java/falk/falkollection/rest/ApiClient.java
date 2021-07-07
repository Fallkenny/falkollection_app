package falk.falkollection.rest;

import falk.falkollection.model.ISBNdbBook;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String OPENLIBRARY_URL = "https://openlibrary.org/";
    public static final String ISBNDB_URL = "https://api2.isbndb.com/";
    private static Retrofit retrofit_covers = null;
    private static Retrofit retrofit_openLibrary = null;


    public static Retrofit getClientOpenLibrary() {
        if (retrofit_openLibrary ==null) {
            retrofit_openLibrary = new Retrofit.Builder()
                    .baseUrl(OPENLIBRARY_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit_openLibrary;
    }

    public static Retrofit getClientISBNdb() {
        if (retrofit_covers ==null) {
            retrofit_covers = new Retrofit.Builder()
                    .baseUrl(ISBNDB_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit_covers;
    }

}

