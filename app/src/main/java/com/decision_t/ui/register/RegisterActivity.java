package com.decision_t.ui.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.decision_t.R;
import com.decision_t.base.BaseActivity;
import com.decision_t.ui.table.TableActivity;
import com.decision_t.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private Button confirmButton;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmButton = (Button) findViewById(R.id.confirmButton);

        //初始化Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("註冊帳號");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmRegister();
            }
        });

    }

    private void confirmRegister() {
        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "註冊時，電子信箱或密碼，請勿空白！", Toast.LENGTH_LONG).show();
        } else {
            mProgressDialog.setMessage("Signing up...");
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        //這裡取得了使用者的UID，請服用
                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_db = mDatabase.child(user_id);

                        //這個 name 會存在 Firebase 的資料庫裡，請服用，存在你的資料庫裡
                        current_user_db.child("name").setValue(name);

                        //這個 email 會存在 Firebase 的資料庫裡，請服用，存在你的資料庫裡
                        current_user_db.child("email").setValue(email);

                        mProgressDialog.dismiss();

                        Toast.makeText(RegisterActivity.this, "註冊成功！您的 UID 為 " + user_id, Toast.LENGTH_LONG).show();

                        //註冊成功後，跳轉到主畫面
                        Intent mainIntent = new Intent(RegisterActivity.this, TableActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }
            });
        }

    }

    @Override
    public ActivityRegisterBinding getInflatedBinding() {
        return ActivityRegisterBinding.inflate(getLayoutInflater());
    }
}
