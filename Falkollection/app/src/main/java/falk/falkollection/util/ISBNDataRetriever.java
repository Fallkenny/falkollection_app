package falk.falkollection.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import falk.falkollection.model.ISBNTitleBitmap;
import falk.falkollection.model.ISBNdbBook;
import falk.falkollection.model.ISBNdbResponse;
import falk.falkollection.model.OpenLibraryISBNResponse;
import falk.falkollection.rest.ApiClient;
import falk.falkollection.rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Response;

public class ISBNDataRetriever {

    final static String ISBNdb_apikey = "46300_b1d8da1febda9d18c882b29e8b054086";
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            final Bitmap[] myBitmap = {null};

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        myBitmap[0] = BitmapFactory.decodeStream(input);
                    }
                    catch (Exception ex)
                    {

                    }
                }
            });

            thread.start();


            while (thread.isAlive())
            {

            }
            return myBitmap[0];
        } catch (Exception e) {
            // Log exception
            return null;
        }
    }

    public static Document getHTMLFromURL(String src) {
        final Document[] doc = {null};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    doc[0] = Jsoup.connect(src).get();
                }
                catch (Exception ex)
                { }
            }
        });
        thread.start();

        while (thread.isAlive())
        { }
        return doc[0];
    }



    public static ISBNTitleBitmap getTitleAndImageFromISBN(long isbn)
    {

        //Tenta pegar pelo openLibrary
        ISBNTitleBitmap dataOL = getDataFromOpenLibrary(isbn);
        if(dataOL !=null)
        {
            if(dataOL.getTitle() !=null && !dataOL.getTitle().isEmpty() &&
            dataOL.getBitmap()!=null)
                return dataOL;
        }


        return null;
    }

    public static ISBNTitleBitmap getDataFromOpenLibrary (long isbn) {

        ISBNTitleBitmap data1 = getDataFromISBNdbJSON(isbn);

        if(data1 !=null && data1.isComplete())
        {
            return data1;
        }

        ISBNTitleBitmap data2 = getDataFromOpenLibraryJSON(isbn);

        if(data2 == null)
        {

            boolean createdPage = tryCreatingPage(isbn);
            if(createdPage)
                data2 = getDataFromOpenLibraryJSON(isbn);
        }

        if(data1 !=null)
        {
            if(!data1.isTitleValid() && data2.isTitleValid())
                data1.setTitle(data2.getTitle());

            if(data1.getBitmap()==null && data2.getBitmap() !=null)
                data1.setBitmap(data2.getBitmap());
            return data1;
        }
        return data2;
    }

    public static ISBNTitleBitmap getDataFromISBNdbJSON (long isbn) {

        ApiInterface apiInterface = ApiClient.getClientISBNdb().create(ApiInterface.class);
        Call<ISBNdbResponse> call = apiInterface.getByISBNFromISBNdbJson(ISBNdb_apikey,isbn);
        final Response<ISBNdbResponse>[] response = new Response[]{null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    response[0] = call.execute();
                }
                catch (Exception ex)
                {

                }
            }
        });
        thread.start();

        while (thread.isAlive())
        {}

        if(response[0] == null)
            return null;

        int statusCode = response[0].code();
        if(statusCode == 200)
        {
            ISBNTitleBitmap data = new ISBNTitleBitmap();
            ISBNdbResponse ISBNdbResponse = ((Response<ISBNdbResponse>) response[0]).body();
            ISBNdbBook book = ISBNdbResponse.getBook();

            String coverlink = book.getImage();
            if(coverlink !=null)
                data.setBitmap(getBitmapFromURL(coverlink));

            data.setTitle(book.getTitle());
            return data;
        }
        else
            return null;
    }



    public static ISBNTitleBitmap getDataFromOpenLibraryJSON (long isbn) {

        ApiInterface apiInterface = ApiClient.getClientOpenLibrary().create(ApiInterface.class);
        Call<OpenLibraryISBNResponse> call = apiInterface.getByISBNFromOpenLibraryJson(isbn);
        final Response<OpenLibraryISBNResponse>[] response = new Response[]{null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    response[0] = call.execute();
                }
                catch (Exception ex)
                {

                }
            }
        });
        thread.start();

        while (thread.isAlive())
        {}

        if(response[0] == null)
            return null;

        int statusCode = response[0].code();
        if(statusCode == 200)
        {
            ISBNTitleBitmap data = new ISBNTitleBitmap();
            OpenLibraryISBNResponse openLibraryISBNResponse = ((Response<OpenLibraryISBNResponse>) response[0]).body();
            if(openLibraryISBNResponse.getCovers().size()>0)
            {
                int coverCode = openLibraryISBNResponse.getCovers().get(0);
                String coverlink = "https://covers.openlibrary.org/b/id/"+coverCode+"-L.jpg";
                data.setBitmap(getBitmapFromURL(coverlink));
            }
            String name =((Response<OpenLibraryISBNResponse>) response[0]).body().getTitle();
            data.setTitle(name);
            return data;
        }
        else
            return null;
    }

    //o openlibrary tem um bot que eventualmente consegue criar uma página do livro ao fazer o request para a pagina HTML, caso aconteça, o endpoint json fica disponivel também
    public static boolean tryCreatingPage(long isbn)
    {
        Document doc = getHTMLFromURL("https://openlibrary.org/isbn/"+isbn);
        return doc != null;
    }


}
