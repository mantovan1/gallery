package com.example.gallery;

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
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallery.adapters.ImageAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> mThumbUris;
    ArrayList<String> mFolders;
    private static final int MY_PERMISSION_CODE = 1;
    String imageToCopy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Galeria");

        SharedPreferences sharedPreferences = getSharedPreferences("secret", MODE_PRIVATE);
        imageToCopy = sharedPreferences.getString("imageUri", null);

        if(imageToCopy != null) {
            getSupportActionBar().setTitle("Selecione uma pasta para copiar");
        }

        GridView grid = (GridView) findViewById(R.id.gvPhotos);

        String permissions[] = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if(!hasPermissions(this, permissions)) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivity.this
                );

                builder.setTitle("Grant those permission");
                builder.setMessage("Read, Write permissions");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
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
                        MainActivity.this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, 1
                );
            }

        } else {
            loadPastas();
            loadFotos();
        }

        ImageAdapter imageAdaptor = new ImageAdapter(mFolders, mThumbUris, this);

        grid.setAdapter(imageAdaptor);

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView ivAlbum = (ImageView) view.findViewById(R.id.ivAlbum);
                TextView tvAlbum = (TextView) view.findViewById(R.id.tvAlbum);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ivAlbum.setForeground(AppCompatResources.getDrawable(view.getContext(), R.drawable.gradient_2));
                }

                tvAlbum.setText(null);

                return true;

            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(imageToCopy != null) {
                    InputStream in = null;
                    OutputStream out = null;

                    Uri imageUri = Uri.parse(imageToCopy);

                    try {

                        //create output directory if it doesn't exist
                        File dir = new File (imageAdaptor.getItem(position));
                        if (!dir.exists())
                        {
                            dir.mkdirs();
                        }


                        in = new FileInputStream(imageUri.getPath());
                        out = new FileOutputStream(imageAdaptor.getItem(position) + imageUri.getLastPathSegment());

                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        in = null;

                        // write the output file
                        out.flush();
                        out.close();
                        out = null;

                        // delete the original file
                        //new File(imageUri.getPath()).delete();


                    }

                    catch (FileNotFoundException fnfe1) {
                        Log.e("tag", fnfe1.getMessage());
                    }
                    catch (Exception e) {
                        Log.e("tag", e.getMessage());
                    }

                }

                SharedPreferences sharedPreferences = getSharedPreferences("secret", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.remove("imageUri");

                editor.apply();

                Intent i = new Intent(getBaseContext(), FotosActivity.class);
                i.putExtra("mFolderUri", imageAdaptor.getItem(position));
                startActivity(i);

            }
        });

    }

    public boolean hasPermissions(Context context, String... permissions) {

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_CODE) {

           if((grantResults.length > 0)
           && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               Toast.makeText(MainActivity.this, "Read/Write External Storage Permission Granted", Toast.LENGTH_SHORT).show();
               loadFotos();
           } else {
               Toast.makeText(MainActivity.this, "Read/Write External Storage Permission Denied", Toast.LENGTH_SHORT).show();
           }
        }
    }

    public void loadPastas() {
        mFolders = ImagesGallery.listOfFolders();
    }

    public void loadFotos() {
        mThumbUris = ImagesGallery.listOfImages(MainActivity.this);
    }
}