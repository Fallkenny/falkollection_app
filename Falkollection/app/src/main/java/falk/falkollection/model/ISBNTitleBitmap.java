package falk.falkollection.model;

import android.graphics.Bitmap;

public class ISBNTitleBitmap {
    private String title;
    private Bitmap bitmap;

    public String getTitle() {
        return title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public void setBitmap( Bitmap bitmap ) {
        this.bitmap = bitmap;
    }

    public boolean isComplete() {return this.isTitleValid() && this.getBitmap()!=null;}

    public boolean isTitleValid() {return this.title !=null && !this.getTitle().isEmpty();}

}
