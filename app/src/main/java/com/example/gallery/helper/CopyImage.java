package com.example.gallery.helper;

import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyImage {

    private static CopyImage mInstance = null;

    private CopyImage() {
    }

    public static CopyImage getmInstance() {
        if (mInstance == null) {
            mInstance = new CopyImage();
        }

        return mInstance;
    }

    public void copyImage (String imageToCopy, String albumURI) throws IOException {

        InputStream in = null;
        OutputStream out = null;

        Uri imageURI = Uri.parse(imageToCopy);

        File dir = new File(albumURI);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        in = new FileInputStream(imageURI.getPath());
        out = new FileOutputStream(albumURI + imageURI.getLastPathSegment());

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
    }
}
