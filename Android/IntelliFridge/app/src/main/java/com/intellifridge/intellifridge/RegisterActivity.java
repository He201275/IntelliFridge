package com.intellifridge.intellifridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etFname,etLname,etEmail,etPassword;
    AppCompatButton register_btn;
    Button login_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFname = (EditText) findViewById(R.id.fName);
        etLname = (EditText) findViewById(R.id.lName);
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        register_btn = (AppCompatButton) findViewById(R.id.btnRegister);
        login_link = (Button) findViewById(R.id.login_link);

        register_btn.setOnClickListener(this);
        login_link.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegister:
                onRegister(v);
                break;
            case R.id.login_link:
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void onRegister(View view){
        String fName = etFname.getText().toString();
        String lName = etLname.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String type = "register";

        new BackgroundWorker(this).execute(type,email,password,fName,lName);
    }

}
