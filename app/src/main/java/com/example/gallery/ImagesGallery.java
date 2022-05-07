package com.example.gallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;

public class ImagesGallery {

    private static ArrayList <String> mFolders = new ArrayList<>();

    public static ArrayList <String> listOfImages(Context context) {

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        ArrayList <String> listOfAllImages = new ArrayList<>();

        String absolutePathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;

        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC" );

        try {
            cursor.moveToFirst();

            do {

                absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                String splitPath[] = absolutePathOfImage.split("/");

                String path = "";

                for (int i = 0; i < splitPath.length-1; i++) {
                    path += splitPath[i] + "/";
                }

                boolean isInMFolders = false;

                for ( String folderPath : mFolders ) {
                    if (folderPath.equalsIgnoreCase(path)) {
                        isInMFolders = true;
                    }
                }

                if(isInMFolders == false) {
                    mFolders.add(path);
                }

                listOfAllImages.add(absolutePathOfImage);
            } while(cursor.moveToNext());

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }



        return listOfAllImages;

    }

    public static ArrayList <String> listOfFolders() {

        return mFolders;
    }

    public static ArrayList <String> listOfImagesFromAlbum(Context context, String folderUri) {

        ArrayList <String> listOfImages = new ArrayList<>();

        File file = new File(folderUri);

        File[] files = file.listFiles();

        if (files != null) {
            for (File file1 : files) {
                if (file1.getPath().endsWith(".png") || file1.getPath().endsWith(".jpg") || file1.getPath().endsWith(".jpeg")) {
                    listOfImages.add(file1.getAbsolutePath());
                }
            }
        }

        return listOfImages;

    }

}
