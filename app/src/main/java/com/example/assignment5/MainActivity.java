package com.example.assignment5;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etPlayer1, etPlayer2;
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        etPlayer1 = findViewById(R.id.etPlayer1);
        etPlayer2 = findViewById(R.id.etPlayer2);
        btnStart = findViewById(R.id.btnStart);

        // Start button click
        btnStart.setOnClickListener(v -> {

            String p1 = etPlayer1.getText().toString().trim();
            String p2 = etPlayer2.getText().toString().trim();

            if (p1.isEmpty()) p1 = "Player 1";
            if (p2.isEmpty()) p2 = "Player 2";

            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("player1", p1);
            i.putExtra("player2", p2);
            startActivity(i);

            // Smooth transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        TextView title = findViewById(R.id.titleText);
        Animation dance = AnimationUtils.loadAnimation(this, R.anim.title_dance);
        title.startAnimation(dance);
        btnStart.startAnimation(dance);


    }
}
