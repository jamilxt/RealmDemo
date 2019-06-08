package com.example.realmdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmdemo.model.SocialAccount;
import com.example.realmdemo.model.User;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText etPersonName, etAge, etSocialAccountName, etStatus;

    private Realm myRealm;
    private RealmAsyncTask realmAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPersonName = findViewById(R.id.etPersonName);
        etAge = findViewById(R.id.etAge);
        etSocialAccountName = findViewById(R.id.etSocialAccount);
        etStatus = findViewById(R.id.etStatus);

        myRealm = Realm.getDefaultInstance();

    }

    // Add data to Realm using Main UI Thread. Be Careful: As it may BLOCK the UI.
    public void addUserToRealm_Synchronously(View view) {

        final String id = UUID.randomUUID().toString();
        final String name = etPersonName.getText().toString();
        final int age = Integer.valueOf(etAge.getText().toString());
        final String socialAccountName = etSocialAccountName.getText().toString();
        final String status = etStatus.getText().toString();

//        try {
//
//            myRealm.beginTransaction();
//
//
//            myRealm.commitTransaction();
//
//        } catch (Exception e) {
//
//            myRealm.cancelTransaction();
//
//        }

        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                SocialAccount socialAccount = realm.createObject(SocialAccount.class);
                socialAccount.setName(socialAccountName);
                socialAccount.setStatus(status);

                User user = realm.createObject(User.class, id);
                user.setName(name);
                user.setAge(age);
                user.setSocialAccount(socialAccount);

//                Toast.makeText(MainActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                // no callback to know if the data successfully or not.
            }
        });

    }

    // Add Data to Realm in the Background Thread.
    public void addUserToRealm_ASynchronously(View view) {

        final String id = UUID.randomUUID().toString();
        final String name = etPersonName.getText().toString();
        final int age = Integer.valueOf(etAge.getText().toString());
        final String socialAccountName = etSocialAccountName.getText().toString();
        final String status = etStatus.getText().toString();

        realmAsyncTask = myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                SocialAccount socialAccount = realm.createObject(SocialAccount.class);
                socialAccount.setName(socialAccountName);
                socialAccount.setStatus(status);

                User user = realm.createObject(User.class, id);
                user.setName(name);
                user.setAge(age);
                user.setSocialAccount(socialAccount);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

                Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void displayAllUsers(View view) {

        RealmResults<User> userList = myRealm.where(User.class).findAll();

        StringBuilder builder = new StringBuilder();

        for (User user : userList) {

            builder.append("ID: ").append(user.getId());
            builder.append("\nName: ").append(user.getName());
            builder.append("\nAge: ").append(user.getAge());

            SocialAccount socialAccount = user.getSocialAccount();
            builder.append("\nS'Account: ").append(socialAccount.getName());
            builder.append(", Status: ").append(socialAccount.getStatus()).append(" .\n\n");

        }

        Log.i(TAG + " Lists", builder.toString());

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
            realmAsyncTask.cancel();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        myRealm.close();

    }
}
