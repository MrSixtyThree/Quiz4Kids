package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FinalScoreActivity extends AppCompatActivity {

    String category;
    int correctAnswers;
    int incorrectAnswers;
    int score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_score);

        // Transition animation
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        // Toolbar initialisation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button and text initialisation
        TextView outputFinal = (TextView) findViewById(R.id.output_final);

        Button animalsButton = (Button) findViewById(R.id.animals_button);
        Button cartoonsButton = (Button) findViewById(R.id.cartoons_button);
        Button mainMenuButton = (Button) findViewById(R.id.main_button);
        Button logOutButton = (Button) findViewById(R.id.log_out_button);

        animalsButton.setOnClickListener(clickListener);
        cartoonsButton.setOnClickListener(clickListener);
        mainMenuButton.setOnClickListener(clickListener);
        logOutButton.setOnClickListener(clickListener);

        Bundle b = getIntent().getExtras();
        if (b != null){
            category = (String) b.get("Category");
            correctAnswers = (int) b.get("correctAnswers");
            incorrectAnswers = (int) b.get("incorrectAnswers");
        }

        // Get score
        score = calculateScore();

        // Get current date and time
        Date currentDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(currentDate);
        System.out.println(formattedDate);
        UserManager.currentUser.addAttempt(category, formattedDate, score);

        // Update the user with new attempt to JSON file
        UserManager.updateCurrentUserToJSON(this);

        // Final output formatted string.
        String outputFinalText = "";
        if(correctAnswers < 2){
            outputFinalText += "Unlucky ";
        }else{
            outputFinalText += "Well done ";
        }
        outputFinalText += String.format("%s, you have finished the \"%s\" " +
                "quiz with %d correct and %d incorrect answers or %d points for this attempt." +
                "\nOverall you have %d points.", UserManager.currentUser.getUsername(), category, correctAnswers, incorrectAnswers, score,UserManager.currentUser.getTotalPoints());
        outputFinal.setText(outputFinalText);

    }

    public boolean onCreateOptionsMenu(Menu menu){
        //inflate the menu - adds items to the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_final, menu);
        return true;
    }

    private final View.OnClickListener clickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.log_out_button:
                            String text = UserManager.currentUser.getUsername() + ", you have " + UserManager.currentUser.getTotalPoints() + " points overall";
                            Toast.makeText(FinalScoreActivity.this, text, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(FinalScoreActivity.this, LoginActivity.class);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(FinalScoreActivity.this).toBundle());
                            finish();
                            break;
                        case R.id.animals_button:
                            Intent intent2 = new Intent(FinalScoreActivity.this, QuizQuestionActivity.class);
                            intent2.putExtra("category", "Animals");
                            startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(FinalScoreActivity.this).toBundle());
                            finish();
                            break;
                        case R.id.cartoons_button:
                            Intent intent3 = new Intent(FinalScoreActivity.this, QuizQuestionActivity.class);
                            intent3.putExtra("category", "Cartoons");
                            startActivity(intent3, ActivityOptions.makeSceneTransitionAnimation(FinalScoreActivity.this).toBundle());
                            finish();
                            break;
                        case R.id.main_button:
                            Intent intent4 = new Intent(FinalScoreActivity.this, MainActivity.class);
                            startActivity(intent4, ActivityOptions.makeSceneTransitionAnimation(FinalScoreActivity.this).toBundle());
                            finish();
                            break;
                    }
                }
            };

    private int calculateScore(){
        int score = (3*correctAnswers) - incorrectAnswers;
        if(score < 0){score = 0;}
        return score;
    }

    // Prevents users returning to this page
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}