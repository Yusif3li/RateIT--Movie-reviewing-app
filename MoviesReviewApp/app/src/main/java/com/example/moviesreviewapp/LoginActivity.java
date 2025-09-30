package com.example.moviesreviewapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity
{
    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonSignup;
    MoviesDBHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//      deleteDatabase("movieDatabase");
        databaseHelper = new MoviesDBHelper(this);
        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonGoToSignup);

        buttonLogin.setOnClickListener(v -> {
            String emailInput = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString();


            boolean found = false;
            for (String domain : new String[]{"@gmail.com", "@yahoo.com", "@outlook.com"})
            {
                String fullEmail = emailInput.contains("@") ? emailInput : emailInput + domain;
                if (databaseHelper.checkUserLogin(fullEmail, password))
                {
                    found = true;
                    break;
                }
            }

            if (found)
            {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("userId", databaseHelper.fetchUserId(emailInput));
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                startActivity(new Intent(this, MainActivity.class));
            }
            else
            {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }
}
