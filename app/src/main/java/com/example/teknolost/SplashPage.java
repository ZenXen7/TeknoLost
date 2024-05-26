package com.example.teknolost;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashPage extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_page);

        ImageView imageView = findViewById(R.id.imageView4);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView.startAnimation(fadeInAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mainIntent = new Intent(SplashPage.this, Login.class);
                SplashPage.this.startActivity(mainIntent);
                SplashPage.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
