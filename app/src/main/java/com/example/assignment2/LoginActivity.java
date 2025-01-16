package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //UserManager.fixJSON(this);

        // Find EditText fields
        usernameEditText = findViewById(R.id.enter_username);
        passwordEditText = findViewById(R.id.enter_password);

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(signInListener);

    }

    // Inflate menu toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate the menu - adds items to the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    // Register button
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.register_button:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Sign in button
    private final View.OnClickListener signInListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Load the users
                    UserManager.loadUsers(LoginActivity.this);

                    // Get Username and Password
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    if(username.isEmpty() || password.isEmpty()){ // Prompt user if username or password is empty
                        Toast.makeText(LoginActivity.this, "Username and password cannot be empty", Toast.LENGTH_LONG).show();
                    }
                    else{ // Attempt to login
                        User user = UserManager.login(username, password);
                        if(user != null){ // Successful
                            UserManager.currentUser = user;
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                        }
                        else{ // Unsuccessful
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };
}