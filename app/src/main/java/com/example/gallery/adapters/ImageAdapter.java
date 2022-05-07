package com.example.gallery.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.example.gallery.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private List<String> mFolders;
    private List<String> mThumbUris;
    private Context mContext;

    public ImageAdapter(List <String> mFolders, List <String> mThumbUris, Context mContext) {
        this.mFolders = mFolders;
        this.mThumbUris = mThumbUris;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mFolders.size();
    }

    @Override
    public String getItem(int position) {
        return mFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(mContext, R.layout.layout_thumbnail, null);

        ImageView ivAlbum = (ImageView) v.findViewById(R.id.ivAlbum);

        TextView tvAlbum = (TextView) v.findViewById(R.id.tvAlbum);

        String folderPath = mFolders.get(position);
        String mThumbUri = "";


        for (String thumbUri : mThumbUris) {

            String splitPath[] = thumbUri.split("/");

            String path = "";

            for (int i = 0; i < splitPath.length-1; i++) {
                path += splitPath[i] + "/";
            }

            if(path.equalsIgnoreCase(folderPath)) {
                mThumbUri = thumbUri;
                break;
            }
        }

        Glide.with(mContext)
                .load(mThumbUri)
                .centerCrop()
                .override(500)
                .into(ivAlbum);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ivAlbum.setForeground(AppCompatResources.getDrawable(mContext, R.drawable.gradient));
        }

        Uri uri = Uri.parse(mThumbUri);

        tvAlbum.setText(uri.getPathSegments().get(uri.getPathSegments().size()-2));

        return v;
    }
}
