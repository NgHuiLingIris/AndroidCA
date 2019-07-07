package com.lwx.ca;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadService extends Service {

    private Callback callback;
    private IBinder binder = new LocalBinder();



    public class LocalBinder extends Binder {
        DownloadService getService(){
            return DownloadService.this;
        }
    }

    public DownloadService() {
    }

    public void setCallback(DownloadService.Callback callback){
        this.callback = callback;
    }

    public interface Callback {
        void callback(String...images);
        void publishProgress();
    }
    public interface ServiceCallback {
        void svcToActivity(int percent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }

    public void scraper(final String href){
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();

                try{
                    String line = "";
                    URL url = new URL(href);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    while((line = br.readLine()) != null){
                        sb.append(line);
                    }

                    callback.callback(sb.toString());
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void DownloadImage(int b, String... s){
        final int c = b;
        final String href = s[0];
        final String fn = s[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int readLen = 0;
                    URL url = new URL(href);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    byte[] data = new byte[1024];

                    InputStream in = url.openStream();
                    BufferedInputStream bufIn = new BufferedInputStream(in, 2048);
                    OutputStream out = new FileOutputStream(fn);

                    while ((readLen = bufIn.read(data)) != -1) {
                        out.write(data, 0, readLen);
                    }

                    callback.publishProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
