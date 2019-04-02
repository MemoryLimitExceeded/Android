package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity
{
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPreButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_MISCHEATER = "IsCheater";
    private static final String KEY_NUM = "num";
    private static final int REQUEST_CODE_CHEAT = 0;
    //private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String ISLOOKED = "lvbo";
    private Question[] mQuestionBank = new Question[]
    {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };
    private int mCurrentIndex = 0;
    private int mCheartNum = 3;
    private boolean[] mIsCheaterArray = new boolean[mQuestionBank.length];
    //private int mCorrect = 0;
    //private int mNum = 0;
    //private int[] mMark = new int[mQuestionBank.length];
    /*public static boolean wasAnswerShown(Intent result)
    {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }*/
    private void updateQuestion()
    {
        //Log.d(TAG, "Updating question text ", new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if(mCheartNum == 0)
        {
            if(mIsCheaterArray[mCurrentIndex] == true)
            {
                mCheatButton.setEnabled(true);
            }
            else
            {
                mCheatButton.setEnabled(false);
            }
        }
    }
    private void checkAnswer(boolean userPressedTrue)
    {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if(mIsCheaterArray[mCurrentIndex] == true)
        {
            messageResId = R.string.judgment_toast;
            //mCorrect++;
        }
        else
        {
            if(userPressedTrue == answerIsTrue)
            {
                messageResId = R.string.correct_toast;
            }
            else
            {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show();
        /*mNum++;
        if(mNum == mQuestionBank.length)
        {
            Toast.makeText(this,String.valueOf(mCorrect*100.0/mNum),Toast.LENGTH_SHORT).show();
            finish();
        }*/
    }
    /*public static Intent newIntent(Context packageContext,boolean answerIsTrue)
    {
        Intent intent = new Intent(packageContext,CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }*/
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == REQUEST_CODE_CHEAT)
            {
                if(data != null && mIsCheaterArray[mCurrentIndex] == false)
                {
                    if(data.getBooleanExtra(EXTRA_ANSWER_SHOWN,false) == true)
                    {
                        mCheartNum--;
                        mCheatButton.setText(getString(R.string.cheat_button) + mCheartNum);
                    }
                    mIsCheaterArray[mCurrentIndex] = mIsCheaterArray[mCurrentIndex] | CheatActivity.wasAnswerShown(data);
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        if(savedInstanceState != null)
        {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mIsCheaterArray[mCurrentIndex] = mIsCheaterArray[mCurrentIndex] | savedInstanceState.getBoolean(KEY_MISCHEATER,false);
            mCheartNum = savedInstanceState.getInt(KEY_NUM,0);
        }
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*if(mMark[mCurrentIndex] == 1)
                {
                    Toast.makeText(QuizActivity.this,"Disable",Toast.LENGTH_SHORT).show();
                }
                else*/
                {
                    //mMark[mCurrentIndex] = 1;
                    checkAnswer(true);
                }
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*if(mMark[mCurrentIndex] == 1)
                {
                    Toast.makeText(QuizActivity.this,"Disable",Toast.LENGTH_SHORT).show();
                }
                else*/
                {
                    //mMark[mCurrentIndex] = 1;
                    checkAnswer(false);
                }
            }
        });
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mPreButton = (Button) findViewById(R.id.pre_button);
        mPreButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setText(getString(R.string.cheat_button) + mCheartNum);
        if(mCheartNum == 0)
        {
            mCheatButton.setEnabled(false);
        }
        mCheatButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Intent intent = newIntent(QuizActivity.this,mQuestionBank[mCurrentIndex].isAnswerTrue());
                Intent intent = CheatActivity.newIntent(QuizActivity.this,mQuestionBank[mCurrentIndex].isAnswerTrue());
                intent.putExtra(ISLOOKED,mIsCheaterArray[mCurrentIndex]);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        updateQuestion();
    }
    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG,"onStart() called");
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG,"onResume() called");
    }
    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG,"onPause() called");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        //Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_MISCHEATER, mIsCheaterArray[mCurrentIndex]);
        savedInstanceState.putInt(KEY_NUM,mCheartNum);
    }
    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
