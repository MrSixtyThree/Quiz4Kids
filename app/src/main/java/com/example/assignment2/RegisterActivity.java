package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find EditText fields
        usernameEditText = findViewById(R.id.enter_username);
        passwordEditText = findViewById(R.id.enter_password);
        emailEditText = findViewById(R.id.enter_email);

        Button signInButton = findViewById(R.id.register_button);
        signInButton.setOnClickListener(registerListener);

        //UserManager.testUserLoading(this);
    }

    // Inflate menu toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate the menu - adds items to the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }

    // Back button
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.back_button:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Register Button
    private final View.OnClickListener registerListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    UserManager.loadUsers(RegisterActivity.this);

                    // Get Username, Password and Email
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String email = emailEditText.getText().toString();

                    if(username.isEmpty() || password.isEmpty() || email.isEmpty()){ // Prompt user if username, password or email is empty
                        Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                    }
                    else{
                        if(UserManager.addUser(RegisterActivity.this, username,email,password)){
                            Toast.makeText(RegisterActivity.this, "Account registered successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this).toBundle());
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Account with username or email already exists", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };
}