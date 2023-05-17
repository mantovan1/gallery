package com.example.gallery.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gallery.helper.DeleteImage;
import com.example.gallery.R;

public class FullPhotoActivity extends AppCompatActivity {

    private DeleteImage imageDelete = DeleteImage.getmInstance();

    //Data
    String mImageUri = "";

    //Components
    ImageView ivPhoto;
    ImageButton ibDelete;
    ImageButton ibCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullview);

        ivPhoto = (ImageView) findViewById(R.id.ivFoto);
        ibDelete = (ImageButton) findViewById(R.id.ibDelete);
        ibCopy = (ImageButton) findViewById(R.id.ibCopy);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mImageUri = extras.getString("mImageUri");

            Glide.with(FullPhotoActivity.this).load(mImageUri).into(ivPhoto);

        }

        getSupportActionBar().setTitle(Uri.parse(mImageUri).getLastPathSegment());

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
                    imageDelete.deleteAPI28(uri, FullPhotoActivity.this);
                    //alertDialog.dismiss();
                }catch (Exception e){
                    //  PendingIntent createDeleteRequest()
                    Toast.makeText(FullPhotoActivity.this,"Permission needed", Toast.LENGTH_SHORT).show();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            imageDelete.deleteAPI30(uri, FullPhotoActivity.this);
                        }
//                        notifyItemRemoved(position);
//                        Toast.makeText(context, "Image Deleted successfully", Toast.LENGTH_SHORT).show();
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }

                    //finish();

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

        Intent i = new Intent(FullPhotoActivity.this, AlbumsActivity.class);
        startActivity(i);

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

}
