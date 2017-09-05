package com.example.ameyp.MathPuzzleAlarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ameyp.MathPuzzleAlarm.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MathPuzzle extends AppCompatActivity {

    private SecureRandom random;
    private List<String> EnabledOperators = new ArrayList<>(Arrays.asList("Subtraction", "Addition", "Multiplication"));
    private String correctAnswer;
    private TextView QuestionTextView, answerTextView;
    private LinearLayout[] guessLinearLayouts;
    private TextView questionNumberTextView;
    private int correctAnswers;
    private LinearLayout quizLinearLayout;
    private Handler handler;
    private ArrayList<Integer> randomList;
    private int listIterator = 0;

    private final int guessRows = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_puzzle);

        QuestionTextView =  (TextView) findViewById(R.id.QuestionTextView);
        answerTextView =  (TextView) findViewById(R.id.answerTextView);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] =
                (LinearLayout) findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] =
                (LinearLayout) findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] =
                (LinearLayout) findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] =
                (LinearLayout) findViewById(R.id.row4LinearLayout);
        questionNumberTextView =
                (TextView) findViewById(R.id.questionNumberTextView);
        quizLinearLayout =
                (LinearLayout) findViewById(R.id.quizLinearLayout);

        random = new SecureRandom();
        handler = new Handler();

        Intent intent = getIntent();

        correctAnswers = 0;

        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }
    }

    protected void onStart() {
        super.onStart();

        if(MainActivity.totalQuestions == 0){
            Log.e("Inside  - " , "Func");
            RingtoneService.mediaPlayer.stop();
            RingtoneService.mediaPlayer.reset();
            MainActivity.set_alarm_status("Alarm Off!");

            finish();
            return;
        }

        //Generates random numbers and stores in a HashMap
        generateRandomNum();

        new Thread() {
            public void run() {
                loadNextQuestion();
            }
        }.start();
    }

    private void generateRandomNum() {

        int num;

        Set<Integer> setRandomNums = new HashSet<>();
        randomList = new ArrayList<Integer>();

        while(setRandomNums.size() < MainActivity.totalQuestions*2)
        {
            num = random.nextInt(50) + 1;
            setRandomNums.add(num);
        }
         int i=0;

        for(Integer randomNum : setRandomNums){
            randomList.add(i, randomNum);
            i++;
        }

        Collections.shuffle(randomList);

    }

    public void loadNextQuestion() {

        int numOne = 0, numTwo = 0;
        int RandomChoices;
        int correctAnswerInt;

        String question;

        int OperatorRandom = random.nextInt(EnabledOperators.size());
        String CurrentOperator = EnabledOperators.get(OperatorRandom);

        if(CurrentOperator.equals("Multiplication"))
        {
            numOne = randomList.get(listIterator);
            listIterator++;

            numTwo = randomList.get(listIterator);
            listIterator++;

            correctAnswerInt = numOne*numTwo;

            correctAnswer = Integer.toString(correctAnswerInt);

            question = "\n" + Integer.toString(numOne) + " X " + Integer.toString(numTwo) ;

            QuestionTextView.setText(question);

        }
        else if (CurrentOperator.equals("Addition")) {
            numOne = randomList.get(listIterator);
            listIterator++;

            numTwo = randomList.get(listIterator);
            listIterator++;

            correctAnswerInt = numOne+numTwo;
            correctAnswer = Integer.toString(correctAnswerInt);

            question = "\n" + Integer.toString(numOne) + " + " + Integer.toString(numTwo) ;
            QuestionTextView.setText(question);
        }
        else if (CurrentOperator.equals("Subtraction")) {
            numOne = randomList.get(listIterator);
            listIterator++;

            numTwo = randomList.get(listIterator);
            listIterator++;

            if(numOne>numTwo) {
                correctAnswerInt = numOne - numTwo;
                question = "\n" + Integer.toString(numOne) + " - " + Integer.toString(numTwo) ;
            }
            else{
                correctAnswerInt = numTwo - numOne;
                question = "\n" + Integer.toString(numTwo) + " - " + Integer.toString(numOne)  ;
            }

            correctAnswer = Integer.toString(correctAnswerInt);

            QuestionTextView.setText(question);
        }

        answerTextView.setText("");

        questionNumberTextView.setText(getString(
                R.string.question, (correctAnswers + 1), MainActivity.totalQuestions));

        for (int row = 0; row < guessRows; row++) {

            for (int column = 0; column < guessLinearLayouts[row].getChildCount(); column++)
            {

                Button newGuessButton =
                        (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                while(true) {
                    RandomChoices = random.nextInt(130) + 1;
                    if(RandomChoices != Integer.parseInt(correctAnswer)){
                        break;
                    }
                }
                newGuessButton.setText(Integer.toString(RandomChoices));
            }
        }

        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        ((Button) randomRow.getChildAt(column)).setText(correctAnswer);

    }

    public View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = correctAnswer;

            if (guess.equals(answer)) {
                ++correctAnswers;

                answerTextView.setText(answer + " is correct!");
                answerTextView.setTextColor(Color.BLACK);

                disableButtons();

                if (correctAnswers == MainActivity.totalQuestions) {

                    answerTextView.setText("Disabling the alarm tone");
                    answerTextView.setTextColor(Color.BLACK);

                    RingtoneService.mediaPlayer.stop();
                    RingtoneService.mediaPlayer.reset();
                    MainActivity.set_alarm_status("Alarm Off!");

                    finish();
                }
                else {
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    animate(true);
                                }
                            }, 650);
                }
            }
            else {
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(Color.RED);
                guessButton.setEnabled(false);
            }
        }

    };


    private void animate(boolean animateOut) {
        if (correctAnswers == 0)
            return;

        int centerX = (quizLinearLayout.getLeft() +
                quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() +
                quizLinearLayout.getBottom()) / 2;

        int radius = Math.max(quizLinearLayout.getWidth(),
                quizLinearLayout.getHeight());

        Animator animator;

        if (animateOut) {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loadNextQuestion();
                        }
                    }
            );
        }
        else {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, 0, radius);
        }
        animator.setDuration(500);
        animator.start();
    }

    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++)
                guessRow.getChildAt(i).setEnabled(false);
        }
    }


}
