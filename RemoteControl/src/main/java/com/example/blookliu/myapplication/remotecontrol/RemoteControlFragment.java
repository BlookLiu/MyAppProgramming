package com.example.blookliu.myapplication.remotecontrol;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by BlookLiu on 2015/11/8.
 */
public class RemoteControlFragment extends Fragment {
    private TextView mSelectedTv, mWorkingTv;
//    private Button mZeroBtn, mOneBtn, mEnterBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remote_control, container, false);
        mSelectedTv = (TextView) v.findViewById(R.id.remote_control_selected_tv);
        mWorkingTv = (TextView) v.findViewById(R.id.remote_control_working_tv);
        View.OnClickListener numberBtnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                String working = mWorkingTv.getText().toString();
                String text = textView.getText().toString();
                if (working.equals("0")) {
                    mWorkingTv.setText(text);
                } else {
                    mWorkingTv.setText(working + text);
                }
            }
        };
        TableLayout tl = (TableLayout) v.findViewById(R.id.fragment_remote_control_tl);
        int number = 1;
        for (int i = 2; i < tl.getChildCount() - 1; i++) {
            TableRow tr = (TableRow) tl.getChildAt(i);
            for (int j = 0; j < tr.getChildCount(); j++) {
                Button button = (Button) tr.getChildAt(j);
                button.setText(String.valueOf(number));
                button.setOnClickListener(numberBtnClickListener);
                number++;
            }
        }
        TableRow bottomRow = (TableRow) tl.getChildAt(tl.getChildCount() - 1);
        Button deleteBtn = (Button) bottomRow.getChildAt(0);
        deleteBtn.setText(R.string.fragment_remote_control_delete_text);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkingTv.setText("0");
            }
        });
        Button zeroBtn = (Button) bottomRow.getChildAt(1);
        zeroBtn.setText("0");
        zeroBtn.setOnClickListener(numberBtnClickListener);
        Button enterBtn = (Button) bottomRow.getChildAt(2);
        enterBtn.setText(R.string.fragment_remote_control_enter_text);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence working = mWorkingTv.getText();
                if (working.length() > 0) {
                    mSelectedTv.setText(working);
                }
                mWorkingTv.setText("0");
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            enterBtn.setTextAppearance(R.style.RemoteControlBtnStyle_Bold);
            deleteBtn.setTextAppearance(R.style.RemoteControlBtnStyle_Bold);
        } else {
            enterBtn.setTextAppearance(getActivity(), R.style.RemoteControlBtnStyle_Bold);
            deleteBtn.setTextAppearance(getActivity(), R.style.RemoteControlBtnStyle_Bold);
        }
        return v;
    }
}
