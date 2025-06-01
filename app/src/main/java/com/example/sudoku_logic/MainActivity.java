package com.example.sudoku_logic;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sudoku_logic.logic.Difficulty;
import com.example.sudoku_logic.logic.SudokuBoard;
import com.example.sudoku_logic.logic.SudokuGenerator;

public class MainActivity extends AppCompatActivity {

    // 스도쿠 보드 관련 변수
    private SudokuBoard sudokuBoard;
    private TextView[][] cellViews = new TextView[9][9];

    // 현재 선택된 셀의 행, 열 (선택되지 않은 경우 -1)
    private int selectedRow = -1;
    private int selectedCol = -1;

    // 타이머용 Chronometer
    private Chronometer chronometerTimer;
    private boolean timerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Chronometer 시작 (퍼즐 시작 시점을 기준)
        chronometerTimer = findViewById(R.id.chronometerTimer);
        chronometerTimer.setBase(SystemClock.elapsedRealtime());
        chronometerTimer.start();
        timerRunning = true;

        // 스도쿠 보드와 버튼 초기화
        initBoard();
        initNumberButtons();
        initSpecialButtons();
    }

    /**
     * 9x9 스도쿠 보드를 생성 및 초기화합니다.
     */
    private void initBoard() {
        SudokuGenerator generator = new SudokuGenerator();
        sudokuBoard = generator.generatePuzzle(Difficulty.MEDIUM);

        GridLayout gridLayout = findViewById(R.id.gridLayoutBoard);
        gridLayout.setBackgroundResource(R.drawable.board_background);
        gridLayout.setRowCount(9);
        gridLayout.setColumnCount(9);

        final float scale = getResources().getDisplayMetrics().density;
        int subgridMargin = (int) (2 * scale + 0.5f);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final int r = row;
                final int c = col;
                TextView cell = new TextView(this);
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(18);
                cell.setBackgroundResource(R.drawable.cell_border);

                int value = sudokuBoard.getCell(row, col);
                cell.setText(value != 0 ? String.valueOf(value) : "");

                // 서브그리드 경계에 여백 설정
                int leftMargin = (col % 3 == 0 && col != 0) ? subgridMargin : 0;
                int topMargin = (row % 3 == 0 && row != 0) ? subgridMargin : 0;
                int rightMargin = (col == 8) ? 0 : (col % 3 == 2 ? subgridMargin : 0);
                int bottomMargin = (row == 8) ? 0 : (row % 3 == 2 ? subgridMargin : 0);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row, 1, 1f);
                params.columnSpec = GridLayout.spec(col, 1, 1f);
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                cell.setLayoutParams(params);

                // 고정 셀은 붉은색, 사용자가 입력한 셀은 파란색
                if (sudokuBoard.isFixed(row, col)) {
                    cell.setTextColor(Color.RED);
                } else {
                    cell.setTextColor(Color.BLUE);
                }

                cell.setOnClickListener(v -> {
                    selectedRow = r;
                    selectedCol = c;
                    highlightSelectedCell();
                });

                cellViews[row][col] = cell;
                gridLayout.addView(cell);
            }
        }
    }

    /**
     * 1~9 숫자 버튼을 초기화합니다.
     */
    private void initNumberButtons() {
        for (int num = 1; num <= 9; num++) {
            int resId = getResources().getIdentifier("button" + num, "id", getPackageName());
            Button numButton = findViewById(resId);
            if (numButton != null) {
                final int digit = num;
                numButton.setOnClickListener(v -> applyNumber(digit));
            }
        }
    }

    /**
     * 삭제 버튼과 선택 해제 버튼을 초기화합니다.
     */
    private void initSpecialButtons() {
        Button deleteButton = findViewById(R.id.buttonDelete);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> {
                applyNumber(0);  // 0은 삭제를 의미합니다.
            });
        }

        Button deselectButton = findViewById(R.id.buttonDeselect);
        if (deselectButton != null) {
            deselectButton.setOnClickListener(v -> {
                selectedRow = -1;
                selectedCol = -1;
                highlightSelectedCell();
            });
        }
    }

    /**
     * 선택된 셀에 주어진 숫자(또는 0)를 적용한 후 보드를 갱신합니다.
     * 보드가 완성되면 타이머를 중지하고, 정답 여부에 따라 ResultDialogFragment(오버레이 다이얼로그)를 표시합니다.
     */
    private void applyNumber(int number) {
        if (selectedRow == -1 || selectedCol == -1) {
            Toast.makeText(this, "셀을 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sudokuBoard.isFixed(selectedRow, selectedCol)) {
            Toast.makeText(this, "고정된 셀은 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = sudokuBoard.setCellWithValidation(selectedRow, selectedCol, number, this);
        if (success) {
            updateBoard();

            // 보드가 모두 채워졌으면 결과 오버레이를 표시합니다.
            if (sudokuBoard.isBoardFull() && timerRunning) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometerTimer.getBase();
                float accuracy = sudokuBoard.getAccuracyPercentage();

                // 사용자 입력 대상 셀의 총 개수(totalUserCells), 틀린 셀의 개수(wrongCount), 그리고 초기 고정 셀의 개수(fixedCount) 계산
                int totalUserCells = 0;
                int wrongCount = 0;
                int fixedCount = 0;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (sudokuBoard.isFixed(i, j)) {
                            fixedCount++;
                        } else {
                            totalUserCells++;
                            if (sudokuBoard.getCell(i, j) != sudokuBoard.getSolutionBoard().getCell(i, j)) {
                                wrongCount++;
                            }
                        }
                    }
                }

                // 타이머 중지
                chronometerTimer.stop();
                timerRunning = false;
                boolean solvedCorrectly = sudokuBoard.isSolvedCorrectly();

                // ResultDialogFragment.newInstance()는 (accuracy, elapsedMillis, solvedCorrectly, totalUserCells, wrongCount, fixedCount) 인자를 받습니다.
                ResultDialogFragment dialog = ResultDialogFragment.newInstance(accuracy, elapsedMillis, solvedCorrectly, totalUserCells, wrongCount, fixedCount);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "ResultDialog");
            }
        }
    }

    /**
     * 현재 보드의 상태를 업데이트합니다.
     */
    private void updateBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = sudokuBoard.getCell(row, col);
                cellViews[row][col].setText(value != 0 ? String.valueOf(value) : "");
            }
        }
    }

    /**
     * 선택된 셀에 하이라이트 효과를 적용합니다.
     */
    private void highlightSelectedCell() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cellViews[row][col].setBackgroundResource(R.drawable.cell_border);
            }
        }
        if (selectedRow != -1 && selectedCol != -1) {
            int selectedColor = Color.parseColor("#ADD8E6"); // 연한 파란색으로 하이라이트
            cellViews[selectedRow][selectedCol].setBackgroundColor(selectedColor);
        }
    }

}