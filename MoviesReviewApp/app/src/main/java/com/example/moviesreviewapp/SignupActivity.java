package com.example.moviesreviewapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    EditText editTextName, editTextEmail, editTextPassword;
    Spinner spinnerGender;
    RadioGroup radioGroupRole;
    TextView textViewGuidelines;
    Button buttonSignup;
    MoviesDBHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseHelper = new MoviesDBHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerGender = findViewById(R.id.spinnerGender);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        textViewGuidelines = findViewById(R.id.textViewGuidelines);
        buttonSignup = findViewById(R.id.buttonSignup);


        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_options, R.layout.spinner_text_out);
        genderAdapter.setDropDownViewResource(R.layout.spinner_text_out);
        spinnerGender.setAdapter(genderAdapter);

        editTextPassword.addTextChangedListener(new TextWatcher()
        {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                updateGuidelines(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        buttonSignup.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString();
            String gender = spinnerGender.getSelectedItem().toString();

            int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();
            RadioButton selectedRoleButton = findViewById(selectedRoleId);
            String role = selectedRoleButton != null ? selectedRoleButton.getText().toString() : "";

            if (!isValidFullName(name))
            {
                editTextName.setError("Invalid full name. Only letters and spaces allowed (2-50 chars).");
                return;
            }
            if (!isValidEmail(email))
            {
                editTextEmail.setError("Invalid email. Must contain domain (e.g., @gmail.com, @yahoo.com).");
                return;
            }

            if (!isValidPassword(password))
            {
                editTextPassword.setError("Password must have 8+ chars, number, special char, uppercase.");
                return;
            }
            if (!isValidRole(role))
            {
                Toast.makeText(this, "ERROR: please Select your role !", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.checkUserExists(email))
            {
                Toast.makeText(this, "ERROR: Email already used!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = databaseHelper.insertNewUser(name, email, password, gender, role);
            if (success)
            {
                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(this, "ERROR: DB insertion failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidFullName(String fullName)
    {
        if (fullName.length() < 2 || fullName.length() > 50) return false;
        return fullName.matches("[A-Za-z ]+");
    }

    private boolean isValidEmail(String email)
    {
        return email.matches("^[A-Za-z0-9+_.-]+@(gmail|yahoo|outlook)\\.com$");
    }

    private boolean isValidPassword(String password)
    {
        String pattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }
    private boolean isValidRole(String role)
    {
        if(role.isEmpty())
        {
            return false;
        }
        else
        {
            return true ;
        }
    }

    private void updateGuidelines(String password)
    {
        StringBuilder sb = new StringBuilder("Password must:\n");
        sb.append(password.length() >= 8 ? "✅ At least 8 characters\n" : "❌ At least 8 characters\n");
        sb.append(password.matches(".*[A-Za-z].*") ? "✅ Contain a letter\n" : "❌ Contain a letter\n");
        sb.append(password.matches(".*[0-9].*") ? "✅ Contain a number\n" : "❌ Contain a number\n");
        sb.append(password.matches(".*[!@#$%^&*].*") ? "✅ Contain a special character\n" : "❌ Contain a special character\n");
        sb.append(password.matches(".*\\s.*") ? "❌ No spaces allowed\n" : "✅ No spaces\n");
        textViewGuidelines.setText(sb.toString());
    }

}
