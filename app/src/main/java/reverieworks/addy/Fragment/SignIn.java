package reverieworks.addy.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import reverieworks.addy.Activity.MapsActivity;
import reverieworks.addy.R;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static reverieworks.addy.Activity.WelcomeActivity.MyPREFERENCES;


public class SignIn extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInFragment";
    private TextView textView_usernameLogo_signIn;
    private TextView textView_passwordLogo_signIn;
    private EditText editText_username;
    private EditText editText_password;
    private ImageView imageView_facebookLogIn;
    private ImageView imageView_GoogleLogIn;
    private ImageView imageView_TwitterLogIn;
    private Button button_signIn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager callbackManager;

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    public SignIn() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MultiDex.install(getContext());
        mAuth = FirebaseAuth.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getActivity());
        callbackManager = CallbackManager.Factory.create();
        //LoginButton loginButton = (LoginButton) findViewById(R.id.usersettings_fragment_login_button);
        //loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() { ... });
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        //Google SignIn
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf");

        textView_usernameLogo_signIn = (TextView) view.findViewById(R.id.textView_usernameLogo_signIn);
        textView_passwordLogo_signIn = (TextView) view.findViewById(R.id.textView_PasswordLogo_signIn);

        assert textView_usernameLogo_signIn != null;
        textView_usernameLogo_signIn.setTypeface(typeface);

        assert textView_passwordLogo_signIn != null;
        textView_passwordLogo_signIn.setTypeface(typeface);

        textView_usernameLogo_signIn.setText("\uf007");
        textView_passwordLogo_signIn.setText("\uf023");

        //Get Details From User
        editText_username = (EditText) view.findViewById(R.id.editText_Email_SignIn);
        editText_password = (EditText) view.findViewById(R.id.editText_Password_signIn);

        //SignIn
        button_signIn = (Button) view.findViewById(R.id.button_logIn_SignIn);
        button_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editText_username.getText().toString();
                String password = editText_password.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                Toast.makeText(getContext(), "Successfully Logged In", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(getApplicationContext(), Homepage.class));
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                                    Toast.makeText(getContext(), "Auth Failed",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });

        //Facebook LogIn
        imageView_facebookLogIn = (ImageView) view.findViewById(R.id.imageView_FacebookLogo_signIn);
        imageView_facebookLogIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:{

                        //overlay is black with transparency of 0x77 (119)
                        imageView_facebookLogIn.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        imageView_facebookLogIn.invalidate();

                        facebookLogIn(view);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        imageView_facebookLogIn.clearColorFilter();
                        imageView_facebookLogIn.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        //Google LogIn
        imageView_GoogleLogIn = (ImageView) view.findViewById(R.id.imageView_GoogleLogo_signIn);
        imageView_GoogleLogIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        signIn();
                        //overlay is black with transparency of 0x77 (119)
                        imageView_GoogleLogIn.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        imageView_GoogleLogIn.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        imageView_GoogleLogIn.clearColorFilter();
                        imageView_GoogleLogIn.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        //Twitter LogIn
        imageView_TwitterLogIn = (ImageView) view.findViewById(R.id.imageView_TwitterLogo_signIn);
        imageView_TwitterLogIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN: {

                        //overlay is black with transparency of 0x77 (119)
                        imageView_TwitterLogIn.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        imageView_TwitterLogIn.invalidate();

                        twitterLogIn();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        imageView_TwitterLogIn.clearColorFilter();
                        imageView_TwitterLogIn.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void twitterLogIn() {

    }

    private void facebookLogIn(View view) {

            LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
            loginButton.setReadPermissions("email");
            // If using in a fragment
            loginButton.setFragment(this);
            // Other app specific specialization
            loginButton.performClick();
            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Toast.makeText(getContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show();
                    setSessionManagement();
                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                }

                @Override
                public void onCancel() {
                    // App code
                    Toast.makeText(getContext(), "LogIn Failed",
                            Toast.LENGTH_SHORT).show();
                }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getContext(), "LogIn Failed",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                setSessionManagement();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        Toast.makeText(getContext(), "Successfully LoggedIn",
                                Toast.LENGTH_SHORT).show();
                        setSessionManagement();

                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void setSessionManagement() {

        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();

        editor.putString("LOCAL_sessionManagement", String.valueOf(1));
        editor.apply();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });
    }
}