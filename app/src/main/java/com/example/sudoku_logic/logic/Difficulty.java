package com.example.sudoku_logic.logic;

public enum Difficulty {    // EASY, MEDIUM, HARD 세 단계에 대응하여 hintCount 변화
    EASY(36),
    MEDIUM(30),
    HARD(24);

    private final int hintCount;    // 힌트 수

    Difficulty(int hintCount) {     // 객체 생성자
        this.hintCount = hintCount;
    }


    public int getHintCount() { return hintCount; } // 사용자가 임의로 힌트 수를 설정할 경우 사용
}