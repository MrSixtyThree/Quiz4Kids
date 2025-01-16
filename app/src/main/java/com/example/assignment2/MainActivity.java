package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Booleans to swap the sorting order
    Boolean dateInvert = true;
    Boolean categoryInvert = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        // Toolbar initialisation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button initialisation
        Button logOutButton = findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(clickListener);

        Button categoryButton = findViewById(R.id.category_button);
        categoryButton.setOnClickListener(clickListener);

        Button dateButton = findViewById(R.id.date_button);
        dateButton.setOnClickListener(clickListener);

        Button cartoonsButton = findViewById(R.id.cartoons_button);
        cartoonsButton.setOnClickListener(clickListener);

        Button animalsButton = findViewById(R.id.animals_button);
        animalsButton.setOnClickListener(clickListener);

        // Test Data to demonstrate sorting
        //UserManager.currentUser.addAttempt("Animals", "15-05-2024 19:34", 4);
        //UserManager.currentUser.addAttempt("Cartoons", "01-07-2024 13:00",8);
        //UserManager.currentUser.addAttempt("Cartoons", "05-08-2024 09:04", 12);

        // Add attempts to linearlayout in scrollview
        updateAttemptsList(UserManager.currentUser.getPreviousAttempts());

    }

    public boolean onCreateOptionsMenu(Menu menu){
        //inflate the menu - adds items to the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    // Help button
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.help_button:
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                return true;
            case R.id.change_password_button:
                Intent intent2 = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener clickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.log_out_button:
                            String text = UserManager.currentUser.getUsername() + ", you have " + UserManager.currentUser.getTotalPoints() + " points overall";
                            Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                            break;
                        case R.id.animals_button:
                            Intent intent2 = new Intent(MainActivity.this, QuizQuestionActivity.class);
                            intent2.putExtra("category", "Animals");
                            startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                            break;
                        case R.id.cartoons_button:
                            Intent intent3 = new Intent(MainActivity.this, QuizQuestionActivity.class);
                            intent3.putExtra("category", "Cartoons");
                            startActivity(intent3, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                            break;
                        case R.id.category_button:
                            sortByCategory();
                            break;
                        case R.id.date_button:
                            sortByDate();
                            break;
                    }
                }
            };


    public void updateAttemptsList(List<Attempt> attempts){
        LinearLayout attemptsContainer = findViewById(R.id.container_attempts);
        attemptsContainer.removeAllViews();

        TextView header = new TextView(this);
        header.setText("Hi " + UserManager.currentUser.getUsername() + ", you have earned " + UserManager.currentUser.getTotalPoints() + " points in the following attempts: ");
        attemptsContainer.addView(header);
        for (Attempt attempt : attempts){
            TextView tv = new TextView(this);
            tv.setText(attempt.toString());
            attemptsContainer.addView(tv);
        }
    }

    public void sortByCategory(){

        List<Attempt> originalAttempts = UserManager.currentUser.getPreviousAttempts();

        // Create a deep copy of the attempts list
        List<Attempt> copiedAttempts = new ArrayList<>();
        for (Attempt originalAttempt : originalAttempts) {
            Attempt copiedAttempt = new Attempt(
                    originalAttempt.getQuizArea(),
                    originalAttempt.getStartTime(),
                    originalAttempt.getPointsEarned()
            );
            copiedAttempts.add(copiedAttempt);
        }

        if(categoryInvert){
            Collections.sort(copiedAttempts, new Comparator<Attempt>(){
                @Override
                public int compare(Attempt a1, Attempt a2){
                    return a1.getQuizArea().compareToIgnoreCase(a2.getQuizArea());
                }
            });
            categoryInvert = false;
        }
        else{
            Collections.sort(copiedAttempts, new Comparator<Attempt>(){
                @Override
                public int compare(Attempt a1, Attempt a2){
                    return a2.getQuizArea().compareToIgnoreCase(a1.getQuizArea());
                }
            });
            categoryInvert = true;
        }

        updateAttemptsList(copiedAttempts);
    }

    public void sortByDate(){

        if(dateInvert){
            List<Attempt> originalAttempts = UserManager.currentUser.getPreviousAttempts();

            //System.out.println("1: " + originalAttempts);

            // Create a deep copy of the attempts list
            List<Attempt> copiedAttempts = new ArrayList<>();
            for (Attempt originalAttempt : originalAttempts) {
                Attempt copiedAttempt = new Attempt(
                        originalAttempt.getQuizArea(),
                        originalAttempt.getStartTime(),
                        originalAttempt.getPointsEarned()
                );
                copiedAttempts.add(copiedAttempt);
            }

            Collections.reverse(copiedAttempts);

            updateAttemptsList(copiedAttempts);
            dateInvert = false;
        }
        else{
            updateAttemptsList(UserManager.currentUser.getPreviousAttempts());
            dateInvert = true;
        }


    }

    // Instantiate sorted list again once returned to from post quiz activity screen
    @Override
    protected void onResume() {
        super.onResume();
        updateAttemptsList(UserManager.currentUser.getPreviousAttempts());
    }
}