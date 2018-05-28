package com.example.android.hangman;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView ques;
    TextView curScore;
    TextView prevHigh;
    TextView usedLetters;
    View root;
    EditText guess;
    Button check;
    ImageView stage;

    String[] words = {"water","juice","plants","spider","winter"
                        ,"money","science","wonder","miss","tea"
                        , "finger","hour","empty","kind","song"
                        , "place","bird","fear","mute","week"};
    String word = words[new Random().nextInt(20)].toUpperCase();
    int[] id = new int[]{R.drawable.hangman0,R.drawable.hangman1,R.drawable.hangman2,R.drawable.hangman3,R.drawable.hangman4,R.drawable.hangman5,
            R.drawable.hangman6};
    ArrayList<String> wrong_guesses=new ArrayList<String>();

    static final int MAX_WRONG  = 12;
    int cur_wrong = 0,cur_score=60,prev_high_score=0;

    SharedPreferences pref;
    SharedPreferences.Editor prefE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ques = (TextView) findViewById(R.id.prompt);
        curScore = (TextView) findViewById(R.id.score);
        prevHigh = (TextView) findViewById(R.id.highScore);
        usedLetters = (TextView) findViewById(R.id.used);
        root = findViewById(R.id.root);
        guess = (EditText) findViewById(R.id.guess);
        check = (Button) findViewById(R.id.check);
        stage = (ImageView) findViewById(R.id.stages);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        prefE = pref.edit();

        prev_high_score = pref.getInt("prevHighScore",0);
        prevHigh.setText(String.valueOf(prev_high_score));


        encrypt(word);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current="";
                try {
                    String test = guess.getText().toString();
                    current = ques.getText().toString();
                    ques.setText("");
                    guess.setText("");
                    reveal(word, test, current);
                } catch (Exception ignore) {
                    ques.setText(current);
                }
            }
        });

        root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cur_score=60;curScore.setText(String.valueOf(cur_score));cur_wrong=0;
                prev_high_score=pref.getInt("prevHighScore",0);prevHigh.setText(String.valueOf(prev_high_score));
                stage.setImageDrawable(getResources().getDrawable(id[0]));
                guess.setEnabled(true);
                usedLetters.setText("");
                ques.setText("");
                word = words[new Random().nextInt(20)].toUpperCase();
                wrong_guesses.clear();
                encrypt(word);
                return false;

            }
        });
    }

    public void encrypt(String word){
        for(int i=0;i<word.length();i++)
            ques.append("*");
    }
    public void reveal(String word,String test,String current){
        if(word.indexOf(test)==-1) {
            ques.setText(current);
            if (cur_score == 0) {
                ques.setText("YOU LOST, LONG PRESS TO RESTART");
                guess.setEnabled(false);
            }
            if(!wrong_guesses.contains(test)) {
                wrong_guesses.add(test);
                usedLetters.append(test + "  ");
                cur_wrong += 1;
                cur_score = (12 - cur_wrong) * 5;
                curScore.setText(String.valueOf(cur_score));
                stage.setImageDrawable(getResources().getDrawable(id[cur_wrong / 2]));
            }
            else
                makeToast("Incorrect Guess has already been made");
        }
        else {
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == test.charAt(0))
                    ques.append(String.valueOf(word.charAt(i)));
                else if (current.charAt(i) == '*')
                    ques.append("*");
                else
                    ques.append(String.valueOf(current.charAt(i)));
            }
            if (ques.getText().toString().equals(word)) {
                if(cur_score>prev_high_score){
                    prevHigh.setText(String.valueOf(cur_score));
                    prev_high_score=cur_score;
                    prefE.putInt("prevHighScore",prev_high_score);
                    prefE.commit();
                    makeToast("NEW HIGH SCORE!!");
                }
                else
                    makeToast("YEAH ! YOU FOUND IT");
                guess.setEnabled(false);
            }
        }

    }
    public void makeToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulist1,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(MainActivity.this,RulesActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
