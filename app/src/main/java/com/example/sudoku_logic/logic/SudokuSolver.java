package com.example.sudoku_logic.logic;

public class SudokuSolver {     // 답안 검증 용 모듈
    private int solutionCount = 0;  // 존재하는 답안의 개수

    public boolean hasUniqueSolution (SudokuBoard board) {      // 유일 답안인지 체크하는 함수
        solutionCount = 0;
        countSolutions(board);      // 가능한 답안의 수 확인
        return ( solutionCount == 1 );  // 유일 답이라면 True 반환, 아니라면 False
    }

    private void countSolutions(SudokuBoard board) {
        if ( solutionCount > 1 ) return;    // 답안이 2개 이상이라면 탐색 종료

        int[] empty = board.findEmpty();
        if ( empty == null ) {      // 빈 칸이 없다면
            solutionCount++;        // 정답으로 간주
            return;
        }

        int row = empty[0];
        int col = empty[1];

        for ( int num = 1; num <= 9; num++ ) {
            if (board.isValid(row, col, num)) {     // isValid()로 유효성 검사해서
                board.setCell(row, col, num);
                countSolutions(board);              // 문제 없으면 재귀
                board.setCell(row, col, 0);     // 문제 있으면 되돌리기
            }
        }
    }
}