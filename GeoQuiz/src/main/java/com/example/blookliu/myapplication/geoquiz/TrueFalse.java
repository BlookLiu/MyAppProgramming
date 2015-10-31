package com.example.blookliu.myapplication.geoquiz;

/**
 * Created by BlookLiu on 2015/9/28.
 */
public class TrueFalse {
    private String mQuestion;
    private boolean mTrueQuestion;
    private boolean mHasCheated;

    public TrueFalse(String mQuestion, boolean mTrueQuestion) {
        this.mQuestion = mQuestion;
        this.mTrueQuestion = mTrueQuestion;
        this.mHasCheated = false;
    }

    public TrueFalse(String question, boolean trueQuestion, boolean hasCheated) {
        mQuestion = question;
        mTrueQuestion = trueQuestion;
        mHasCheated = hasCheated;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        mQuestion = question;
    }

    public boolean isTrueQuestion() {
        return mTrueQuestion;
    }

    public void setTrueQuestion(boolean trueQuestion) {
        mTrueQuestion = trueQuestion;
    }

    public boolean isHasCheated() {
        return mHasCheated;
    }

    public void setHasCheated(boolean hasCheated) {
        mHasCheated = hasCheated;
    }
}
