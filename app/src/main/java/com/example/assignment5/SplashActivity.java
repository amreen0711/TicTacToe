package com.example.assignment5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.ObjectAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView appName = findViewById(R.id.appName);

        // animations
        Animation zoomAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_in_out);
        Animation moveAnim = AnimationUtils.loadAnimation(this, R.anim.move_left_right);
        logo.startAnimation(zoomAnim);
        appName.startAnimation(moveAnim);

        // moving bottom loader LEFT → RIGHT
        ImageView movingLogo = findViewById(R.id.movingLoader);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.move_left_to_right);
        movingLogo.startAnimation(anim);

        // dot progress layout
        LinearLayout dotContainer = findViewById(R.id.dotContainer);
        final int DOT_COUNT = 10;
        ImageView[] dots = new ImageView[DOT_COUNT];

        for (int i = 0; i < DOT_COUNT; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(lp);
            dot.setImageResource(R.drawable.dot_empty);
            dot.setScaleX(0.9f);
            dot.setScaleY(0.9f);
            dotContainer.addView(dot);
            dots[i] = dot;
        }

        TextView progressText = findViewById(R.id.progressText);

        Handler handler = new Handler();
        final int[] progress = {0};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progress[0] <= 100) {

                    progressText.setText(progress[0] + "%");

                    int filled = (int) Math.floor((progress[0] / 100.0) * DOT_COUNT);

                    for (int i = 0; i < DOT_COUNT; i++) {
                        if (i < filled) {
                            if (dots[i].getTag() == null) {
                                dots[i].setTag("filled");
                                dots[i].setImageResource(R.drawable.dot_filled);

                                ObjectAnimator sx = ObjectAnimator.ofFloat(dots[i], "scaleX", 0.4f, 1.1f);
                                ObjectAnimator sy = ObjectAnimator.ofFloat(dots[i], "scaleY", 0.4f, 1.1f);
                                sx.setDuration(300);
                                sy.setDuration(300);
                                sx.setInterpolator(new AccelerateDecelerateInterpolator());
                                sy.setInterpolator(new AccelerateDecelerateInterpolator());
                                sx.start();
                                sy.start();
                            }
                        } else {
                            dots[i].setImageResource(R.drawable.dot_empty);
                            dots[i].setTag(null);
                        }
                    }

                    progress[0]++;
                    handler.postDelayed(this, 50);
                }
            }
        };

        handler.post(runnable);

        // After 5 sec → MainActivity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 8000);
        Animation dance = AnimationUtils.loadAnimation(this, R.anim.title_dance);
        appName.startAnimation(dance);

    }
}
