package com.example.yashc.loginscreen;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    SignInButton signInButton;
    GoogleApiClient apiClient;
    GoogleSignInOptions signInOptions;
    SurfaceView surfaceView;
    MediaPlayer mp;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);

        mp = new MediaPlayer();

        surfaceView = findViewById(R.id.surfaceview);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.video);

                try {
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                            afd.getDeclaredLength());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Get the dimensions of the video
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                //Get the width of the screen
                int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

                //Get the SurfaceView layout parameters
                android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

                //Set the width of the SurfaceView to the width of the screen
                lp.width = screenWidth;

                //Set the height of the SurfaceView to match the aspect ratio of the video
                //be sure to cast these as floats otherwise the calculation will likely be 0
                Display display = getWindowManager().getDefaultDisplay();
                lp.height = display.getHeight();

                //Commit the layout parameters
                surfaceView.setLayoutParams(lp);

                //Start video
                mp.setLooping(true);
                mp.setDisplay(surfaceHolder);
                mp.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API).build();

        signInButton = findViewById(R.id.googleSignIn_button);

        signInButton.setScopes(signInOptions.getScopeArray());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(signInIntent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE){

            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = signInResult.getSignInAccount();

            Toast.makeText(LoginActivity.this, "Welcome: "+account.getDisplayName(), Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
