package com.example.realmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmdemo.model.SocialAccount;
import com.example.realmdemo.model.User;

import java.util.UUID;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText etPersonName, etAge, etSocialAccountName, etStatus;

    private Realm myRealm;
    private RealmAsyncTask realmAsyncTask;

    private RealmResults<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPersonName = findViewById(R.id.etPersonName);
        etAge = findViewById(R.id.etAge);
        etSocialAccountName = findViewById(R.id.etSocialAccount);
        etStatus = findViewById(R.id.etStatus);

        myRealm = Realm.getDefaultInstance();

//        Realm myAnotherRealm = MyApplication.getAnotherRealm();

        Log.i(TAG, "Current Version: " + myRealm.getVersion());

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

    public void sampleQueryExample(View view) {

/*
        RealmQuery<User> realmQuery = myRealm.where(User.class);

        realmQuery.greaterThan("age", 15); // Condition 1
        realmQuery.contains("name", "john", Case.INSENSITIVE); // Condition 2

        RealmResults<User> userList = realmQuery.findAll();
        displayQueriedUsers(userList);
*/

        // Alternatively, let's use Fluid Interface
/*
        RealmResults<User> userList2 = myRealm.where(User.class)
                .between("age", 15, 40) // AND
                .beginGroup()
                    .endsWith("name", "n")
                    .or() // Explicitly Define OR operator
                    .contains("name", "Pe")
                .endGroup()
                .findAll();
*/
        // Chaining Queries
        RealmResults<User> userList2 = myRealm.where(User.class)
                .findAll()
                .sort("socialAccount.name", Sort.DESCENDING);

        displayQueriedUsers(userList2);


        // Update
/*
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                User user = realm.where(User.class).findFirst();
                user.setName("My New Name");
                user.setAge(47);

                SocialAccount socialAccount = user.getSocialAccount();
                if (socialAccount != null) {
                    socialAccount.setName("Snapchat");
                    socialAccount.setStatus("Going for a stroll");
                }

            }
        });
*/

        // Delete
/*
    myRealm.executeTransactionAsync(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {

            User user = realm.where(User.class).findFirst();
            user.deleteFromRealm(); // Delete a specific entry

            RealmResults<User> userList = realm.where(User.class).findAll();
            userList.deleteFirstFromRealm();
            userList.deleteLastFromRealm();
            userList.deleteFromRealm(3);
            userList.deleteAllFromRealm(); // Delete Whole Realm

        }
    });
*/


    }

    public void displayAllUsers(View view) {

        RealmResults<User> userList = myRealm.where(User.class).findAll();

        displayQueriedUsers(userList);

    }

    private void displayQueriedUsers(RealmResults<User> userList) {

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

    public void exploreMiscConcepts(View view) {

        userList = myRealm.where(User.class).findAllAsync();
        userList.addChangeListener(userListListener);

/*
        if (userList.isLoaded()) {
            userList.deleteFirstFromRealm();
        }
*/

    }

    RealmChangeListener<RealmResults<User>> userListListener = new RealmChangeListener<RealmResults<User>>() {
        @Override
        public void onChange(RealmResults<User> userList) {

            displayQueriedUsers(userList);

        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        if (userList != null)
            userList.removeChangeListener(userListListener); // Remove a particular Listener
            // or, userList.removeAllChangeListeners();     // Remove all Registered Listeners

        if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
            realmAsyncTask.cancel();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        myRealm.close();

    }


    public void openDisplayActivity(View view) {

        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);

    }
}
