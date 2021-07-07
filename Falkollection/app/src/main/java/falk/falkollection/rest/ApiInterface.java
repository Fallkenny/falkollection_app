package falk.falkollection.rest;

import android.text.Html;

import falk.falkollection.model.ISBNdbResponse;
import falk.falkollection.model.OpenLibraryISBNResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // curl -X GET -i 'http://api.themoviedb.org/3/movie/top_rated?api_key=7e8f60e325cd06e164799af1e317d7a7'

    @GET("book/{isbn}")
    Call<ISBNdbResponse> getByISBNFromISBNdbJson(@Header("Authorization") String authHeader, @Path("isbn") long isbn);

    // curl -X GET -i 'http://api.themoviedb.org/3/movie/238?api_key=7e8f60e325cd06e164799af1e317d7a7'

    @GET("isbn/{isbn}.json")
    Call<OpenLibraryISBNResponse> getByISBNFromOpenLibraryJson(@Path("isbn") long isbn);
}
