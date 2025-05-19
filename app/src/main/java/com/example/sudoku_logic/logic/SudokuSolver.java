package com.example.sudoku_logic.logic;

public class SudokuSolver {     // 답안 검증 용 모듈
    private int solutionCount = 0;  // 존재하는 답안의 개수

    public boolean hasUniqueSolution (SudokuBoard board) {      // 유일 답안인지 체크하는 함수
        solutionCount = 0;
        countSolutions(board);      // 가능한 답안의 수 확인
        return ( solutionCount == 1 );  // 유일 답이라면 True 반환, 아니라면 False
    }

    private void countSolutions (SudokuBoard board) {
        if ( solutionCount > 1 ) return;    // 답안이 2개 이상이라면 탐색 종료

        int[] empty = board.findEmpty();
        if ( empty == null ) {      // 빈 칸이 없다면
            solutionCount++;        // 정답으로 간주
            return;
        }


        int row = empty[0];
        int col = empty[1];

        for ( int num = 1; num <= 9; num++ ) {
            if ( isValidCheck(board, row, col, num) ) { // 유효성 검사에서 문제가
                board.setCell(row, col, num);
                countSolutions(board);              // 없다면 재귀
                board.setCell(row, col, 0);     // 있다면 되돌리기
            }
        }
    }

    private boolean isValidCheck(SudokuBoard board, int row, int col, int num) {
        for ( int i = 0; i < 9; i++ ) {     // 가로, 세로 유효성 검사
            if ( board.getCell(row, i) == num || board.getCell(i, col) == num )
                return false;       // 문제 있으면 false 반환
        }

        int startRow = ( row / 3 ) * 3;
        int startCol = ( col / 3 ) * 3;

        for ( int i = startRow; i < startRow + 3; i++ ) {       // 서브그리드(작은박스) 검사
            for ( int j = startCol; j < startCol + 3; j++ ) {
                if ( board.getCell(i, j) == num ) return false; // 문제 있으면 false 반환
            }
        }
        return true;
    }
}
