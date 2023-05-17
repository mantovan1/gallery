package com.example.gallery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallery.helper.CopyImage;
import com.example.gallery.helper.ImagesExtractor;
import com.example.gallery.R;
import com.example.gallery.adapters.ImageAdapter;
import com.example.gallery.classes.Album;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumsActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_CODE = 1;
    private CopyImage copyImage = CopyImage.getmInstance();

    //Data

    private ArrayList<String> mThumbUris;
    private ArrayList<Album> mFolders;
    private String imageToCopy = "";
    private ArrayList<String> selectedMFolders = new ArrayList<>();

    //Components

    private GridView grid;
    private ImageAdapter imageAdapter;

    //Methods from the Class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadSharedPreferences();

        getSupportActionBar().setTitle(
            (!imageToCopy.equalsIgnoreCase("")) ? "Selecione uma pasta para copiar" : "Galeria"
        );

        grid = (GridView) findViewById(R.id.gvPhotos);

        //Checking Permissions

        String permissions[] = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if(!hasPermissions(this, permissions)) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(AlbumsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        AlbumsActivity.this
                );

                builder.setTitle("Grant those permission");
                builder.setMessage("Read, Write permissions");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        ActivityCompat.requestPermissions(
                                AlbumsActivity.this,
                                new String[]{
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }, 1
                        );
                    }
                });

                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else {
                ActivityCompat.requestPermissions(
                        AlbumsActivity.this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, 1
                );
            }

        } else {
            loadFolders();
            loadPhotos();
        }

       loadIntoGridView();

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!selectedMFolders.isEmpty()) {

                    ImageView ivAlbum = (ImageView) view.findViewById(R.id.ivAlbum);
                    TextView tvAlbum = (TextView) view.findViewById(R.id.tvAlbum);

                    if(selectedMFolders.contains(imageAdapter.getItem(position).getAlbumUri())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ivAlbum.setForeground(AppCompatResources.getDrawable(view.getContext(), R.drawable.gradient));
                        }

                        tvAlbum.setText(imageAdapter.getItem(position).getAlbumName());

                        selectedMFolders.remove(imageAdapter.getItem(position).getAlbumUri());

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ivAlbum.setForeground(AppCompatResources.getDrawable(view.getContext(), R.drawable.gradient_2));
                        }

                        selectedMFolders.add(imageAdapter.getItem(position).getAlbumUri());

                        tvAlbum.setText(null);

                    }

                } else {
                    if (imageToCopy != "") {

                        try {
                            copyImage.copyImage(imageToCopy, imageAdapter.getItem(position).getAlbumUri());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("secret", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("imageUri");
                        editor.apply();

                        finish();

                    } else {
                        Intent i = new Intent(getBaseContext(), PhotosFromAlbumActivity.class);
                        i.putExtra("mFolderUri", imageAdapter.getItem(position).getAlbumUri());
                        startActivity(i);
                    }

                }

            }
        });

    }

    //////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_CODE) {

            if((grantResults.length > 0)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AlbumsActivity.this, "Read/Write External Storage Permission Granted", Toast.LENGTH_SHORT).show();
                loadPhotos();
            } else {
                Toast.makeText(AlbumsActivity.this, "Read/Write External Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSharedPreferences();

        getSupportActionBar().setTitle(
                (!imageToCopy.equalsIgnoreCase("")) ? "Selecione uma pasta para copiar" : "Galeria"
        );

        loadFolders();
        loadPhotos();

        loadIntoGridView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(!selectedMFolders.isEmpty()) {
                selectedMFolders.clear();
                grid.invalidateViews();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("secret", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.remove("imageUri");

                editor.apply();

                finish();
            }
        }
        return false;
    }

    //Checking Permissions

    public boolean hasPermissions(Context context, String... permissions) {

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;

    }



    //Loading Data

    public void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("secret", MODE_PRIVATE);
        imageToCopy = sharedPreferences.getString("imageUri", "");
    }

    public void loadFolders() {
        mFolders = ImagesExtractor.listOfFolders();
    }

    public void loadPhotos() {
        mThumbUris = ImagesExtractor.listOfImages(AlbumsActivity.this);
    }

    //Putting Data into Component

    public void loadIntoGridView() {

        //Checking with the needed data is already loaded, if not, it won't do anything.

        if(!mFolders.isEmpty() && !mThumbUris.isEmpty()) {

            imageAdapter = new ImageAdapter(mFolders, mThumbUris, this);

            grid.setAdapter(imageAdapter);

            grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    ImageView ivAlbum = (ImageView) view.findViewById(R.id.ivAlbum);
                    TextView tvAlbum = (TextView) view.findViewById(R.id.tvAlbum);

                    if(selectedMFolders.contains(imageAdapter.getItem(position).getAlbumUri())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ivAlbum.setForeground(AppCompatResources.getDrawable(view.getContext(), R.drawable.gradient));
                        }

                        tvAlbum.setText(imageAdapter.getItem(position).getAlbumName());

                        selectedMFolders.remove(imageAdapter.getItem(position).getAlbumUri());

                        return false;

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ivAlbum.setForeground(AppCompatResources.getDrawable(view.getContext(), R.drawable.gradient_2));
                        }

                        selectedMFolders.add(imageAdapter.getItem(position).getAlbumUri());

                        tvAlbum.setText(null);

                        return true;
                    }
                }
            });
        }
    }
}