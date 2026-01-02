package com.example.synomatch;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuestionActivity extends AppCompatActivity {
    int score;
    MediaPlayer correctSound;
    MediaPlayer wrongSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        correctSound = MediaPlayer.create(this, R.raw.correct_sound);
        wrongSound = MediaPlayer.create(this, R.raw.wrong_sound);

        int mode = getIntent().getIntExtra("MODE", 0);
        this.score = 0;

        Button option1Btn = findViewById(R.id.option1Btn);
        Button option2Btn = findViewById(R.id.option2Btn);
        Button option3Btn = findViewById(R.id.option3Btn);
        Button option4Btn = findViewById(R.id.option4Btn);

        TextView wordTextView = findViewById(R.id.wordTextView);

        wordTextView.setText("Capable");
        String correctAnswer = "Adroit";

        option1Btn.setText("Antienne");
        option2Btn.setText("Adroit");
        option3Btn.setText("Détenu");
        option4Btn.setText("Armature");

        Button[] optionButtons =
                {option1Btn, option2Btn, option3Btn, option4Btn};

        for (Button btn: optionButtons) {
            btn.setOnClickListener(v -> checkAnswer(btn, correctAnswer));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        correctSound.release();
        wrongSound.release();
    }


    private void checkAnswer(Button btn, String correctAnswer) {
        String answer = btn.getText().toString();

        if (answer.equals(correctAnswer)){
            correctSound.start();
            this.score++;

            btn.setBackgroundColor(Color.GREEN);
            btn.setTextColor(Color.WHITE);
        } else {
            wrongSound.start();

            btn.setBackgroundColor(Color.RED);
            btn.setTextColor(Color.WHITE);
        }
    }
}