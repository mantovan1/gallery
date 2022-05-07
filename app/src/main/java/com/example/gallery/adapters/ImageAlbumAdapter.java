package com.example.gallery.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gallery.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAlbumAdapter extends BaseAdapter {

    private ArrayList<String> mThumbUris;
    private Context mContext;

    public ImageAlbumAdapter(ArrayList <String> mThumbUris, Context mContext) {
        this.mThumbUris = mThumbUris;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mThumbUris.size();
    }

    @Override
    public String getItem(int position) {
        return mThumbUris.get(position);
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

       Glide.with(mContext)
               .load(getItem(position))
               .centerCrop()
               .override(500)
               .into(ivAlbum);

       return v;
    }
}

