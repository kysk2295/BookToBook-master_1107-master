package com.example.booktobook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.multidex.MultiDex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private EnrollFragment enrollFragment = new EnrollFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private ShowFragment showFragment = new ShowFragment();
    private AlertFragment alertFragment = new AlertFragment();
    private ShelfFragment shelfFragment = new ShelfFragment();
    private Intent serviceIntent;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceIntent!=null){
            stopService(serviceIntent);
            serviceIntent=null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        //첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout,showFragment).commitAllowingStateLoss();

        //bottomNavigationView 의 아이템이 선택될 떄 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()){
                    case R.id.navigation_enroll:{
                        transaction.replace(R.id.frame_layout,enrollFragment).commitAllowingStateLoss();
                        break;
                    }

                    case R.id.navigation_search:{
                        transaction.replace(R.id.frame_layout,searchFragment).commitAllowingStateLoss();
                        break;
                    }

                    case R.id.navigation_show:{
                        transaction.replace(R.id.frame_layout,showFragment).commitAllowingStateLoss();
                        break;
                    }

                    case R.id.navigation_alert:{
                        transaction.replace(R.id.frame_layout,alertFragment).commitAllowingStateLoss();
                        break;
                    }

                    case R.id.navigation_shelf:{
                        transaction.replace(R.id.frame_layout,shelfFragment).commitAllowingStateLoss();
                        break;
                    }
                }

                return true;
            }
        });


//        PowerManager pm=(PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
//        boolean isWhiteListing =false;
//        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.M){
//            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
//
//        }
//        if (!isWhiteListing){
//            Intent intent= new Intent();
//            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//            intent.setData(Uri.parse("package:"+getApplicationContext().getPackageName()));
//            startActivity(intent);
//        }
//        if (RealService.serviceIntent==null) {
//            serviceIntent = new Intent(this, RealService.class);
//            Log.d("hi","service start");
//            startService(serviceIntent);
//        }
//        else{
//            serviceIntent=RealService.serviceIntent;
//            Toast.makeText(getApplicationContext(),"already",Toast.LENGTH_SHORT).show();
//
//        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String newToken=instanceIdResult.getToken();
                Log.d("qwe","새 토큰"+newToken);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                SharedPreferences preferences = getSharedPreferences("pref",MODE_PRIVATE);
                final String ID = preferences.getString("ID","");
                Log.d("id",ID);
                DocumentReference documentReference = db.collection("Users")
                        .document(ID);
                documentReference.update("token",newToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("dd","Sucessful update");
                    }
                });
            }
        });
    }





}
