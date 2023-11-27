package com.andromou.taabia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class SplashActivity extends AppCompatActivity {
    ImageView imageshine, sp_imag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      compart();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_splash);
       // sp_imag = findViewById(R.id.sp_imag);
 /*       ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgshine();
                    }
                });

            }
        }, 1,1, TimeUnit.SECONDS);*/


        Thread timer = new Thread(){
            public void run() {
                try {
                    sleep(2000);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                }
            }

        };
        timer.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    public void compart(){
        String s = "kot";
        if(!getPackageName().equals("kotkit.snap.ta"+"abia")){
            String error = null;
            error.getBytes();
        }
    }





}