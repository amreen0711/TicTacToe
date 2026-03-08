package com.example.assignment5;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.animation.ValueAnimator;
import android.animation.ArgbEvaluator;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    Button[][] buttons = new Button[3][3];
    boolean player1Turn = true;
    int roundCount = 0;
    int player1Points = 0;
    int player2Points = 0;
    String player1, player2;
    TextView tvInfo;
    SharedPreferences prefs;

    // Guards to prevent double counting / extra taps
    private boolean gameOver = false;
    private boolean statsUpdated = false;

    // Winning positions array
    private int[][] winningPositions = new int[3][2];

    // Which player won last (true -> player1 (X), false -> player2 (O))
    private boolean lastWinnerIsPlayer1 = false;
    MediaPlayer soundX,soundO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
         soundX = MediaPlayer.create(this, R.raw.popx);
         soundO = MediaPlayer.create(this, R.raw.popy);

        player1 = getIntent().getStringExtra("player1");
        player2 = getIntent().getStringExtra("player2");
        tvInfo = findViewById(R.id.tvInfo);
        prefs = getSharedPreferences("tictactoe_stats", MODE_PRIVATE);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "btn_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }
        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetBoard());
        updateInfo();
    }

    @Override
    public void onClick(View v) {
        // Prevent clicks after game finished until reset
        if (gameOver) return;

        // Play click animation
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_click);
        v.startAnimation(anim);

        Button btn = (Button) v;

        if (player1Turn) {
            soundX.start();
            btn.setText("X");
            btn.setTextColor(ContextCompat.getColor(this, R.color.player1_text));  // X color
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.player1_bg)); // X background
        } else {
            soundO.start();
            btn.setText("O");
            btn.setTextColor(ContextCompat.getColor(this, R.color.player2_text));  // O color
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.player2_bg)); // O background
        }

        roundCount++;

        if (checkForWin()) {
            // Mark game over to block further clicks
            gameOver = true;
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            gameOver = true;
            draw();
        } else {
            player1Turn = !player1Turn;
            updateInfo();
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        // Rows
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) &&
                    field[i][0].equals(field[i][2]) &&
                    !field[i][0].equals("")) {

                winningPositions[0] = new int[]{i, 0};
                winningPositions[1] = new int[]{i, 1};
                winningPositions[2] = new int[]{i, 2};
                return true;
            }
        }

        // Columns
        for (int col = 0; col < 3; col++) {
            if (field[0][col].equals(field[1][col]) &&
                    field[0][col].equals(field[2][col]) &&
                    !field[0][col].equals("")) {

                winningPositions[0] = new int[]{0, col};
                winningPositions[1] = new int[]{1, col};
                winningPositions[2] = new int[]{2, col};
                return true;
            }
        }

        // Diagonal 1
        if (field[0][0].equals(field[1][1]) &&
                field[0][0].equals(field[2][2]) &&
                !field[0][0].equals("")) {

            winningPositions[0] = new int[]{0, 0};
            winningPositions[1] = new int[]{1, 1};
            winningPositions[2] = new int[]{2, 2};
            return true;
        }

        // Diagonal 2
        if (field[0][2].equals(field[1][1]) &&
                field[0][2].equals(field[2][0]) &&
                !field[0][2].equals("")) {

            winningPositions[0] = new int[]{0, 2};
            winningPositions[1] = new int[]{1, 1};
            winningPositions[2] = new int[]{2, 0};
            return true;
        }

        return false;
    }

    private void playWinAnimation() {
        // choose color based on winner
        int winColor = lastWinnerIsPlayer1
                ? ContextCompat.getColor(this, android.R.color.holo_blue_light)
                : ContextCompat.getColor(this, android.R.color.holo_red_light);

        Animation glow = AnimationUtils.loadAnimation(this, R.anim.win_glow);

        for (int i = 0; i < 3; i++) {
            int r = winningPositions[i][0];
            int c = winningPositions[i][1];
            buttons[r][c].setBackgroundColor(winColor);
            buttons[r][c].startAnimation(glow);
        }
    }

    private void player1Wins() {
        // Guard so updateStats runs once
        if (!statsUpdated) {
            updateStats(player1, true);
            statsUpdated = true;
        }
        lastWinnerIsPlayer1 = true;
        playWinAnimation();
        player1Points++;
        showResultDialog(player1 + " wins!");
    }

    private void player2Wins() {
        if (!statsUpdated) {
            updateStats(player2, true);
            statsUpdated = true;
        }
        lastWinnerIsPlayer1 = false;
        playWinAnimation();
        player2Points++;
        showResultDialog(player2 + " wins!");
    }

    private void draw() {
        if (!statsUpdated) {
            updateStats(null, false);
            statsUpdated = true;
        }
        showResultDialog("Draw!");
    }

    private void showResultDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Emoji rain effect inside message
        String winnerMessage =
                "🎉✨ Congratulations! ✨🎉\n" +
                        message + "\n" +
                        "You played amazingly well! 🏆\n\n" +
                        "🎊🎊🎊";

        builder.setTitle(message);
        builder.setMessage(winnerMessage + "\n" + getStatsText());

        builder.setPositiveButton("Play Again", (d, w) -> resetBoard());
        builder.setNegativeButton("Exit", (d, w) -> finish());

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogZoomAnimation;
        }

        dialog.setOnShowListener(show -> {

            // 🔥 1. Fade + Scale animation on whole dialog
            View root = dialog.getWindow().getDecorView();
            Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.dialog_zoom_in);
            root.startAnimation(zoomIn);

            // 🔥 2. Background color glow animation
            int colorFrom = Color.WHITE;
            int colorTo = Color.parseColor("#FF66CC"); // neon pink glow
            ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

            colorAnim.addUpdateListener(anim ->
                    dialog.getWindow().setBackgroundDrawable(
                            new ColorDrawable((int) anim.getAnimatedValue())
                    )
            );

            colorAnim.setDuration(900);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.start();

            // 🔥 3. Title shimmer effect
            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
            TextView titleView = dialog.findViewById(titleId);

            if (titleView != null) {
                titleView.setTextColor(Color.WHITE);
                titleView.setTextSize(23f);
                titleView.setShadowLayer(8, 0, 0, Color.BLACK);

                Animation shimmer = AnimationUtils.loadAnimation(this, R.anim.shimmer_text);
                titleView.startAnimation(shimmer);
            }

            // 🔥 4. Buttons bounce animation
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            Animation bounce = AnimationUtils.loadAnimation(this, R.anim.button_bounce);

            if (btnPositive != null) btnPositive.startAnimation(bounce);
            if (btnNegative != null) btnNegative.startAnimation(bounce);
        });

        dialog.show();
    }


    private String getStatsText() {
        int p1wins = prefs.getInt(player1 + "_wins", 0);
        int p1loss = prefs.getInt(player1 + "_losses", 0);
        int p1plays = prefs.getInt(player1 + "_plays", 0);
        int p2wins = prefs.getInt(player2 + "_wins", 0);
        int p2loss = prefs.getInt(player2 + "_losses", 0);
        int p2plays = prefs.getInt(player2 + "_plays", 0);

        return player1 + ": wins=" + p1wins + ", losses=" + p1loss + ", plays=" + p1plays + "\n" +
                player2 + ": wins=" + p2wins + ", losses=" + p2loss + ", plays=" + p2plays;
    }

    private void updateStats(String winner, boolean isWin) {
        SharedPreferences.Editor e = prefs.edit();
        // increment plays for both players
        e.putInt(player1 + "_plays", prefs.getInt(player1 + "_plays", 0) + 1);
        e.putInt(player2 + "_plays", prefs.getInt(player2 + "_plays", 0) + 1);
        if (winner == null) {
            // draw: no wins/losses change
        } else if (winner.equals(player1)) {
            e.putInt(player1 + "_wins", prefs.getInt(player1 + "_wins", 0) + 1);
            e.putInt(player2 + "_losses", prefs.getInt(player2 + "_losses", 0) + 1);
        } else {
            e.putInt(player2 + "_wins", prefs.getInt(player2 + "_wins", 0) + 1);
            e.putInt(player1 + "_losses", prefs.getInt(player1 + "_losses", 0) + 1);
        }
        e.apply();
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].clearAnimation();
                buttons[i][j].setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            }
        }
        roundCount = 0;
        player1Turn = true;
        gameOver = false;
        statsUpdated = false;
        updateInfo();
    }

    private void updateInfo() {
        Animation dance = AnimationUtils.loadAnimation(this, R.anim.title_dance);
        tvInfo.startAnimation(dance);
        if (player1Turn) {
            tvInfo.setText(player1 + "'s turn (X)");
            tvInfo.setTextColor(ContextCompat.getColor(this, R.color.player1_text)); // Blue for Player 1
        } else {
            tvInfo.setText(player2 + "'s turn (O)");
            tvInfo.setTextColor(ContextCompat.getColor(this, R.color.player2_text)); // Red for Player 2
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundX != null) soundX.release();
        if (soundO != null) soundO.release();
    }

}
