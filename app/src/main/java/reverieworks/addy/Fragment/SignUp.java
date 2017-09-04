package reverieworks.addy.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import reverieworks.addy.Activity.MapsActivity;
import reverieworks.addy.R;

import static com.facebook.FacebookSdk.getApplicationContext;
import static reverieworks.addy.R.id.textView_userLogo_signUp;


public class SignUp extends Fragment{

    private static final String TAG = "SignUpFragment";
    private TextView textView_userLogo;
    private TextView textView_EmailLogo_signUp;
    private TextView textView_PasswordLogo_signUp;
    private TextView textView_ViewPassword_signUp;
    private EditText editText_username;
    private EditText editText_password;
    private EditText editText_email;
    private Button button_signUp;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public SignUp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf");

        textView_userLogo = (TextView) view.findViewById(textView_userLogo_signUp);
        textView_EmailLogo_signUp = (TextView) view.findViewById(R.id.textView_EmailLogo_signUp);
        textView_PasswordLogo_signUp = (TextView) view.findViewById(R.id.textView_PasswordLogo_signUp);
        textView_ViewPassword_signUp = (TextView) view.findViewById(R.id.textView_ViewPassword_signUp);

        assert textView_userLogo != null;
        textView_userLogo.setTypeface(typeface);

        assert textView_EmailLogo_signUp != null;
        textView_EmailLogo_signUp.setTypeface(typeface);

        assert textView_PasswordLogo_signUp != null;
        textView_PasswordLogo_signUp.setTypeface(typeface);

        assert textView_ViewPassword_signUp != null;
        textView_ViewPassword_signUp.setTypeface(typeface);

        textView_userLogo.setText("\uf007");
        textView_EmailLogo_signUp.setText("\uf0e0");
        textView_PasswordLogo_signUp.setText("\uf023");
        textView_ViewPassword_signUp.setText("\uf06e");
        textView_ViewPassword_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textView_ViewPassword_signUp.getText().toString().compareTo("\uf06e")==0)
                    textView_ViewPassword_signUp.setText("\uf070");
                else
                    textView_ViewPassword_signUp.setText("\uf06e");
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //Get Details From The User (Input)
        editText_username = (EditText) view.findViewById(R.id.editText_Name_SignUp);
        editText_email = (EditText) view.findViewById(R.id.editText_Email_SignUp);
        editText_password = (EditText) view.findViewById(R.id.editText_Password_SignUp);

        //SignUp
        button_signUp = (Button) view.findViewById(R.id.button_signUp_signUp);
        button_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = editText_email.getText().toString();
                String password = editText_password.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                Toast.makeText(getContext(), "Sign Up Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });

            }
        });

        return view;
    }

    private void createAccount(String email, String password) {

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


}
