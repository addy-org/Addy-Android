package reverieworks.addy.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import reverieworks.addy.R;

    public class PlacesAPIActivity extends AppCompatActivity implements
            GoogleApiClient.OnConnectionFailedListener {
        private static final String LOG_TAG = "PlacesAPIActivity";
        private static final int GOOGLE_API_CLIENT_ID = 0;
        private GoogleApiClient mGoogleApiClient;
        private static final int PERMISSION_REQUEST_CODE = 100;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_places_api);
            Button currentButton = (Button) findViewById(R.id.currentButton);
            mGoogleApiClient = new GoogleApiClient.Builder(PlacesAPIActivity.this)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                    .build();
            currentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGoogleApiClient.isConnected()) {
                        if (ContextCompat.checkSelfPermission(PlacesAPIActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(PlacesAPIActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_CODE);
                        } else {
                            callPlaceDetectionApi();
                        }

                    }
                }
            });
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                    + connectionResult.getErrorCode());

            Toast.makeText(this,
                    "Google Places API connection failed with error code:" +
                            connectionResult.getErrorCode(),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE:
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        callPlaceDetectionApi();
                    }
                    break;
            }
        }

        private void callPlaceDetectionApi() throws SecurityException {
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Log.i(LOG_TAG, String.format("Place '%s' with " +
                                        "likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                    likelyPlaces.release();
                }
            });
        }
    }

