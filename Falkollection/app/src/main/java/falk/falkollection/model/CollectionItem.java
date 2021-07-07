package falk.falkollection.model;

import android.graphics.Bitmap;

public class CollectionItem {

    private String title;
    private int volumeNo;
    private Bitmap image;
    private int id;
    private long isbn;
    private boolean owned;
    private int collectionID;
    // Getter Methods

    public int getVolumeNo() {
        return volumeNo;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public int getCollectionID() {
        return collectionID;
    }

    public long getIsbn() {
        return isbn;
    }

    public boolean getOwned() {
        return this.owned;
    }
    // Setter Methods
    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCollectionId(int collectionId) {
        this.collectionID = collectionId;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public void setVolumeNo( int volumeNo ) {
        this.volumeNo = volumeNo;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public void setImage( Bitmap image ) {
        this.image = image;
    }

}
