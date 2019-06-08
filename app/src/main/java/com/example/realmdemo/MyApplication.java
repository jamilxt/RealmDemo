package com.example.realmdemo;


import android.app.Application;

import com.example.realmdemo.model.Company;
import com.example.realmdemo.model.SocialAccount;
import com.example.realmdemo.model.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmConfiguration.Builder;
import io.realm.annotations.RealmModule;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("MyFirstRealm.realm") // By default the name of db is "default.realm"
                .modules(new MyCustomModule())
                .schemaVersion(2) // Increase it whenever db schema changes
                .migration(new MyMigration())
                .build();

        Realm.setDefaultConfiguration(configuration);

    }

    @RealmModule (classes = {User.class, SocialAccount.class, Company.class})
    public class MyCustomModule {

    }

/*
    public static Realm getAnotherRealm() {

        RealmConfiguration myOtherConfig = new RealmConfiguration.Builder()
                .name("myAnotherRealm.realm")
                .build();
        Realm myAnotherRealm = Realm.getInstance(myOtherConfig);

        return myAnotherRealm;
    }
*/
}
