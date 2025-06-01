package com.example.sudoku_logic.logic;

public enum Difficulty {    // EASY, MEDIUM, HARD 세 단계에 대응하여 hintCount 변화
    TEST(70),
    EASY(34),
    MEDIUM(28),
    HARD(22),
    CUSTOM(-1);

    private int hintCount;    // 힌트 수

    Difficulty(int hintCount) {     // 객체 생성자
        this.hintCount = hintCount;
    }


    public int getHintCount() { return hintCount; }

    public void setCustomHintCount(int hintCount) {
        if (this == CUSTOM) {
            this.hintCount = hintCount;
        }
    }

}