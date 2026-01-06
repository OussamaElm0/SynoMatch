package com.example.synomatch;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.synomatch.data.SynonymsRepository;
import com.example.synomatch.quiz.QuizEngine;
import com.example.synomatch.quiz.QuizItem;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    // Logic Variables
    private List<QuizItem> quizList;
    private int currentIndex = 0;
    private int score = 0;
    private int totalQuestions = 0;
    private boolean isAnswered = false;

    // UI Variables
    private MediaPlayer correctSound, wrongSound;
    private TextView wordTextView, scoreTextView;
    private Button[] optionButtons;

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

        scoreTextView = findViewById(R.id.scoreTextView);
        wordTextView = findViewById(R.id.wordTextView);
        optionButtons = new Button[]{
                findViewById(R.id.option1Btn),
                findViewById(R.id.option2Btn),
                findViewById(R.id.option3Btn),
                findViewById(R.id.option4Btn)
        };

        correctSound = MediaPlayer.create(this, R.raw.correct_sound);
        wrongSound = MediaPlayer.create(this, R.raw.wrong_sound);

        totalQuestions = getIntent().getIntExtra("MODE", 10);
        SynonymsRepository repo = new SynonymsRepository(this);
        QuizEngine engine = new QuizEngine(repo);
        quizList = engine.generate(totalQuestions);

        if (quizList.isEmpty()){
            Toast.makeText(getApplicationContext(), "Données insuffisantes pour générer le quiz !", Toast.LENGTH_LONG)
                    .show();
            finish();
            return;
        }

        loadQuestion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        correctSound.release();
        wrongSound.release();
    }


    private void loadQuestion() {
        if (currentIndex >= quizList.size()) {
            showGameOver();
            return;
        }

        QuizItem currentItem = quizList.get(currentIndex);
        isAnswered = false;

        // Set Base Word
        wordTextView.setText(currentItem.baseWord);

        // Set Buttons
        for (int i = 0; i < 4; i++) {
            Button btn = optionButtons[i];

            btn.setBackgroundColor(getResources().getColor(R.color.frozen_water));
            btn.setTextColor(getResources().getColor(R.color.blue_green));

            if (i < currentItem.options.size()) {
                btn.setText(currentItem.options.get(i));
                btn.setVisibility(View.VISIBLE);

                btn.setOnClickListener(v -> checkAnswer(btn, currentItem.correctAnswer));
            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }
    private void checkAnswer(Button selectedBtn, String correctAnswer) {
        if (isAnswered) return;
        isAnswered = true;

        String selectedText = selectedBtn.getText().toString();
        selectedBtn.setTextColor(Color.WHITE);

        if (selectedText.equals(correctAnswer)) {
            score++;
            scoreTextView.setText("Score: " + score);

            selectedBtn.setBackgroundColor(Color.GREEN);
            if (correctSound != null) correctSound.start();
        } else {
            selectedBtn.setBackgroundColor(Color.RED);
            if (wrongSound != null) wrongSound.start();

            for (Button btn : optionButtons) {
                if (btn.getText().toString().equals(correctAnswer)) {
                    btn.setBackgroundColor(Color.GREEN);
                    btn.setTextColor(Color.WHITE);
                }
            }
        }

        selectedBtn.postDelayed(() -> {
            currentIndex++;
            loadQuestion();
        }, 1000);
    }

    private void showGameOver() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Your Score: " + score + " / " + totalQuestions)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}