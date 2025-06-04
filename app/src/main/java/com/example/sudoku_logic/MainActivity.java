package com.example.sudoku_logic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.Level);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup levelGroup = findViewById(R.id.levelGroup);
                int selectedId = levelGroup.getCheckedRadioButtonId();

                String difficulty = "EASY";
                if (selectedId == R.id.medium) {
                    difficulty = "MEDIUM";
                } else if (selectedId == R.id.hard) {
                    difficulty = "HARD";
                }

                Intent intent = new Intent(MainActivity.this, Puzzle.class);
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            }
        });
    }
}
