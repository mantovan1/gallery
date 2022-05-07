package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.gallery.adapters.ImageAlbumAdapter;

import java.util.ArrayList;

public class FotosActivity extends AppCompatActivity {

    ArrayList<String> mThumbUris;
    private static final int MY_READ_PERMISSION_CODE = 101;
    private static final int MY_WRITE_PERMISSION_CODE = 1;
    public String mFolderUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView grid = (GridView) findViewById(R.id.gvPhotos);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mFolderUri = extras.getString("mFolderUri");
        }

        loadFotos();

        getSupportActionBar().setTitle(mFolderUri);

        ImageAlbumAdapter imageAlbumAdapter = new ImageAlbumAdapter(mThumbUris, this);

        grid.setAdapter(imageAlbumAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String mImageUri = imageAlbumAdapter.getItem(position);

                Intent i = new Intent(getBaseContext(), FullView.class);
                i.putExtra("mImageUri", mImageUri);
                startActivity(i);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_READ_PERMISSION_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FotosActivity.this, "Read External Storage Permission Granted", Toast.LENGTH_SHORT).show();
                loadFotos();
            } else {
                Toast.makeText(FotosActivity.this, "Read External Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadFotos() {
        mThumbUris = ImagesGallery.listOfImagesFromAlbum(FotosActivity.this, mFolderUri);
    }
}
