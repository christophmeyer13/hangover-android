package com.stefanlippl.hangover.loadingscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.locations.LocationItem;
import com.stefanlippl.hangover.utils.ImageHelper;

import java.io.InputStream;

public class DownloadLocationImageTask extends AsyncTask<String, Void, Bitmap> {

    OnLocationDownloadFinishedListener listener;

    private String id;
    private String name;
    private String street;
    private String houseNumber;
    private String zip;
    private String city;
    private String type;
    private String ima;
    private String info;
    private String[] openFrom;
    private String[] openUntil;
    private Context context;

    public DownloadLocationImageTask(OnLocationDownloadFinishedListener listener, String id, String name, String street, String houseNumber, String zip, String city, String type, String ima, String info, String[] openFrom, String[] openUntil, Context context) {
        this.listener = listener;
        this.id = id;
        this.name = name;
        this.street = street;
        this.houseNumber = houseNumber;
        this.zip = zip;
        this.city = city;
        this.type = type;
        this.ima = ima;
        this.info = info;
        this.openFrom = openFrom;
        this.openUntil = openUntil;
        this.context = context;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        if (!urldisplay.equals("")) {
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress();
            return bmp;
        } else {
            publishProgress();
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        listener.onLocationUpdate();
    }

    protected void onPostExecute(Bitmap result) {
        Bitmap image = null;
        if (result != null) {
            image = ImageHelper.resizeImage(result, 100);
        }
        listener.onLocationDownloadFinished(new LocationItem(id, R.drawable.ho_logo, name, street, houseNumber, zip, city, type, ima, info, openFrom, openUntil, context, image));
    }
}
