package com.example.sudoku_logic.logic;

import android.content.Context;
import android.widget.Toast;

public class SudokuBoard {
    private int[][] board;      // 현재 스도쿠 보드 상태
    private boolean[][] fixed;  // 고정 힌트 여부
    private SudokuBoard solutionBoard;  // 정답지 (퍼즐 생성 시 설정)

    public SudokuBoard() {
        board = new int[9][9];
        fixed = new boolean[9][9];
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public void setCell(int row, int col, int val) {
        board[row][col] = val;
    }

    public void setFixed(int row, int col, boolean isFixed) {
        fixed[row][col] = isFixed;
    }

    public boolean isFixed(int row, int col) {
        return fixed[row][col];
    }

    public boolean isValid(int row, int col, int num) {
        // 행과 열 검사
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num)
                return false;
        }
        // 3x3 서브그리드 검사
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num)
                    return false;
            }
        }
        return true;
    }

    public int[] findEmpty() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0)
                    return new int[]{i, j};
            }
        }
        return null;
    }

    public void setSolutionBoard(SudokuBoard solution) {
        this.solutionBoard = solution;
    }

    public SudokuBoard getSolutionBoard() {
        return this.solutionBoard;
    }

    public boolean isSolvedCorrectly() {
        if (solutionBoard == null)
            return false;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.board[i][j] != solutionBoard.getCell(i, j))
                    return false;
            }
        }
        return true;
    }

    public boolean solve() {
        int[] emptyPos = findEmpty();
        if (emptyPos == null)
            return true;

        int row = emptyPos[0];
        int col = emptyPos[1];

        for (int num = 1; num <= 9; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                if (solve())
                    return true;
                board[row][col] = 0;
            }
        }
        return false;
    }

    public boolean isValidMove(int row, int col, int val) {
        // 빈 칸(삭제)은 고정 셀만 아니면 항상 허용
        if (val == 0) {
            return !fixed[row][col];
        }
        return !fixed[row][col] && isValid(row, col, val);
    }

    /**
     * 셀에 값을 적용한 후, 보드가 완성되었는지 체크하고 정답과 비교합니다.
     * 입력 시마다 정답 여부를 Toast 메시지로 표시합니다.
     */
    public boolean setCellWithValidation(int row, int col, int val, Context context) {
        if (isValidMove(row, col, val)) {
            setCell(row, col, val);

            // 보드가 완전히 채워졌는지 검사
            if (isBoardFull()) {
                if (isSolvedCorrectly()) {
                    Toast.makeText(context, "정답입니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "오답입니다.", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        } else {
            Toast.makeText(context, "잘못된 수입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 현재 보드에 빈 칸이 없는지 검사하여, 보드가 완성되었는지 확인
    public boolean isBoardFull() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    // 고정되지 않은 셀(사용자 입력 대상)의 정답률을 계산합니다.
    public float getAccuracyPercentage() {
        if(solutionBoard == null)
            return 0f; // 정답판이 없으면 0으로 처리

        int totalUserCells = 0;
        int wrongCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!fixed[i][j]) { // 고정 셀이 아니라면 사용자가 입력해야 하는 셀
                    totalUserCells++;
                    if (board[i][j] != solutionBoard.getCell(i, j)) {
                        wrongCount++;
                    }
                }
            }
        }
        if (totalUserCells == 0) return 100f;  // 모든 셀이 고정이면 100%
        return ((totalUserCells - wrongCount) / (float) totalUserCells) * 100;
    }

}