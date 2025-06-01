package com.example.sudoku_logic.logic;

import android.content.Context;
import android.widget.Toast;

public class SudokuBoard {
    private int[][] board;            // 현재 스도쿠 보드 상태 (각 셀의 숫자 기록)
    private boolean[][] fixed;        // 고정 힌트 여부 (힌트로 제공된 셀은 true)
    private SudokuBoard solutionBoard; // 정답지 (퍼즐 생성 시 설정된 정답)

    public SudokuBoard() {
        board = new int[9][9];       // 9x9 보드 초기화 (빈 상태: 모든 값 0)
        fixed = new boolean[9][9];   // 모든 셀을 비고정 상태로 초기화
    }

    public int getCell(int row, int col) {  // 지정된 행, 열의 셀 값을 반환
        return board[row][col];
    }

    public void setCell(int row, int col, int val) {  // 지정된 셀에 숫자를 설정
        board[row][col] = val;
    }

    public void setFixed(int row, int col, boolean isFixed) {  // 해당 셀의 고정 여부를 설정
        fixed[row][col] = isFixed;
    }

    public boolean isFixed(int row, int col) {  // 지정 셀이 고정 상태인지 반환
        return fixed[row][col];
    }

    public boolean isValid(int row, int col, int num) {
        // 행과 열 검사: 같은 행이나 열에 num이 존재하는지 확인
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num)
                return false;
        }
        // 3x3 서브그리드 검사: 서브그리드 내 중복 여부 확인
        int startRow = (row / 3) * 3;  // 서브그리드 시작 행 계산
        int startCol = (col / 3) * 3;  // 서브그리드 시작 열 계산
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num)
                    return false;
            }
        }
        return true;  // 모든 검사 통과 시 유효함
    }

    public int[] findEmpty() {
        // 보드를 순회하며 첫번째 빈 셀 (값 == 0)의 위치 [row, col]를 반환
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0)
                    return new int[]{i, j};
            }
        }
        return null;  // 빈 셀이 없으면 전체 보드가 채워진 상태
    }

    public void setSolutionBoard(SudokuBoard solution) {  // 정답 보드를 설정
        this.solutionBoard = solution;
    }

    public SudokuBoard getSolutionBoard() {  // 정답 보드를 반환
        return this.solutionBoard;
    }

    public boolean isSolvedCorrectly() {
        if (solutionBoard == null)
            return false;  // 정답 보드가 없으면 정답 여부 판단 불가

        // 모든 셀을 비교해서 현재 보드와 정답 보드가 일치하는지 확인
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.board[i][j] != solutionBoard.getCell(i, j))
                    return false;
            }
        }
        return true;  // 모든 셀이 정답 보드와 일치하면 정답
    }

    public boolean solve() {
        // backtracking 알고리즘으로 스도쿠 보드를 해결
        int[] emptyPos = findEmpty();  // 첫 번째 빈 셀 찾기
        if (emptyPos == null)
            return true;               // 빈 셀이 없으면 보드를 모두 채운 상태

        int row = emptyPos[0];
        int col = emptyPos[1];

        // 1~9의 숫자를 순차적으로 시도
        for (int num = 1; num <= 9; num++) {
            if (isValid(row, col, num)) {  // num이 유효하면
                board[row][col] = num;     // 해당 셀에 배정
                if (solve())               // 재귀 호출로 보드 해결 시도
                    return true;
                board[row][col] = 0;       // 실패 시 원상복구 (백트래킹)
            }
        }
        return false;  // 모든 숫자 시도 실패 시 false 반환
    }

    public boolean isValidMove(int row, int col, int val) {
        if (val == 0)  return !fixed[row][col];                 // 삭제(0 입력)은 고정 셀만 아니면 항상 허용
        return !fixed[row][col] && isValid(row, col, val);      // 숫자 입력의 경우, 해당 셀이 고정되지 않으면서 유효한 값인지 검사
    }

    // 셀에 값을 적용한 후, 보드가 완성되었는지 체크하고 정답과 비교
    // 입력 시마다 보드 완성시 정답 여부를 Toast 메시지로 표시하는 메소드
    public boolean setCellWithValidation(int row, int col, int val, Context context) {
        if (isValidMove(row, col, val)) {  // 입력된 수가 허용되면
            setCell(row, col, val);        // 셀에 값 적용

            // 보드가 완전히 채워졌는지 확인
            if (isBoardFull()) {
                if (isSolvedCorrectly())  // 정답이면
                    Toast.makeText(context, "정답입니다!", Toast.LENGTH_SHORT).show();
                else                      // 아니면 오답 메시지 표시
                    Toast.makeText(context, "오답입니다.", Toast.LENGTH_SHORT).show();
            }
            return true;    // 유효한 입력이므로 true 반환
        }
        else {              // 유효하지 않은 입력이면
            Toast.makeText(context, "잘못된 수입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 현재 보드에 빈 칸이 없는지 검사하여 보드가 완성되었는지 확인
    public boolean isBoardFull() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    // 고정되지 않은 셀(사용자 입력 대상)에서 정답과 일치하는 비율(정답률)을 계산
    public float getAccuracyPercentage() {
        if (solutionBoard == null)
            return 0f;  // 정답 보드가 없으면 0% 처리

        int totalUserCells = 0;  // 사용자 입력 대상 셀 수
        int wrongCount = 0;      // 정답과 불일치하는 셀 수
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!fixed[i][j]) {  // 고정 셀이 아니라면
                    totalUserCells++;
                    if (board[i][j] != solutionBoard.getCell(i, j)) {
                        wrongCount++;
                    }
                }
            }
        }
        if (totalUserCells == 0) return 100f;  // 모든 셀이 고정이면 정답률 100%
        return ((totalUserCells - wrongCount) / (float) totalUserCells) * 100;
    }
}