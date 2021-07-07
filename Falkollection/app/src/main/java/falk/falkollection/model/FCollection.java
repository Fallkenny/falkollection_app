package falk.falkollection.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class FCollection {

    private int id;
    private String name;
    private int volumesTotal;
    List<Integer> volumes;
    private Bitmap image;

    // Getter Methods

    public Bitmap getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVolumesTotal() {
        return volumesTotal;
    }

    // Setter Methods

    public List<Integer> getVolumes() {
        return this.volumes; }

    public void setVolumes(List<Integer> volumes) {
        this.volumes = volumes; }

    public void setId( int id ) {
        this.id = id;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setVolumesTotal( int volumesTotal ) {
        this.volumesTotal = volumesTotal;
    }

    public void setImage( Bitmap image ) {
        this.image = image;
    }
}
