package com.stefanlippl.hangover.loadingscreen;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import com.stefanlippl.hangover.R;
import com.stefanlippl.hangover.events.EventItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadEventImageTask extends AsyncTask<String, Void, Bitmap> {

    OnEventDownloadFinishedListener listener;
    Context context;

    private String id;
    private String headline;
    private String date;
    private String ima;
    private String info;
    private String locationId;
    private String music;
    private String price;
    private String time;

    DownloadEventImageTask(OnEventDownloadFinishedListener listener, String id, String headline, String date, String ima, String info, String locationId, String music, String price, String time, Context context) {
        this.listener = listener;
        this.id = id;
        this.headline = headline;
        this.date = date;
        this.ima = ima;
        this.info = info;
        this.locationId = locationId;
        this.music = music;
        this.price = price;
        this.time = time;
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
        listener.onEventUpdate();
    }

    protected void onPostExecute(Bitmap result) {
        listener.onEventDownloadFinished(new EventItem(context,id,R.drawable.ho_logo_full,headline,date,time,locationId,ima,info,Double.parseDouble(price),music,saveImage(context,result).toString()));
    }

    private Uri saveImage(Context context, Bitmap image){
        String imageName = id + locationId + ".jpg";
        ContextWrapper wrapper = new ContextWrapper(context);
        File file = wrapper.getDir("Images",Context.MODE_PRIVATE);
        file = new File(file,imageName);
        try {
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG,80,stream);
            stream.flush();
            stream.close();
        }catch (Exception e){}

        Uri imageUri = Uri.parse(file.getAbsolutePath());
        return imageUri;
    }

}
