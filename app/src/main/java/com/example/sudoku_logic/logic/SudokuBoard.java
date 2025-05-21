package com.example.sudoku_logic.logic;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuBoard {      // 스도쿠 객체 정의
    private final int[][] board = new int[9][9];  // 스도쿠 객체
    private final boolean[][] fixed = new boolean[9][9];    // 초기 힌트 저장용

    public int getCell( int row, int col ) { return board[row][col]; }      // 셀 내 값 호출
    public void setCell( int row, int col, int val ) { board[row][col] = val; }     // 셀 내 값 수정

    public int[] findEmpty() {   // 비어있는 칸 찾기
        for ( int i = 0; i < 9; i++ ) {
            for ( int j = 0; j < 9; j++ ) {
                if ( this.getCell(i, j) == 0) return new int[]{i, j};
            }
        }
        return null;
    }

    public boolean isValid(int row, int col, int num) {         // 룰에 맞는지 검증
        for ( int i = 0; i < 9; i++ ) if ( board[row][i] == num ) return false;     // 가로줄 확인
        for ( int i = 0; i < 9; i++ ) if ( board[i][col] == num ) return false;     // 세로줄 확인


        // 서브 그리드(작은 박스) 확인
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;

        for ( int i = 0; i < 3; i++ ) {
            for ( int j = 0; j < 3; j++ ) {
                if (board[startRow + i][startCol + j] == num) return false;
            }
        }
        return true;
    }

    public boolean isValidMove(int row, int col, int num) {     // 유저 입력 검증
        if (num < 1 || num > 9) return false;       // 입력 값이 1~9 사이인지 확인
        if (board[row][col] != 0) return false;     // 이미 채워진 칸이면 false
        return isValid(row, col, num);
    }

    // 입력에 대한 유효성 검사
    public void setCellWithValidation(Context context, int row, int col, int num) {
        if (isFixed(row, col)) {                                        // 힌트 셀에 입력하면
            Toast.makeText(context, "힌트 셀은 수정할 수 없습니다!",    // 토스트 메시지 출력
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidMove(row, col, num)) {
            board[row][col] = 0;                                        // 잘못된 입력이면 칸을 자동으로 비우고
            Toast.makeText(context, "올바르지 않은 입력입니다!",
                    Toast.LENGTH_SHORT).show();                         // 토스트 메시지 출력
            return;
        }

        board[row][col] = num;      // 문제 없으면 값 저장
    }

    public boolean solve() {        //정답판 만들기
        int[] emptyPos = findEmpty();           // 빈 칸 찾기
        if ( emptyPos == null ) return true;    // 빈 칸이 없으면 완성. True 반환

        int row = emptyPos[0];
        int col = emptyPos[1];

        List<Integer> numbers = generateShuffle();  //섞인 숫자 가져오기

        for ( int num : numbers ) {
            if ( isValid(row, col, num) ) {         // 유효성 검사

                setCell(row, col, num);             // 문제 없으면 값 넣기
                if ( solve() ) return true;         // 재귀 함수 트리거

                setCell(row, col, 0);           // 재귀 호출 실패 시 비우고 백트래킹
            }
        }

        return false;   // 전부 실패했을 경우 False 반환
    }

    private List<Integer> generateShuffle() {       // 숫자 섞기
        List<Integer> nums = new ArrayList<>();
        for ( int i = 1; i <= 9; i++ ) nums.add(i);     // 리스트에 1~9 넣기
        Collections.shuffle(nums);  // 리스트 내에서 섞기
        return nums;
    }

    public void setFixed(int row, int col, boolean isFixed) {       // 고정 힌트 추가 메소드
        fixed[row][col] = isFixed;
    }

    public boolean isFixed(int row, int col) {      // 고정 힌트 칸인지 확인하는 메소드
        return fixed[row][col];     // 고정칸이면 True, 아니면 False
    }
}
