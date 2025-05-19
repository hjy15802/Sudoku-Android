package com.example.sudoku_logic.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuGenerator {      // 답안, 문제지 만드는 모듈
    private SudokuBoard board;

    public SudokuBoard generateSolution() {     // 정답 만드는 함수
        board = new SudokuBoard();      // 빈 답지 생성
        board.solve();                  // 답지 채워넣기
        return board;
    }

    public SudokuBoard generatePuzzle(Difficulty diff) {    // 문제지 만드는 함수
        SudokuBoard fullBoard = generateSolution();     // 정답 생성
        SudokuBoard puzzle = boardCopy(fullBoard);      // 복사해서 퍼즐 생성
        int hints = diff.getHintCount();

        removeCells(puzzle, 81 - hints);        // 힌트만큼 남기고 지우기
        return puzzle;
    }

    private void removeCells(SudokuBoard board, int rmvCells) {     // 문제지 만드는 과정
        List<int[]> cells = new ArrayList<>();

        for ( int i = 0; i < 9; i++ ) {
            for ( int j = 0; j < 9; j++ ) {
                cells.add( new int[]{i,j} );
            }
        }
        Collections.shuffle(cells);     // 스도쿠를 모두 섞음


        SudokuSolver solver = new SudokuSolver();
        int removed = 0;

        for ( int[] cell : cells ) {
            if ( removed >= rmvCells ) break;   // 지운 양이 지울 양에 도달하면 종료

            int row = cell[0];
            int col = cell[1];
            int tmp = board.getCell(row, col);  // 예비용 백업

            board.setCell(row, col, 0);     // 지워보기

            SudokuBoard clone = boardCopy(board);               // 복사본 생성 후
            if ( solver.hasUniqueSolution(clone) ) removed++;   // 유효하다면 제거
            else board.setCell(row, col, tmp);                  // 그렇지 않으면 백업으로 복구
        }
    }

    private SudokuBoard boardCopy(SudokuBoard origin) {
        SudokuBoard copy = new SudokuBoard();

        for ( int i = 0; i < 9; i++ )
            for ( int j = 0; j < 9; j++ ) copy.setCell( i, j, origin.getCell(i,j) );

        return copy;
    }

}
