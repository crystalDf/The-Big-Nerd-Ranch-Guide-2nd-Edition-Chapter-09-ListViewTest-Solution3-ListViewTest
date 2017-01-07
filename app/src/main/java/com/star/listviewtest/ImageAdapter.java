package com.star.listviewtest;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class ImageAdapter extends ArrayAdapter<String> {

    private ImageLoader mImageLoader;

    public ImageAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(requestQueue, new BitmapCache());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String currentUrl = getItem(position);

        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.image_item, null);
        } else {
            view = convertView;
        }

        NetworkImageView networkImageView = (NetworkImageView)
                view.findViewById(R.id.image_view);

        networkImageView.setDefaultImageResId(R.drawable.empty_photo);
        networkImageView.setErrorImageResId(R.drawable.empty_photo);
        networkImageView.setImageUrl(currentUrl, mImageLoader);

        return view;
    }

    private class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mMemoryCache;

        public BitmapCache() {
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
        }

        @Override
        public Bitmap getBitmap(String s) {
            return mMemoryCache.get(s);
        }

        @Override
        public void putBitmap(String s, Bitmap bitmap) {
            if (mMemoryCache.get(s) == null) {
                mMemoryCache.put(s, bitmap);
            }
        }
    }

}
