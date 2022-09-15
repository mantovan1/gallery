package com.example.gallery.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.gallery.helper.DeleteImage;
import com.example.gallery.helper.ImagesExtractor;
import com.example.gallery.R;
import com.example.gallery.adapters.ImageAlbumAdapter;

import java.util.ArrayList;

public class PhotosFromAlbumActivity extends AppCompatActivity {

    private static final int MY_READ_PERMISSION_CODE = 101;
    private static final int MY_WRITE_PERMISSION_CODE = 1;
    private DeleteImage imageDelete = DeleteImage.getmInstance();

    //Data

    private ArrayList<String> mThumbUris;
    private String mFolderUri;

    //Components

    private GridView grid;
    private ImageAlbumAdapter imageAlbumAdapter;

    //Methods from the Class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grid = (GridView) findViewById(R.id.gvPhotos);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mFolderUri = extras.getString("mFolderUri");
        }

        loadPhotos();

        getSupportActionBar().setTitle(Uri.parse(mFolderUri).getLastPathSegment());

        loadIntoGridView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPhotos();
        loadIntoGridView();
    }

    //Loading Data

    public void loadPhotos() {
        mThumbUris = ImagesExtractor.listOfImagesFromAlbum(PhotosFromAlbumActivity.this, mFolderUri);
    }

    //Putting Data into Component

    public void loadIntoGridView() {

        if(!mThumbUris.isEmpty()) {

            imageAlbumAdapter = new ImageAlbumAdapter(mThumbUris, this);
            grid.setAdapter(imageAlbumAdapter);

            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String mImageUri = imageAlbumAdapter.getItem(position);

                    Intent i = new Intent(getBaseContext(), FullPhotoActivity.class);
                    i.putExtra("mImageUri", mImageUri);

                    startActivity(i);
                }
            });

        }
    }
}
