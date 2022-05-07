package com.example.gallery;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullView extends AppCompatActivity {

    private static final int DELETE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullview);

        ImageView ivFoto = (ImageView) findViewById(R.id.ivFoto);
        ImageButton ibDelete = (ImageButton) findViewById(R.id.ibDelete);
        ImageButton ibCopy = (ImageButton) findViewById(R.id.ibCopy);

        Bundle extras = getIntent().getExtras();

        String mImageUri = "";

        if (extras != null) {
            mImageUri = extras.getString("mImageUri");

            Glide.with(FullView.this).load(mImageUri).into(ivFoto);

        }

        getSupportActionBar().setTitle(mImageUri);

        String finalMImageUri = mImageUri;

        ibCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copy(finalMImageUri);
            }
        });

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = getContentUriId(Uri.parse(finalMImageUri));
                try {
                    deleteAPI28(uri, FullView.this);
                    //alertDialog.dismiss();
                }catch (Exception e){
                    //  PendingIntent createDeleteRequest()
                    Toast.makeText(FullView.this,"Permission needed", Toast.LENGTH_SHORT).show();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            deleteAPI30(uri);
                        }
//                        notifyItemRemoved(position);
//                        Toast.makeText(context, "Image Deleted successfully", Toast.LENGTH_SHORT).show();
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                    //alertDialog.dismiss();

                }


            }
        });

    }

    private void copy(String imageUri) {

        SharedPreferences preferences = getSharedPreferences("secret", MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("imageUri", imageUri);

        editor.apply();

        Intent i = new Intent(FullView.this, MainActivity.class);
        startActivity(i);

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void deleteAPI30(Uri imageUri) throws IntentSender.SendIntentException {
        ContentResolver contentResolver = this.getContentResolver();
        // API 30

        List<Uri> uriList = new ArrayList<>();
        Collections.addAll(uriList, imageUri);
        PendingIntent pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
        ((Activity)this).startIntentSenderForResult(pendingIntent.getIntentSender(),
                DELETE_REQUEST_CODE,null,0,
                0,0,null);

    }

    private Uri getContentUriId(Uri imageUri) {
        String[] projections = {MediaStore.MediaColumns._ID};
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projections,
                MediaStore.MediaColumns.DATA + "=?",
                new String[]{imageUri.getPath()}, null);
        long id = 0;
        if (cursor != null){
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                id  = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }
        }
        cursor.close();
        return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf((int)id));
    }
    public static int deleteAPI28(Uri uri, Context context) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }

}
