package com.example.gallery.classes;

public class Album {

    private String thumbnailUri;
    private String albumUri;
    private String albumName;
    private int quantityOfPhotos;

    public Album(String thumbnailUri, String albumUri, String albumName, int quantityOfPhotos) {

        this.thumbnailUri = thumbnailUri;
        this.albumUri = albumUri;
        this.albumName = albumName;
        this.quantityOfPhotos = quantityOfPhotos;

    }

    public String getAlbumUri() { return albumUri; }

    public String getAlbumName() {
        return albumName;
    }

    public int getQuantityOfPhotos() {
        return quantityOfPhotos;
    }
}
