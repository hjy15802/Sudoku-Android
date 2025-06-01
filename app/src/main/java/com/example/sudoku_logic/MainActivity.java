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
    private SudokuBoard sudokuBoard;            // 스도쿠 보드 객체 (문제지 및 정답지 포함)

    private TextView[][] cellViews = new TextView[9][9];    // 9x9 보드의 각 셀을 나타내는 TextView 배열

    // 현재 선택된 셀의 행, 열 (-1이면 미선택)
    private int selectedRow = -1;
    private int selectedCol = -1;


    private Chronometer chronometerTimer;   // 게임 진행 타이머 (퍼즐 시작부터 경과 시간 측정)
    private boolean timerRunning = false;   // 타이머 작동 여부 (보드가 완료되면 false로 변경)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Chronometer 초기화 및 시작 (게임 시작 시점 기준)
        chronometerTimer = findViewById(R.id.chronometerTimer);
        chronometerTimer.setBase(SystemClock.elapsedRealtime());
        chronometerTimer.start();
        timerRunning = true;

        // 스도쿠 보드 및 버튼 초기화
        initBoard();
        initNumberButtons();
        initSpecialButtons();
    }

    // 스도쿠 보드의 생성
    private void initBoard() {
        // 퍼즐 생성
        SudokuGenerator generator = new SudokuGenerator();
        sudokuBoard = generator.generatePuzzle(Difficulty.MEDIUM);      //난이도 설정하는 부분

        // 보드를 담을 GridLayout 설정
        GridLayout gridLayout = findViewById(R.id.gridLayoutBoard);
        gridLayout.setBackgroundResource(R.drawable.board_background);
        gridLayout.setRowCount(9);
        gridLayout.setColumnCount(9);

        // 화면 밀도에 따른 서브그리드 여백 계산
        final float scale = getResources().getDisplayMetrics().density;
        int subgridMargin = (int) (2 * scale + 0.5f);

        // 9x9 셀 생성: 각 셀을 TextView로 만들어 GridLayout에 추가
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final int r = row;
                final int c = col;
                TextView cell = new TextView(this);
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(18);
                cell.setBackgroundResource(R.drawable.cell_border);

                // 셀 값 설정 (0이면 빈 칸)
                int value = sudokuBoard.getCell(row, col);
                cell.setText(value != 0 ? String.valueOf(value) : "");

                // 서브그리드 경계 여백 설정
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

                // 고정(힌트) 셀은 빨간색, 나머지 셀은 파란색으로 표시
                if (sudokuBoard.isFixed(row, col)) {
                    cell.setTextColor(Color.RED);
                } else {
                    cell.setTextColor(Color.BLUE);
                }

                // 셀 클릭 시 선택된 셀의 위치를 저장하고 하이라이트 처리
                cell.setOnClickListener(v -> {
                    selectedRow = r;
                    selectedCol = c;
                    highlightSelectedCell();
                });

                // TextView 배열에 저장 후 GridLayout에 추가
                cellViews[row][col] = cell;
                gridLayout.addView(cell);
            }
        }
    }

    // 숫자 입력 버튼의 초기화
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

    // 기능성 버튼의 초기화
    private void initSpecialButtons() {
        Button deleteButton = findViewById(R.id.buttonDelete);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> applyNumber(0)); // 0은 삭제 의미
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

    // 선택된 셀에 주어진 숫자(또는 0)를 적용하고 보드를 갱신
    // 만약 보드가 모두 채워졌으면 타이머를 중지하고,
    // 사용자 입력 대상 셀 총 개수, 틀린 셀 개수, 초기 고정 셀 개수를 계산한 후
    // ResultDialogFragment 오버레이를 표시
    private void applyNumber(int number) {
        // 셀이 선택되지 않았을 경우 경고 메시지
        if (selectedRow == -1 || selectedCol == -1) {
            Toast.makeText(this, "셀을 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 고정된(힌트) 셀은 수정 불가
        if (sudokuBoard.isFixed(selectedRow, selectedCol)) {
            Toast.makeText(this, "고정된 셀은 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 유효성 검사 후 셀 업데이트 및 적절한 Toast 메시지 출력
        boolean success = sudokuBoard.setCellWithValidation(selectedRow, selectedCol, number, this);
        if (success) {
            updateBoard();

            // 보드가 완성되었고 타이머가 작동 중이면 결과 오버레이 표시
            if (sudokuBoard.isBoardFull() && timerRunning) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometerTimer.getBase();
                float accuracy = sudokuBoard.getAccuracyPercentage();

                // 사용자 입력 대상 셀의 총 개수, 틀린 셀 개수, 초기 고정 셀 개수 계산
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

                // 타이머를 중지하고 진행 상태 업데이트
                chronometerTimer.stop();
                timerRunning = false;
                boolean solvedCorrectly = sudokuBoard.isSolvedCorrectly();

                // 결과 오버레이 다이얼로그에 6개의 데이터를 전달하여 표시
                ResultDialogFragment dialog = ResultDialogFragment.newInstance(
                        accuracy, elapsedMillis, solvedCorrectly, totalUserCells, wrongCount, fixedCount);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "ResultDialog");
            }
        }
    }

    private void updateBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = sudokuBoard.getCell(row, col);
                cellViews[row][col].setText(value != 0 ? String.valueOf(value) : "");
            }
        }
    }

    private void highlightSelectedCell() {
        // 모든 셀의 배경을 기본 테두리로 초기화
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cellViews[row][col].setBackgroundResource(R.drawable.cell_border);
            }
        }
        // 선택된 셀이 있을 경우 연한 파란색으로 하이라이트
        if (selectedRow != -1 && selectedCol != -1) {
            int selectedColor = Color.parseColor("#ADD8E6"); // 연한 파란색
            cellViews[selectedRow][selectedCol].setBackgroundColor(selectedColor);
        }
    }
}