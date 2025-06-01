package com.example.sudoku_logic;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


//결과 오버레이 다이얼로그를 담당하는 클래스
// 퍼즐 완료 후, 정답/오답 여부와 추가 정보를 사용자에게 표시함
public class ResultDialogFragment extends DialogFragment {

    private static final String ARG_ACCURACY = "ARG_ACCURACY";    // 정답률(%) 전달용 키
    private static final String ARG_TIME = "ARG_TIME";            // 경과 시간(밀리초) 전달용 키
    private static final String ARG_SOLVED = "ARG_SOLVED";        // 정답 여부 전달용 키
    private static final String ARG_TOTAL = "ARG_TOTAL";          // 사용자 입력 대상 셀의 총 개수 전달용 키
    private static final String ARG_WRONG = "ARG_WRONG";          // 틀린 셀 개수 전달용 키
    private static final String ARG_FIXED = "ARG_FIXED";          // 초기 고정 셀의 개수 전달용 키


    // 정답률(accuracy), 경과 시간(time, 밀리초 단위),
    // 정답 여부(solvedCorrectly),
    // 사용자 입력 대상 셀의 총 개수(totalUserCells),
    // 틀린 셀 개수(wrongCount),
    // 그리고 초기 고정 셀의 개수(fixedCount)를 인자로 받아서
    // 새로운 ResultDialogFragment 인스턴스를 생성합니다.
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
        // 전달받은 인자 값들을 읽어옵니다.
        // getArguments()는 null이 아님을 가정합니다.
        assert getArguments() != null;
        float accuracy = getArguments().getFloat(ARG_ACCURACY, 0f);
        long elapsedMillis = getArguments().getLong(ARG_TIME, 0);
        boolean solvedCorrectly = getArguments().getBoolean(ARG_SOLVED, false);
        int totalUserCells = getArguments().getInt(ARG_TOTAL, 0);
        int wrongCount = getArguments().getInt(ARG_WRONG, 0);
        int fixedCount = getArguments().getInt(ARG_FIXED, 0);

        // 경과 시간을 분:초 형식으로 변환 (예: 05:23)
        long elapsedSeconds = elapsedMillis / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        String message;
        if (solvedCorrectly) {
            // 정답인 경우: 초기 고정 셀의 개수와 걸린 시간을 함께 표시
            message = "정답입니다!\n"
                    + "초기 고정 셀: " + fixedCount + "개\n"
                    + "걸린 시간: " + formattedTime;
        } else {
            // 오답인 경우: 오답 메시지, 걸린 시간, 오답률(100 - accuracy),
            // (사용자 입력 대상 셀의 개수/틀린 셀의 개수)를 함께 표시
            float wrongPercentage = 100f - accuracy;
            message = "오답입니다!\n"
                    + "걸린 시간: " + formattedTime + "\n"
                    + "오답률: " + String.format("%.1f", wrongPercentage) + "%\n"
                    + "(채울 셀: " + totalUserCells + "개 / 틀린 셀: " + wrongCount + "개)";
        }

        // AlertDialog를 생성하여, 결과 메시지를 출력합니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("결과")
                .setMessage(message)
                .setPositiveButton("계속 진행", (dialog, which) -> {
                    // 다이얼로그를 닫고 퍼즐 진행 (추가 동작이 필요한 경우 이곳에 구현 가능)
                    dialog.dismiss();
                });
        return builder.create();
    }
}