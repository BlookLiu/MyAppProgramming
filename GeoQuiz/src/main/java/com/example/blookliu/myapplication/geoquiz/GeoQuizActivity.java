package com.example.blookliu.myapplication.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class GeoQuizActivity extends AppCompatActivity {
    private static final String TAG = "GeoQuizActivity";
    private static final String INDEX_KEY = "index_key";
    private static final String CHEAT_KEY = "cheat_key";
    public static final String EXTRA_ANSWER_IS_TRUE = "com.example.blookliu.MyApplication.geoquiz.answer_is_true";
    private static final int GEO_REQ_CODE = 0X01;
    private TextView mQuestionTv;
    private Button mTrueBtn;
    private Button mFalseBtn;
    private Button mCheatBtn;
    private ImageButton mNextBtn;
    private ImageButton mPrevBtn;
    private int mCurrentIndex = 0;
    private boolean mIsCheater = false;
    //    private String[] mQuestionBank = getResources().getStringArray(R.array.questions_bank_text);
    private TrueFalse[] mQuestionBank = new TrueFalse[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_quiz);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(INDEX_KEY, 0);
            mIsCheater = savedInstanceState.getBoolean(CHEAT_KEY, false);
        }
        Log.d(TAG, "on create currentIndex " + mCurrentIndex);
        String[] questionStrArr = getResources().getStringArray(R.array.questions_bank_text);
        mQuestionBank[0] = new TrueFalse(questionStrArr[0], true);
        mQuestionBank[1] = new TrueFalse(questionStrArr[1], false);
        mQuestionBank[2] = new TrueFalse(questionStrArr[2], false);
        mQuestionBank[3] = new TrueFalse(questionStrArr[3], true);
        mQuestionBank[4] = new TrueFalse(questionStrArr[4], true);
        initView();
        initData();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("dynamic title");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initData() {
        mTrueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "press true");
                checkAnswer(true);
            }
        });
        mFalseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "press false");
                checkAnswer(false);
            }
        });
        mCheatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GeoQuizActivity.this, CheatActivity.class);
                i.putExtra(EXTRA_ANSWER_IS_TRUE, mQuestionBank[mCurrentIndex].isTrueQuestion());
                startActivityForResult(i, GEO_REQ_CODE);
            }
        });
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                updateQuestion();
            }
        });
    }

    private void updateQuestion() {
        mIsCheater = false;
        String question = mQuestionBank[mCurrentIndex].getQuestion();
        mQuestionTv.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean anwserIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
        boolean hasCheated = mQuestionBank[mCurrentIndex].isHasCheated();
        int messageResId = 0;
        if (hasCheated) {
            messageResId = R.string.toast_judgement;
        } else {
            if (userPressedTrue == anwserIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show();
    }

    private void initView() {
        mQuestionTv = (TextView) findViewById(R.id.question_tv);
        mTrueBtn = (Button) findViewById(R.id.true_btn);
        mFalseBtn = (Button) findViewById(R.id.false_btn);
        mCheatBtn = (Button) findViewById(R.id.cheat_btn);
        mNextBtn = (ImageButton) findViewById(R.id.next_btn);
        mPrevBtn = (ImageButton) findViewById(R.id.prev_btn);
        updateQuestion();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(INDEX_KEY, mCurrentIndex);
        outState.putBoolean(CHEAT_KEY, mIsCheater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geo_quiz, menu);
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
            Toast.makeText(GeoQuizActivity.this, GeoQuizActivity.this.getString(R.string.toast_test, SystemClock.currentThreadTimeMillis()), Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode == RESULT_OK) {
//            Toast.makeText(GeoQuizActivity.this, "" + data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false), Toast.LENGTH_SHORT).show();
            mIsCheater = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
            mQuestionBank[mCurrentIndex].setHasCheated(mIsCheater);
        }
    }
}
