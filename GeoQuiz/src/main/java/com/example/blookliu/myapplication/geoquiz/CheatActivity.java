package com.example.blookliu.myapplication.geoquiz;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CheatActivity extends AppCompatActivity {
    public static final String EXTRA_ANSWER_SHOWN = "com.example.blookliu.myapplication.geoquiz.answer_shown";
    private static final String HAS_CHEAT = "has_cheat";
    private static final String ANSWER_RES_ID = "answer_res_id";
    private boolean mAnswerIsTrue;
    private boolean mHasPressedCheat = false;
    private TextView mAnswerTv;
    private TextView mCompileTv;
    private Button mShowAnswerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        mAnswerIsTrue = getIntent().getBooleanExtra(GeoQuizActivity.EXTRA_ANSWER_IS_TRUE, false);
        initView();
        initData();
        if (savedInstanceState != null) {
            mHasPressedCheat = savedInstanceState.getBoolean(HAS_CHEAT, false);
            mAnswerTv.setText(savedInstanceState.getCharSequence(ANSWER_RES_ID, ""));
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
//            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void initData() {
        mShowAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CheatActivity.this, "" + mAnswerIsTrue, Toast.LENGTH_SHORT).show();
                if (mAnswerIsTrue)
                    mAnswerTv.setText(R.string.true_btn_text);
                else
                    mAnswerTv.setText(R.string.false_btn_text);
                mHasPressedCheat = true;
                setAnswerShownResult();
            }
        });
        mCompileTv.setText(Build.DEVICE+" "+Build.VERSION.SDK_INT);
    }

    private void initView() {
        mAnswerTv = (TextView) findViewById(R.id.answer_tv);
        mShowAnswerBtn = (Button) findViewById(R.id.show_answer_btn);
        mCompileTv = (TextView) findViewById(R.id.compile_tv);
    }

    private void setAnswerShownResult() {
        Intent i = new Intent();
        i.putExtra(EXTRA_ANSWER_SHOWN, mHasPressedCheat);
        setResult(RESULT_OK, i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(HAS_CHEAT, mHasPressedCheat);
        outState.putCharSequence(ANSWER_RES_ID, mAnswerTv.getText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cheat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
