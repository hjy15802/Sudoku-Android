package com.example.sudoku_logic.logic;

import android.content.Context;
import android.widget.Toast;

public class SudokuBoard {      // 스도쿠 객체 정의
    private int[][] board;  // 스도쿠 객체
    private boolean[][] fixed;    // 초기 힌트 저장용
    private SudokuBoard solutionBoard;                  // 정답 확인용 정답지

    public SudokuBoard() {
        board = new int[9][9];
        fixed = new boolean[9][9];
    }

    public int getCell( int row, int col ) { return board[row][col]; }      // 셀 내 값 호출
    public void setCell( int row, int col, int val ) { board[row][col] = val; }     // 셀 내 값 수정
    public void setFixed(int row, int col, boolean isFixed) { fixed[row][col] = isFixed; } // 고정 힌트 추가 메소드
    public boolean isFixed(int row, int col) { return fixed[row][col]; } // 고정 칸인지 확인하는 메소드

    public boolean isValid(int row, int col, int num) {         // 룰에 맞는지 검증
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) return false; // 가로, 세로 확인
        }

        // 서브 그리드(작은 박스) 확인
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;

        for ( int i = startRow; i < 3 + startRow; i++ ) {
            for ( int j = startCol; j < 3 + startCol; j++ ) {
                if ( board[i][j] == num ) return false;
            }
        }
        return true;
    }

    public int[] findEmpty() {   // 비어있는 칸 찾기
        for ( int i = 0; i < 9; i++ ) {
            for ( int j = 0; j < 9; j++ ) {
                if ( board[i][j] == 0 ) return new int[]{i, j};
            }
        }
        return null;
    }

    public void setSolutionBoard(SudokuBoard solution) { this.solutionBoard = solution; }
    public SudokuBoard getSolutionBoard() { return this.solutionBoard; }

    public boolean isSolvedCorrectly() {        // 정답지와 비교하며 확인하는 메소드
        if (solutionBoard == null) return false;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.board[i][j] != solutionBoard.getCell(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean solve() {        //정답판 만들기
        int[] emptyPos = findEmpty();           // 빈 칸 찾기
        if ( emptyPos == null ) return true;    // 빈 칸이 없으면 완성. True 반환

        int row = emptyPos[0];
        int col = emptyPos[1];

        for ( int num = 1; num <= 9; num++ ) {
            if ( isValid(row, col, num) ) {         // 유효성 검사
                board[row][col] = num;              // 문제 없으면 값 넣기
                if (solve()) return true;           // 재귀 함수 트리거
                board[row][col] = 0;                // 재귀 호출 실패 시 비우고 백트래킹
            }
        }
        return false;   // 전부 실패했을 경우 False 반환
    }
    public boolean isValidMove(int row, int col, int val) {
        return !fixed[row][col] && isValid(row, col, val);
    }
    public boolean setCellWithValidation(int row, int col, int val, Context context) {
        if ( isValidMove(row, col, val) ) {
            setCell(row, col, val);

            if ( isSolvedCorrectly() ) {
                Toast.makeText(context, "정답입니다!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else {
            Toast.makeText(context, "잘못된 수입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}