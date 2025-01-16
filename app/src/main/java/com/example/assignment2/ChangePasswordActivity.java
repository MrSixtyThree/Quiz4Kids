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
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordET;
    private EditText newPasswordET;
    private EditText confirmPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button confirmButton = (Button) findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(clickListener);

        currentPasswordET = (EditText) findViewById(R.id.current_password);
        newPasswordET = (EditText) findViewById(R.id.new_password);
        confirmPasswordET = (EditText) findViewById(R.id.confirm_password);

    }

    // Inflate menu toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate the menu - adds items to the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_help, menu);
        return true;
    }

    // Return button
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.return_button:
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ChangePasswordActivity.this).toBundle());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener clickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.confirm_button:
                            String currentPassword = currentPasswordET.getText().toString();
                            String newPassword = newPasswordET.getText().toString();
                            String confirmPassword = confirmPasswordET.getText().toString();

                            if(newPassword.equals(confirmPassword)){
                                if(UserManager.currentUser.changePassword(ChangePasswordActivity.this, currentPassword, newPassword)){
                                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ChangePasswordActivity.this).toBundle());
                                    break;
                                }
                                else{
                                    Toast.makeText(ChangePasswordActivity.this, "Current passord is incorrect", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(ChangePasswordActivity.this, "New password does not match", Toast.LENGTH_LONG).show();
                            }
                    }
                }
            };


}