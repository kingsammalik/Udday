package com.samapps.udday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button submit, login;
    EditText phone,otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (SharedPrefUtils.isLogged(this)){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        createIds();
        submit.setOnClickListener(view -> {
            if (phone.getText().toString().isEmpty()){
                Toast.makeText(this, "Phone number is empty",Toast.LENGTH_SHORT).show();
            }
            else {
                submit.setVisibility(View.GONE);
                otp.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
            }
        });

        login.setOnClickListener(view -> {
            if (otp.getText().toString().isEmpty()){
                Toast.makeText(this, "OTP is empty",Toast.LENGTH_SHORT).show();
            }
            else {
                SharedPrefUtils.saveLogin(this);
                startActivity(new Intent(this,MainActivity.class));
                finish();
            }
        });
    }

    private void createIds() {
        submit = findViewById(R.id.submit);
        login = findViewById(R.id.login);
        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
    }
}