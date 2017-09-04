package reverieworks.addy.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import reverieworks.addy.R;

public class WelcomeActivity extends AppCompatActivity {

    protected int _splashTime = 2000;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private String local_sessionManagement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layou);
        if (isNetworkAvailable()) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                    //check for session management
                    //Session Management
                    SharedPreferences sharedPref = getSharedPreferences(WelcomeActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                    local_sessionManagement = (sharedPref.getString("LOCAL_sessionManagement", ""));

                    if (local_sessionManagement.compareTo("1") == 0) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAfterTransition();
                        } else {
                            finish();
                        }
                        Intent intent = new Intent(WelcomeActivity.this, reverieworks.addy.Activity.MapsActivity.class);
                        startActivity(intent);
                    } else {
//                        dialog_Settings.show();
                        startActivity(new Intent(getApplicationContext(),reverieworks.addy.Activity.RegistrationActivity.class));
                    }
                }
            }, _splashTime);
        } else {
            Toast.makeText(getApplicationContext(),"No Connetion Found",Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),"No Connetion Found",Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),"No Connetion Found",Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
