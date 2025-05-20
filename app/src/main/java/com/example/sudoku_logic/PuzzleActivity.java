package com.example.sudoku_logic;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.sudoku_logic.logic.SudokuBoard;
import com.example.sudoku_logic.logic.SudokuGenerator;
import com.example.sudoku_logic.logic.Difficulty;

public class PuzzleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        TextView sudokuTextView = findViewById(R.id.sudokuTextView);

        // 퍼즐 생성 및 출력
        SudokuGenerator generator = new SudokuGenerator();      // SudokuGenerator 객체 생성
        SudokuBoard puzzle = generator.generatePuzzle(Difficulty.HARD); // 난이도가 선택 되는 부분

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int value = puzzle.getCell(i, j);                   // 값 가져와서 넣기
                sb.append(value == 0 ? "." : value).append(" ");    // 빈 칸은 . 로
            }
            sb.append("\n");
        }

        sudokuTextView.setText(sb.toString());
    }
}
