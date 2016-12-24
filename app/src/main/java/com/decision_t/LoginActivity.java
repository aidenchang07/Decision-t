package com.decision_t;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;

    private SignInButton signInButton;

    //Defining Firebaseauth Object
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //------ Database ------
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        //------ Initializing Firebase Auth Object ------
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

        setGooglePlusButtonText(signInButton, "使用 Google 登入");

        //------ Google Sigh In 初始化 ------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            //關閉轉圈圈的畫面
            mProgressDialog.dismiss();
            if (result.isSuccess()) {
                //Google Sign In 登入成功，則取得使用者的資料
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
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

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

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
            Intent mainIntent = new Intent(LoginActivity.this, TableActivity.class);
            startActivity(mainIntent);
            saveuid(user_id, user_email);
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
                        saveuid(user_id, user_email);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //資料有錯誤則執行以下動作
                    Toast.makeText(LoginActivity.this, "DatabaseError.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //將使用者資料存起來
    public void saveuid(String uid, String email)
    {
        try {
            FileOutputStream outStream=this.openFileOutput("uu.txt", Context.MODE_PRIVATE);
            outStream.write(email.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            return;
        }
        catch (IOException e){
            return ;
        }
    }



}
