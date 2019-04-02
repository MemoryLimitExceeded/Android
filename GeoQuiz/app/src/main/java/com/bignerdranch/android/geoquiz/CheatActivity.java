package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CheatActivity extends AppCompatActivity
{
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private TextView mApiLevel;
    private Button mShowAnswerButton;
    private boolean mIsCheater;
    private static final String KEY_MISCHEATER = "IsCheater";
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String ISLOOKED = "lvbo";
    public static boolean wasAnswerShown(Intent result)
    {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }
    public static Intent newIntent(Context packageContext, boolean answerIsTrue)
    {
        Intent intent = new Intent(packageContext,CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }
    private void setAnswerShownResult(boolean isAnswerShown)
    {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mApiLevel = (TextView) findViewById(R.id.show_api_level);
        mApiLevel.setText("API Level " + Integer.valueOf(android.os.Build.VERSION.SDK_INT));
        if(savedInstanceState != null)
        {
            mIsCheater = savedInstanceState.getBoolean(KEY_MISCHEATER,false);
            if(mIsCheater == true)
            {
                if(mAnswerIsTrue == true)
                {
                    mAnswerTextView.setText(R.string.true_button);
                }
                else
                {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
            }
        }
        if(getIntent().getBooleanExtra(ISLOOKED,false) == true)
        {
            mShowAnswerButton.setEnabled(false);
            if(mAnswerIsTrue == true)
            {
                mAnswerTextView.setText(R.string.true_button);
            }
            else
            {
                mAnswerTextView.setText(R.string.false_button);
            }
            mIsCheater = true;
            setAnswerShownResult(true);
        }
        mShowAnswerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mAnswerIsTrue == true)
                {
                    mAnswerTextView.setText(R.string.true_button);
                }
                else
                {
                    mAnswerTextView.setText(R.string.false_button);
                }
                mIsCheater = true;
                setAnswerShownResult(true);
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_MISCHEATER, mIsCheater);
    }
}
