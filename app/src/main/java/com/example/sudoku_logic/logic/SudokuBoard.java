package com.example.sudoku_logic.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SudokuBoard {      // 스도쿠 객체 정의
    private int[][] board = new int[9][9];  //스도쿠 객체

    public int getCell( int row, int col ) { return board[row][col]; }  //셀 내 값 호출
    public void setCell( int row, int col, int val ) { board[row][col] = val; } //셀 내 값 수정

    public int[] findEmpty() {   // 비어있는 칸 찾기
        for ( int i = 0; i < 9; i++ ) {
            for ( int j = 0; j < 9; j++ ) {
                if ( this.getCell(i, j) == 0) return new int[]{i, j};
            }
        }
        return null;
    }

    public boolean isValid(int row, int col, int num) {
        // 가로줄 확인
        for ( int i = 0; i < 9; i++ ) if ( board[row][i] == num ) return false;

        // 세로줄 확인
        for ( int i = 0; i < 9; i++ ) if ( board[i][col] == num ) return false;


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

    public boolean solve() {        //정답판 만들기
        int[] emptyPos = findEmpty();           // 빈 칸 찾기
        if ( emptyPos == null ) return true;    // 빈 칸이 없으면 완성. True 반환

        int row = emptyPos[0];
        int col = emptyPos[1];

        List<Integer> numbers = generataShuffle();  //섞인 숫자 가져오기

        for ( int num : numbers ) {
            if ( isValid(row, col, num) ) {         // 유효성 검사

                setCell(row, col, num);             // 문제 없으면 값 넣기
                if ( solve() ) return true;         // 재귀 함수 트리거

                setCell(row, col, 0);           // 재귀 호출 실패 시 비우고 백트래킹
            }
        }

        return false;   // 전부 실패했을 경우 False 반환
    }

    private List<Integer> generataShuffle() {       // 숫자 섞기
        List<Integer> nums = new ArrayList<>();
        for ( int i = 1; i <= 9; i++ ) nums.add(i);     // 리스트에 1~9 넣기
        Collections.shuffle(nums);  // 리스트 내에서 섞기
        return nums;
    }
}
