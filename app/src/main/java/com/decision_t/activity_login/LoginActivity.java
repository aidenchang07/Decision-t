package com.decision_t.activity_login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.decision_t.databinding.ActivityLoginBinding;
import com.decision_t.manager.DBConnector;
import com.decision_t.R;
import com.decision_t.base.BaseActivity;
import com.decision_t.activity_table.TableActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;

    private ProgressDialog mProgressDialog;

    private SignInButton signInButton;

    //Defining Firebaseauth Object
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseUsers;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //------ Database ------
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        //------ Initializing Firebase Auth Object ------
        FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    // User is signed in
                    /*Intent太多了！正常登入會出現超過三個Intent！留按鈕的就好    by俊憲
                    String displayName = user.getDisplayName();
                    String displayEmail = user.getEmail();

                    //startActivity(new Intent(LoginActivity.this, TableActivity.class));
                    Toast.makeText(LoginActivity.this, "歡迎 " + displayName + " 登入！\n" +
                            "電子信箱 " + displayEmail, Toast.LENGTH_SHORT).show();
                    finish();*/
                } else {
                    // No user is signed in
                }

            }
        };

        //------ Initializing Views ------
        signInButton = (SignInButton) findViewById(R.id.signInButton);

        mProgressDialog = new ProgressDialog(this);

        setGooglePlusButtonText(signInButton, getString(R.string.string_001));

        //------ Google Sigh In 初始化 ------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        //新增 FirebaseAuth 的監聽事件
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            //移除 FirebaseAuth 的監聽事件
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //跑出轉圈圈的畫面
        mProgressDialog.setMessage("Starting Sign in...");
        mProgressDialog.show();

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.v(TAG, "requestCode: " + requestCode);

            //關閉轉圈圈的畫面
            mProgressDialog.dismiss();

            Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                // Signed in successfully, show authenticated UI.
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code = " + e.getStatusCode());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            checkUserExist();
                        }
                    }
                });
    }

    private void signIn() {
        Log.v(TAG, "signIn");
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setTextSize(18);
                return;
            }
        }
    }

    private void checkUserExist() {
        if(mAuth.getCurrentUser() != null){
            final String user_id = mAuth.getCurrentUser().getUid();
            final String user_name = mAuth.getCurrentUser().getDisplayName();
            final String user_email = mAuth.getCurrentUser().getEmail();
            //取得個人照片
            final Uri userPhoto = mAuth.getCurrentUser().getPhotoUrl();

            Intent mainIntent = new Intent(LoginActivity.this, TableActivity.class);
            startActivity(mainIntent);
            saveuid(user_id, user_email, userPhoto.toString());
            finish();
            String sql = "INSERT INTO `Account`" +
                    "       VALUES('"+user_email+"', '"+user_name+"', 'GOOGLE')" +
                    "       ON DUPLICATE KEY UPDATE `Name` = '" + user_name + "';";
            DBConnector.executeQuery(sql);
            //新增資料監聽器
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //資料有變化則執行以下動作
                    if(dataSnapshot.hasChild(user_id)){
                        Intent mainIntent = new Intent(LoginActivity.this, TableActivity.class);
                        startActivity(mainIntent);
                        saveuid(user_id, user_email, userPhoto.toString());
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //資料有錯誤則執行以下動作
                    Toast.makeText(LoginActivity.this, getString(R.string.string_002), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //將使用者資料存起來
    public void saveuid(String uid, String email, String userPhoto)
    {
        try {
            FileOutputStream outStream=this.openFileOutput("uu.txt", Context.MODE_PRIVATE);
            outStream.write(email.getBytes());
            outStream.write(" ".getBytes()); // 分隔用
            outStream.write(userPhoto.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            return;
        }
        catch (IOException e){
            return ;
        }
    }

    @Override
    public ActivityLoginBinding getInflatedBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }
}
