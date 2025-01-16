package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizQuestionActivity extends AppCompatActivity {

    Toolbar toolbar;

    Button submitButton;

    // Quiz Variables
    QuizQuestions quizManager;
    ArrayList<Question> questions;
    String category;
    int currentIndex = 0;
    String[] userAnswers;
    TextView questionNumberLabel;
    TextView questionDisplay;
    ImageView animalImage;
    EditText answerField;
    RadioGroup optionsRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);

        // Set default view; to be overidden (avoids DeadObjectException)
        setContentView(R.layout.activity_animals);

        // Transition animations
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        /* Initialise fields (it makes Android studio happy
        despite it being rereferenced later) DO NOT DELETE
         */
        questionNumberLabel = (TextView) findViewById(R.id.question_number_label);
        questionDisplay = (TextView) findViewById(R.id.question_display);
        answerField = (EditText) findViewById(R.id.answer_field);

        // Get category
        Bundle b = getIntent().getExtras();
        if (!b.isEmpty() && b.containsKey("category")){
            category = getIntent().getStringExtra("category");
        }

        // Set layout depending on Category
        if (category.equals("Animals")){
            setContentView(R.layout.activity_animals);
            questionNumberLabel = (TextView) findViewById(R.id.question_number_label);
            questionDisplay = (TextView) findViewById(R.id.question_display);
            answerField = (EditText) findViewById(R.id.answer_field);
            animalImage = (ImageView) findViewById(R.id.question_image);
        }
        else{
            setContentView(R.layout.activity_cartoons);
            questionNumberLabel = (TextView) findViewById(R.id.question_number_label);
            questionDisplay = (TextView) findViewById(R.id.question_display);
            optionsRadio = (RadioGroup) findViewById(R.id.options_radio_group);
        }

        // Set toolbar and submit button
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(clickListener);


        // Initialise questions and user answer array
        quizManager = new QuizQuestions(this, category);
        questions = (ArrayList<Question>) quizManager.getQuestions();
        userAnswers = new String[]{"", "", "", ""};

        // Set Question to Question 1 (Index 0)
        setQuestions(0);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        //inflate the menu - adds items to the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_quiz_question, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.question_one_button:
                setButtonColours(1);
                setQuestions(0);
                break;
            case R.id.question_two_button:
                setButtonColours(2);
                setQuestions(1);
                break;
            case R.id.question_three_button:
                setButtonColours(3);
                setQuestions(2);
                break;
            case R.id.question_four_button:
                setButtonColours(4);
                setQuestions(3);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener clickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.submit_button:
                            int[] answers = calculateScore();
                            int correctAnswers = answers[0];
                            int incorrectAnswers = answers[1];
                            Intent intent = new Intent(QuizQuestionActivity.this, FinalScoreActivity.class);
                            intent.putExtra("Category", category);
                            intent.putExtra("correctAnswers", correctAnswers);
                            intent.putExtra("incorrectAnswers", incorrectAnswers);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(QuizQuestionActivity.this).toBundle());
                            finish();
                            break;

                    }
                }
            };

    public void setButtonColours(int questionNumber){
        switch (questionNumber){
            case 1:
                toolbar.getMenu().findItem(R.id.question_one_button).setIcon(R.drawable.ic_one);
                toolbar.getMenu().findItem(R.id.question_two_button).setIcon(R.drawable.ic_two_gray);
                toolbar.getMenu().findItem(R.id.question_three_button).setIcon(R.drawable.ic_three_gray);
                toolbar.getMenu().findItem(R.id.question_four_button).setIcon(R.drawable.ic_four_gray);
                break;
            case 2:
                toolbar.getMenu().findItem(R.id.question_one_button).setIcon(R.drawable.ic_one_gray);
                toolbar.getMenu().findItem(R.id.question_two_button).setIcon(R.drawable.ic_two);
                toolbar.getMenu().findItem(R.id.question_three_button).setIcon(R.drawable.ic_three_gray);
                toolbar.getMenu().findItem(R.id.question_four_button).setIcon(R.drawable.ic_four_gray);
                break;
            case 3:
                toolbar.getMenu().findItem(R.id.question_one_button).setIcon(R.drawable.ic_one_gray);
                toolbar.getMenu().findItem(R.id.question_two_button).setIcon(R.drawable.ic_two_gray);
                toolbar.getMenu().findItem(R.id.question_three_button).setIcon(R.drawable.ic_three);
                toolbar.getMenu().findItem(R.id.question_four_button).setIcon(R.drawable.ic_four_gray);
                break;
            case 4:
                toolbar.getMenu().findItem(R.id.question_one_button).setIcon(R.drawable.ic_one_gray);
                toolbar.getMenu().findItem(R.id.question_two_button).setIcon(R.drawable.ic_two_gray);
                toolbar.getMenu().findItem(R.id.question_three_button).setIcon(R.drawable.ic_three_gray);
                toolbar.getMenu().findItem(R.id.question_four_button).setIcon(R.drawable.ic_four);
                break;
        }
    }

    public void setQuestions(int index){

        // Hide button unless on last question
        if(index == 3){
            submitButton.setVisibility(View.VISIBLE);
            submitButton.setEnabled(true);
        }
        else{
            submitButton.setVisibility(View.INVISIBLE);
            submitButton.setEnabled(false);
        }

        if(category.equals("Animals")){

            // Updates the userAnswers array with the current answer field
            // changes currentIndex to parsed index
            // This is for when a new question is selected, the previous questions answer is taken first before rewriting the question
            userAnswers[currentIndex] = answerField.getText().toString();
            currentIndex = index;
            // This took me a long time researching to corresspond a string to an image name but it was worth it (I think, pls give me good marks)
            int imageResource = getResources().getIdentifier(questions.get(index).getImage(), "mipmap", getPackageName());
            animalImage.setImageResource(imageResource);

            // Sets the answerField to equal the current answer for the current index
            // Blank string by default
            answerField.setText(userAnswers[index]);
        }
        else{

            // Get Answers from radio buttons
            int checkedRadioButtonId = optionsRadio.getCheckedRadioButtonId();
            switch (checkedRadioButtonId){
                case R.id.radio_1:
                    userAnswers[currentIndex] = questions.get(currentIndex).getOptions()[0];
                    break;
                case R.id.radio_2:
                    userAnswers[currentIndex] = questions.get(currentIndex).getOptions()[1];
                    break;
                case R.id.radio_3:
                    userAnswers[currentIndex] = questions.get(currentIndex).getOptions()[2];
                    break;
            }

            // Clear radio buttons
            optionsRadio.clearCheck();

            String currentAnswer = userAnswers[index]; // currentIndex
            // Adds labels to current questions
            for (int i = 0; i < optionsRadio.getChildCount(); i++) {
                RadioButton rb = ((RadioButton)(optionsRadio.getChildAt(i)));
                rb.setText(questions.get(index).getOptions()[i]);

                //if(rb.getText().equals(userAnswers[index])){
                if(currentAnswer.equals(questions.get(index).getOptions()[i])){
                    rb.setChecked(true);
                }
                else{
                    rb.setChecked(false);
                }
            }
            currentIndex = index;
        }

        // Set Displays for both cartoons and
        questionNumberLabel.setText("Question " + (index + 1));
        questionDisplay.setText(questions.get(index).getQuestion());

    }

    public int[] calculateScore(){
        int[] answers = new int[]{0,0}; // scores[0] = correct answers, score[1] = incorrect answers

        // Get most recent question answer
        if(category.equals("Animals")){
            userAnswers[currentIndex] = answerField.getText().toString();
        }
        else{
            int checkedRadioButtonId = optionsRadio.getCheckedRadioButtonId();
            switch (checkedRadioButtonId){
                case R.id.radio_1:
                    userAnswers[currentIndex] = questions.get(currentIndex).getOptions()[0];
                    break;
                case R.id.radio_2:
                    userAnswers[currentIndex] = questions.get(currentIndex).getOptions()[1];
                    break;
                case R.id.radio_3:
                    userAnswers[currentIndex] = questions.get(currentIndex).getOptions()[2];
                    break;
            }
        }

        // Determine right and wrong answers
        for(int i = 0; i < userAnswers.length; i++){
            if(userAnswers[i].trim().equalsIgnoreCase(questions.get(i).getAnswer())){
                answers[0] += 1;
            }
            else{
                answers[1] += 1;
            }
        }

        return answers;
    }

    // Prevents users returning to this page from the final page
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}