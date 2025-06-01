package com.example.sudoku_logic;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ResultDialogFragment extends DialogFragment {

    private static final String ARG_ACCURACY = "ARG_ACCURACY";
    private static final String ARG_TIME = "ARG_TIME";
    private static final String ARG_SOLVED = "ARG_SOLVED";
    private static final String ARG_TOTAL = "ARG_TOTAL";     // 사용자 입력 대상 셀의 총 개수
    private static final String ARG_WRONG = "ARG_WRONG";       // 틀린 셀의 개수
    private static final String ARG_FIXED = "ARG_FIXED";       // 초기 고정 셀의 개수

    /**
     * 정답률(accuracy), 경과 시간(time, 밀리초 단위),
     * 정답 여부(solvedCorrectly),
     * 사용자 입력 대상 셀의 총 개수(totalUserCells),
     * 틀린 셀 개수(wrongCount),
     * 그리고 초기 고정 셀의 개수(fixedCount)를 인자로 받습니다.
     */
    public static ResultDialogFragment newInstance(float accuracy, long time, boolean solvedCorrectly,
                                                   int totalUserCells, int wrongCount, int fixedCount) {
        ResultDialogFragment fragment = new ResultDialogFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_ACCURACY, accuracy);
        args.putLong(ARG_TIME, time);
        args.putBoolean(ARG_SOLVED, solvedCorrectly);
        args.putInt(ARG_TOTAL, totalUserCells);
        args.putInt(ARG_WRONG, wrongCount);
        args.putInt(ARG_FIXED, fixedCount);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 전달받은 값 읽기
        assert getArguments() != null;
        float accuracy = getArguments().getFloat(ARG_ACCURACY, 0f);
        long elapsedMillis = getArguments().getLong(ARG_TIME, 0);
        boolean solvedCorrectly = getArguments().getBoolean(ARG_SOLVED, false);
        int totalUserCells = getArguments().getInt(ARG_TOTAL, 0);
        int wrongCount = getArguments().getInt(ARG_WRONG, 0);
        int fixedCount = getArguments().getInt(ARG_FIXED, 0);

        // 밀리초를 분:초 형식으로 변환 (예: 05:23)
        long elapsedSeconds = elapsedMillis / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        String message;
        if (solvedCorrectly) {
            // 정답인 경우: 초기 고정 셀의 개수와 걸린 시간을 함께 표시합니다.
            message = "정답입니다!\n"
                    + "초기 고정 셀: " + fixedCount + "개\n"
                    + "걸린 시간: " + formattedTime;
        } else {
            // 오답인 경우: 오답 메시지, 걸린 시간, 오답률(100 - accuracy) 및 (총 사용자 입력 셀/틀린 셀의 개수)를 표시합니다.
            float wrongPercentage = 100f - accuracy;
            message = "오답입니다!\n"
                    + "걸린 시간: " + formattedTime + "\n"
                    + "오답률: " + String.format("%.1f", wrongPercentage) + "%\n"
                    + "(채울 셀: " + totalUserCells + "개 / 틀린 셀: " + wrongCount + "개)";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("결과")
                .setMessage(message)
                .setPositiveButton("계속 진행", (dialog, which) -> {
                    // 단순히 다이얼로그를 닫고 퍼즐 진행 (필요 시 추가 동작 구현 가능)
                    dialog.dismiss();
                });
        return builder.create();
    }
}