package ovh.intellifridge.intellifridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activité de login
 * Appele la classe LoginBackgroundWorker pour les requêtes à la base de données
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail,etPassword;
    AppCompatButton button;
    Button signup_link;
    String email_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText)findViewById(R.id.email_login);
        etPassword = (EditText)findViewById(R.id.password_login);
        button = (AppCompatButton) findViewById(R.id.login_btn);
        signup_link = (Button)findViewById(R.id.link_signup);

        button.setOnClickListener(this);
        signup_link.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email_register = extras.getString("new_user_email");
            etEmail.setText(email_register);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isLogged = sharedPreferences.getBoolean("isLoggedIn",false);
        if (isLogged){
            startMainActivty();
        }
    }

    private void startMainActivty() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                onLogin(view);
                break;
            case R.id.link_signup:
                startRegisterActivity();
                break;
        }
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void onLogin(View view) {
        String user_email, user_pass;
        user_email = etEmail.getText().toString();
        user_pass = etPassword.getText().toString();

        LoginBackgroundWorker backgroundWorker = new LoginBackgroundWorker(this);
        backgroundWorker.execute(user_email,user_pass);
    }
}
