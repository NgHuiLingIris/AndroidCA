package com.lwx.ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DownloadService.Callback {
    DownloadService svc;
    private int count;
    protected String urls=null;
    ProgressBar bar = null;
    int[] drawableMemoryGame = new int[0];
    final int MEMORYGAME_CODE=100;
    final int DOWNLOAD_CODE=99;

    final int[] drawable=new int[]{ R.id.imageView,R.id.imageView2,R.id.imageView3,R.id.imageView4,R.id.imageView5
            ,R.id.imageView6,R.id.imageView7,R.id.imageView8,R.id.imageView9,R.id.imageView10
            ,R.id.imageView11,R.id.imageView12,R.id.imageView13
            ,R.id.imageView14,R.id.imageView15,R.id.imageView16
            ,R.id.imageView17,R.id.imageView18,R.id.imageView19,R.id.imageView20};


    final String [] url=new String[]{"/1.jpg","/2.jpg","/3.jpg","/4.jpg","/5.jpg",
            "/6.jpg","/7.jpg","/8.jpg","/9.jpg","/10.jpg","/11.jpg","/12.jpg",
            "/13.jpg","/14.jpg","/15.jpg","/16.jpg","/17.jpg","/18.jpg","/19.jpg","/20.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, svcConn, BIND_AUTO_CREATE);
        Button fetch = findViewById(R.id.fetch);
        EditText search=findViewById(R.id.urlInput);
        search.setOnClickListener(this);
        fetch.setOnClickListener(this);
        urls=search.getText().toString();
    }

    @Override
    public void onClick(View view) {
/*        Intent download = new Intent(this, DownloadService.class);
        startActivityForResult(download, DOWNLOAD_CODE);*/

        switch(view.getId()){
            case R.id.fetch:
                svc.scraper(urls);
                break;
        }
    }
    protected void onActivity(int requestCode, int resultCode,Intent data){//data is passed from elsewhere
        switch(requestCode){
            case MEMORYGAME_CODE:
                int[] memoryImages = data.getIntArrayExtra("images");
                Intent done = new Intent(this,MemoryGame.class);//intent shows where i want tog o
                done.putExtra("images",memoryImages);
                startActivity(done);
                break;
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DOWNLOAD_CODE:
                svc.scraper("https://stocksnap.io/");
                Intent selectImages = new Intent(this, MainActivity.class);
                startActivityForResult(selectImages, MEMORYGAME_CODE);
                break;

            case MEMORYGAME_CODE:
                int[] memoryImages = data.getIntArrayExtra("memoryGameImages");
                Intent done = new Intent(this, MemoryGame.class);
                done.putExtra("memoryGameImages", memoryImages);
                startActivity(done);
                break;
        }
    }*/




    @Override
    public void callback(String... images) {
        String[] imgs = new String[20];
        int c = 0;
        String[] content = images[0].split("<");
        for(int i = 0; i < content.length; i++){
            if((content[i].contains("img src=")) && (content[i].contains("cdn.stocksnap.io"))){
                String img_link = content[i].split("\"")[1];
                if(c < 20){
                    imgs[c] = img_link;
                    c++;
                }
                else {
                    break;
                }
            }
        }
        downloadImages(imgs);
    }

    @Override
    public void publishProgress() {
        count += 1;
        final int i = count;
        for(int z=0;z<drawable.length;z++)
        {

            ImageView img = findViewById(drawable[z]);
            img.setVisibility(View.GONE);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ProgressBar bar = findViewById(R.id.progressBar);
                final TextView progresstext = findViewById(R.id.progressText);
                int increment = 5;
                int current = bar.getProgress();
                int total = increment + current;
                System.out.println("Total: " + total);
                bar.setProgress(total);
                progresstext.setText("Downloading " + i + " of 20 images...");
                if(i == 20){
                    bar.setVisibility(View.GONE);
                    progresstext.setVisibility(View.GONE);

                }
                for(int a=0;a<i;a++)
                {
                    ImageView img = findViewById(drawable[a]);
                    img.setVisibility(View.VISIBLE);

                }

            }
        });
    }

    private void downloadImages(String[] images){
        for(int i = 0; i < images.length; i++){
            String fn = getFilesDir() + "/" + (i + 1) + ".jpg";
            svc.DownloadImage(i, images[i], fn, (i + 1) + "");
            displayImage(drawable[i],url[i]);
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void displayImage(int drawable,String url)
    {
        try {
                File file = new File(getFilesDir() + url);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                ImageView img = findViewById(drawable);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void displayImages(int[] drawable,String[] url)
    {
        try {
            for (int j = 0, i = 0; j < url.length; j++) {
                File file = new File(getFilesDir() + url[j]);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                ImageView img = findViewById(drawable[i]);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);
                i++;

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    //storing images
    int memoryImageCount;

    public void Image_Select1(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 1;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select2(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 2;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select3(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 3;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select4(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 4;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select5(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 51;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select6(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 6;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select7(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 7;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select8(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 8;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select9(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 9;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select10(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 10;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select11(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 11;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select12(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 12;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select13(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 13;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select14(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 14;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select15(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 15;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select16(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 16;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select17(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 17;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select18(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 18;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select19(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 19;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }
    public void Image_Select20(View imageview) {
        System.out.println("saving image "+imageview.getId());
        int newSize = drawableMemoryGame.length+1;
        int[] temp = new int[newSize];
        for(int i = 0;i<drawableMemoryGame.length;i++)
        {
            temp[i]=drawableMemoryGame[i];
        }
        drawableMemoryGame = temp;
        drawableMemoryGame[memoryImageCount] = 20;
        memoryImageCount++;
        System.out.println("memoryimagecount "+memoryImageCount);
        if(memoryImageCount==6){
            System.out.println("memoryimagecount is 6");

            Intent playGame = new Intent(this, MemoryGame.class);
            playGame.putExtra("images",drawableMemoryGame);
            startActivityForResult(playGame, MEMORYGAME_CODE);
        }
    }


    ServiceConnection svcConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) iBinder;
            if(binder != null){
                svc = binder.getService();
                svc.setCallback(MainActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            svc = null;
        }
    };

}
