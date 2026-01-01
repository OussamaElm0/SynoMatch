package com.example.synomatch;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn10Mots = findViewById(R.id.btn10Mots);
        Button btn20Mots = findViewById(R.id.btn20Mots);

        btn10Mots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSynonymsActivity(10);
            }
        });

        btn20Mots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                goToSynonymsActivity(20);
            }
        });
    }

    private void goToSynonymsActivity(int synonymesNumber) {
        Log.d("GO TO SYNONYMS ACTIVITY", "Synonyms number: " + synonymesNumber );
    }
}