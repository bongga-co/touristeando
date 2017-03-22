package co.bongga.touristeando.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import java.util.Arrays;
import co.bongga.touristeando.R;
import co.bongga.touristeando.models.User;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.Globals;
import co.bongga.touristeando.utils.PreferencesManager;
import co.bongga.touristeando.utils.UtilityManager;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDB;
    private DatabaseReference database;

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private GoogleApiClient googleApiClient;

    private ImageButton btnTwitter;
    private ImageButton btnFacebook;
    private ImageButton btnGoogle;
    private Button btnSkip;

    private ProgressDialog loader;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loader = UtilityManager.showLoader(this, getString(R.string.loader_message));
        preferencesManager = new PreferencesManager(this);

        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDB = FirebaseDatabase.getInstance();
        database = firebaseDB.getReference();

        btnTwitter = (ImageButton) findViewById(R.id.btn_twitter_login);
        btnTwitter.setOnClickListener(this);

        btnFacebook = (ImageButton) findViewById(R.id.btn_facebook_login);
        btnFacebook.setOnClickListener(this);

        btnGoogle = (ImageButton) findViewById(R.id.btn_google_login);
        btnGoogle.setOnClickListener(this);

        btnSkip = (Button) findViewById(R.id.btn_skip_login);
        btnSkip.setOnClickListener(this);

        configGoogleSingIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE){
            (new TwitterLoginButton(this)).onActivityResult(requestCode, resultCode, data);
        }
        else if (requestCode == Constants.REQUEST_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                loader.dismiss();

                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                didRequestToken(credential, Constants.GOOGLE_PROVIDER);
            }
            else {
                loader.dismiss();
                UtilityManager.showMessage(btnGoogle, getResources().getString(R.string.login_error_msg));
            }
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if(!UtilityManager.getInstance().isConnected(this)){
            UtilityManager.showMessage(btnGoogle, getResources().getString(R.string.no_network_connection));
            return;
        }

        switch (view.getId()){
            case R.id.btn_twitter_login:
                didLoginToTwitter();
                break;

            case R.id.btn_facebook_login:
                didLoginToFacebook();
                break;

            case R.id.btn_google_login:
                didLoginToGoogle();
                break;
            case R.id.btn_skip_login:
                didOpenHomeView();
                break;
        }
    }

    private void didLoginToTwitter() {
        loader.show();

        TwitterCore.getInstance().logIn(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                loader.dismiss();

                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                final AuthCredential credential = TwitterAuthProvider.getCredential(authToken.token, authToken.secret);

                didRequestToken(credential, Constants.TWITTER_PROVIDER);
            }

            @Override
            public void failure(TwitterException e) {
                loader.dismiss();
                UtilityManager.showMessage(btnTwitter, getResources().getString(R.string.login_error_msg));
            }
        });
    }

    private void didLoginToFacebook(){
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                    didRequestToken(credential, Constants.FACEBOOK_PROVIDER);
                }

                @Override
                public void onCancel() {
                    UtilityManager.showMessage(btnFacebook, getResources().getString(R.string.login_cancel_msg));
                }

                @Override
                public void onError(FacebookException exception) {
                    loader.dismiss();
                    UtilityManager.showMessage(btnFacebook, getResources().getString(R.string.login_error_msg));
                }
            });
    }

    private void didLoginToGoogle(){
        loader.show();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, Constants.REQUEST_GOOGLE_SIGN_IN);
    }

    private void didRequestToken(AuthCredential credential, final String provider){
        loader.show();

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                loader.dismiss();

                if(!task.isSuccessful()){
                    UtilityManager.showMessage(btnGoogle, getResources().getString(R.string.auth_error));
                }
                else{
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    String firstname = null;
                    String lastname = null;

                    try{
                        String splitName[] = firebaseUser.getDisplayName().split(" ");
                        firstname = splitName[0];
                        lastname = splitName[1];
                    }
                    catch (NullPointerException e){
                        firstname = firebaseUser.getDisplayName();
                        lastname = firebaseUser.getDisplayName();
                    }

                    final User user = new User(firstname, lastname, firebaseUser.getEmail(),
                            firebaseUser.getPhotoUrl().toString());

                    DatabaseReference userRef = database.child("users");
                    userRef.child(firebaseUser.getUid()).setValue(user);

                    preferencesManager.setLoggedUser(user);
                    Globals.loggedUser = user;

                    setResult(RESULT_OK);
                    closeView();
                }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loader.dismiss();
                    UtilityManager.showMessage(btnGoogle, getResources().getString(R.string.auth_error));
                }
            });
    }

    private void didOpenHomeView(){
        setResult(RESULT_CANCELED);
        Globals.loggedUser = null;
        closeView();
    }

    private void configGoogleSingIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        UtilityManager.showMessage(btnGoogle, connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void closeView(){
        finish();
    }
}