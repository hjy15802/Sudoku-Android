package com.example.sudoku_logic.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuGenerator {      // 답안, 문제지 만드는 모듈

    private int countHints(SudokuBoard board) {     // 남아있는 힌트 개수 검수
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getCell(i, j) != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public SudokuBoard generateSolution() {         // 정답 만드는 함수
        SudokuBoard board = new SudokuBoard();      // 빈 답지 생성
        board.solve();                              // 답지 채워넣기
        return board;
    }

    public SudokuBoard generatePuzzle(Difficulty diff) {    // 문제지 만드는 함수
        SudokuBoard solutionBoard;     // 정답 생성
        SudokuBoard puzzle;      // 복사해서 퍼즐 생성
        int hints = diff.getHintCount();
        int attempts = 0;
        final int MAX_ATTEMPTS = 1000;  // 재시도 최대 횟수

        do {
            solutionBoard = generateSolution();             // 정답 생성
            puzzle = boardCopy(solutionBoard);              // 정답을 복사해서 퍼즐 생성
            removeCells(puzzle, 81 - hints);        // 힌트만큼 남기고 지우기
            attempts++;
        }
        while (countHints(puzzle) != hints
                && attempts < MAX_ATTEMPTS);

        for (int i = 0; i < 9; i++) {               // 힌트인 셀은 고정된 상태로 설정
            for (int j = 0; j < 9; j++) {
                if (puzzle.getCell(i, j) != 0) {
                    puzzle.setFixed(i, j, true);
                }
            }
        }
        puzzle.setSolutionBoard(solutionBoard);
        return puzzle;
    }

    private void removeCells(SudokuBoard board, int rmvCells) {     // 문제지 만드는 과정
        SudokuSolver solver = new SudokuSolver();
        int removed = 0;

        // 목표 제거 수에 도달할 때까지 반복 시도
        do {
            boolean anyRemovalInPass = false;  // 이번 반복에서 하나라도 제거되었는지 여부

            // 남아있는 셀들로 리스트 생성
            List<int[]> cells = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (board.getCell(i, j) != 0) {  // 값이 남아있는 셀만 추가
                        cells.add(new int[]{i, j});
                    }
                }
            }
            Collections.shuffle(cells);

            for (int[] cell : cells) {
                if (removed >= rmvCells) break;     // 목표 수 달성 시 종료

                int row = cell[0];
                int col = cell[1];
                int tmp = board.getCell(row, col);  // 현재 셀 값 백업

                board.setCell(row, col, 0);     // 셀 제거 시도

                // 제거 후, 복사본을 생성하여 유일해 여부 확인
                SudokuBoard clone = boardCopy(board);
                if (solver.hasUniqueSolution(clone)) {
                    removed++;                     // 유일해가 유지되면 유지
                    anyRemovalInPass = true;
                }
                else {
                    board.setCell(row, col, tmp);  // 유일해가 어긋나면 복원
                }
            }

            if (!anyRemovalInPass) break;    // 이번 반복에서 셀이 제거되지 않았으면 종료

        } while (removed < rmvCells);
    }

    private SudokuBoard boardCopy(SudokuBoard origin) {
        SudokuBoard copy = new SudokuBoard();

        for ( int i = 0; i < 9; i++ )
            for ( int j = 0; j < 9; j++ ) copy.setCell( i, j, origin.getCell(i,j) );

        return copy;
    }

}