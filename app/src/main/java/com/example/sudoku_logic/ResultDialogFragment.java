package com.example.sudoku_logic;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;



public class ResultDialogFragment extends DialogFragment {
    //결과 오버레이 다이얼로그를 담당하는 클래스
    // 퍼즐 완료 후, 정답/오답 여부와 추가 정보를 사용자에게 표시함

    private static final String ARG_ACCURACY = "ARG_ACCURACY";    // 정답률 %
    private static final String ARG_TIME = "ARG_TIME";            // 경과 시간(ms)
    private static final String ARG_SOLVED = "ARG_SOLVED";        // 정답 여부(solvedCorrectly)
    private static final String ARG_TOTAL = "ARG_TOTAL";          // 채울 수 있는 빈 칸 수
    private static final String ARG_WRONG = "ARG_WRONG";          // 틀린 칸 개수
    private static final String ARG_FIXED = "ARG_FIXED";          // 초기 고정 셀 개수


    public static ResultDialogFragment newInstance(float accuracy, long time, boolean solvedCorrectly,
                                                   int totalUserCells, int wrongCount, int fixedCount) {

        // 위 인자들을 불러와 ResultDialogFragment 인스턴스를 생성
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
        // 인자 값들 전달 받기
        assert getArguments() != null;
        float accuracy = getArguments().getFloat(ARG_ACCURACY, 0f);
        long elapsedMillis = getArguments().getLong(ARG_TIME, 0);
        boolean solvedCorrectly = getArguments().getBoolean(ARG_SOLVED, false);
        int totalUserCells = getArguments().getInt(ARG_TOTAL, 0);
        int wrongCount = getArguments().getInt(ARG_WRONG, 0);
        int fixedCount = getArguments().getInt(ARG_FIXED, 0);


        long elapsedSeconds = elapsedMillis / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);    // 경과 시간을 분:초 형식으로 변환

        String message;
        if (solvedCorrectly) {  // 정답인 경우
            message = "정답입니다!\n"
                    + "초기 고정 셀: " + fixedCount + "개\n"
                    + "걸린 시간: " + formattedTime;
        }
        else {    // 오답인 경우
            float wrongPercentage = 100f - accuracy;    // 오답율 계산
            message = "오답입니다!\n"
                    + "걸린 시간: " + formattedTime + "\n"
                    + "오답률: " + String.format("%.1f", wrongPercentage) + "%\n"
                    + "(채울 셀: " + totalUserCells + "개 / 틀린 셀: " + wrongCount + "개)";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());   // AlertDialog를 생성하여, 결과 메시지를 출력
        builder.setTitle("결과")
                .setMessage(message)
                .setPositiveButton("계속 진행", (dialog, which) -> {
                    dialog.dismiss();       // 다이얼로그를 닫고 퍼즐 진행
                });
        return builder.create();
    }
}