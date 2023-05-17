package com.example.gallery.helper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeleteImage {

    private final int DELETE_REQUEST_CODE = 2;
    private static DeleteImage mInstance = null;

    private DeleteImage() {}

    public static DeleteImage getmInstance() {
        if(mInstance == null) {
            mInstance = new DeleteImage();
        }

        return mInstance;
    }

    public int deleteAPI28(Uri uri, Context context) {
        ContentResolver resolver = context.getContentResolver();

        return resolver.delete(uri, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void deleteAPI30(Uri imageUri, Context context) throws IntentSender.SendIntentException {
        ContentResolver contentResolver = context.getContentResolver();
        // API 30

        List<Uri> uriList = new ArrayList<>();
        Collections.addAll(uriList, imageUri);
        PendingIntent pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
        ((Activity)context).startIntentSenderForResult(pendingIntent.getIntentSender(),
                DELETE_REQUEST_CODE,null,0,
                0,0,null);

    }
}
