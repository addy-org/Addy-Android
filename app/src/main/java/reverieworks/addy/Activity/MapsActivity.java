package reverieworks.addy.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import reverieworks.addy.R;

import static reverieworks.addy.R.id.map;

public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final int REQUEST_CHECK_SETTINGS = 1000;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "MaspActivity";
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private String receive_current_latitude;
    private String receive_current_longitude;
    private Button button_searchByPlaces;
    private Button button_searchByACode;
    private SearchView searchView;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private String latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_main);

        assert getSupportActionBar() != null;
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Addy");
        }

        //progressbar
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);

        // Create an instance of GoogleAPIClient. To get current location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        button_searchByACode = (Button) findViewById(R.id.button_SearchByACode);
        button_searchByACode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = MapsActivity.this;
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        Toast.makeText(getApplicationContext(), userInput.getText(), Toast.LENGTH_LONG).show();
                                        if (isLegalACodeCustomName(userInput.getText().toString()))
                                            new GetACodeFromCustomName().execute(userInput.getText().toString());
                                        else {


                                            String latlng2 = convertback(userInput.getText().toString());
                                            String[] latlong =  latlng2.split(",");
                                            double latitude = Double.parseDouble(latlong[0]);
                                            double longitude = Double.parseDouble(latlong[1]);
                                            LatLng location = new LatLng(latitude, longitude);
                                            createMarker(userInput.getText().toString(),location,latitude,longitude);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        button_searchByPlaces = (Button) findViewById(R.id.button_SearchByPlace);
        button_searchByPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
/*


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

*/

    }


    public boolean isLegalACodeCustomName(String acode) {

        for (int i = 0; i < acode.length(); i++)
            if (acode.charAt(0) >= 97 && acode.charAt(0) <= 122)
                return true;
        return false;

    }

    class GetACodeFromCustomName extends AsyncTask<String, Void, String> {

        int responseCode;
        String JsonResponse;

        protected void onPreExecute() {

            progressDialog.setMessage("Searching...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String data_value = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {

                URL url = new URL("https://addydatabase-a4e8.restdb.io/rest/addydata?q={\"Special_Name\":\"" + data_value + "\"}");
                urlConnection = (HttpURLConnection) url.openConnection();
                Log.d(TAG, "Connected To : " + String.valueOf(url));
//set headers
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("x-apikey", "0e835d0ce5cc81e9ae08fb1f7ac2392ad04ee");
                urlConnection.setRequestProperty("cache-control", "no-cache");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
//0
//get response code
                responseCode = urlConnection.getResponseCode();
                android.util.Log.e(TAG, "ResponseMessage " + urlConnection.getResponseMessage() + " ; ResponseCode " + urlConnection.getResponseCode());

                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    //  android.util.Log.e(TAG, "InputStream Is Null");
                    return null;

                }
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine).append("\n");

                if (buffer.length() == 0) {
                    //android.util.Log.e(TAG, "Stream was empty. No point in parsing.");
                    return null;
                } else {

                    JsonResponse = buffer.toString();
//response data
                    //android.util.Log.i(TAG, "doInBackGround() " + JsonResponse);
                    return JsonResponse;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        android.util.Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String data) {

            //android.util.Log.i(TAG, "no return");
            if (data == null) {
                android.util.Log.e(TAG, "Error Null");
            }
            progressDialog.dismiss();
            String message_alertBox;
            String title_alertBox;

            Log.e("TAG1", data);
            LatLngBounds.Builder latlng = new LatLngBounds.Builder();

            if (responseCode == 200) {

                android.util.Log.i(TAG, "Response Code = 200.... " + data);

                try {
                    android.util.Log.e(TAG, "Inside try");

                    //if(data_currentRadius == null)

                    //Convert String to JSON Data
                    JSONArray jsonObject_mainArray = (JSONArray) new JSONTokener(data).nextValue();
                    JSONObject jsonObject_main = jsonObject_mainArray.getJSONObject(0);
                    ACodeJSON object;


                    String latlng2 = convertback(jsonObject_main.getString("Acode"));
                    String[] latlong =  latlng2.split(",");
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);
                    LatLng location = new LatLng(latitude, longitude);
                    createMarker(jsonObject_main.getString("Acode"),location,latitude,longitude);

                    Log.e("TAG", latlng2);

                    //if(jsonObject.getString())


                } catch (JSONException e) {
                    // Log.e(TAG, "ERROR PARSING DATA");
                    e.printStackTrace();
                }
                return;

            } else if (responseCode == 401) {
                message_alertBox = "Internal Server Error";
                title_alertBox = "Error";
                //android.util.Log.e(TAG, "Response Code = 401");
            } else {
                //   android.util.Log.e(TAG, "JSON Response returns =" + data);
                title_alertBox = "Failed";
                message_alertBox = "Some Error Occured";
            }

            //android.util.Log.i("INFO", "data" + data);

            showMessageDialog(title_alertBox, message_alertBox);
        }
    }

    private void createMarker(String acode, LatLng location, double latitude, double longitude) {
        Marker mMarker = null;
        if (acode!= null) {

            mMap.setOnMarkerClickListener(this);
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).anchor(0.5f, 0.5f).title(acode));
            //mMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pegman));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 10);
            mMap.animateCamera(cameraUpdate);

        }
    }


    class SendCustomName extends AsyncTask<String, Void, String> {

        int responseCode;
        String JsonResponse;

        protected void onPreExecute() {

            progressDialog.setMessage("Sending custom name...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String special_name = params[0];
            String acode = params[1];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://addydatabase-a4e8.restdb.io/rest/addydata");
                urlConnection = (HttpURLConnection) url.openConnection();

                JSONObject JSON_Main = new JSONObject();

                try {

                    JSON_Main.put("UserName", "username");
                    JSON_Main.put("Acode",acode);
                    JSON_Main.put("Special_Name",special_name);
                    //JSON_Main.put()
                    // Log.e(TAG, JSON_Main.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//set headers

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("x-apikey","0e835d0ce5cc81e9ae08fb1f7ac2392ad04ee");
                urlConnection.setRequestProperty("cache-control","no-cache");
                urlConnection.setRequestProperty("CONTENT-TYPE","application/json");
                urlConnection.connect();

//set headers and method
                DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
                Log.i(TAG, JSON_Main.toString());
                writer.writeBytes(JSON_Main.toString());
// json data
                android.util.Log.e(TAG, "ResponseMessage " + urlConnection.getResponseMessage() + "ResponseCode " + urlConnection.getResponseCode());
                responseCode = urlConnection.getResponseCode();
                writer.flush();
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    // Log.e(TAG, "InputStream Is Null");
                    return null;

                }
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine).append("\n");

                if (buffer.length() == 0) {
                    // Log.e(TAG, "Stream was empty. No point in parsing.");
                    return null;
                } else {

                    JsonResponse = buffer.toString();
//response data
                    // Log.i(TAG, "doInBackGround() " + JsonResponse);
                    return JsonResponse;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        // Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }


        protected void onPostExecute(String data) {

//            android.util.Log.i(TAG, data);
            if (data == null) {
                // Log.e(TAG, "Error Null");
            }

            progressDialog.dismiss();
            String message_alertBox;
            String title_alertBox;

            if (responseCode == 201) {

                title_alertBox = "Request Sent";
                message_alertBox = "Please Wait For The Response";
                //TODO: add this user to the list,

            } else if (responseCode == 400) {

                message_alertBox = "Error";
                title_alertBox = "User has different Blood group";
                // Log.e(TAG, "Response Code = 400");
            } else if (responseCode == 409) {

                message_alertBox = "Conflict";
                title_alertBox = "You cannot request yourself";
                // Log.e(TAG, "Response Code = 400");
            } else if (responseCode == 404) {

                message_alertBox = "Not Found";
                title_alertBox = "Donor Not Found";
                // Log.e(TAG, "Response Code = 400");
            } else {

                // Log.e(TAG, "JSON Response returns =" + data);

                message_alertBox = "Error";
                title_alertBox = "Internal Server Error";
                // Log.i("INFO", "data" + data);
            }
            showMessageDialog(title_alertBox, message_alertBox);
        }


    }

    private void showMessageDialog(String title, String message) {


        android.app.AlertDialog.Builder builderSingle;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            builderSingle = new android.app.AlertDialog.Builder(MapsActivity.this);
        } else {
            builderSingle = new android.app.AlertDialog.Builder(MapsActivity.this);
        }

//        AlertDialog.Builder builderSingle = new AlertDialog.Builder(FirstTimeLogIn.this,R.style.MyAlertDialogStyle);
        builderSingle.setMessage(message);
        builderSingle.setTitle(title);
        builderSingle.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builderSingle.show();


    }

    String convertback(String Acode) {
        StringBuilder ss = new StringBuilder("");
        return ss.append((Integer.parseInt(Acode.substring(0, 4), 36) * 1.0 / 10000) + 7.4).append(",").append((Integer.parseInt(Acode.substring(4, 8), 36) * 1.0 / 10000) + 67.5).toString();
    }

    String convert(double lati, double longi) {
        int l = (int) (Math.round(longi * 10000) - 675000);
        int la = (int) (Math.round(lati * 10000) - 74000);
        return (Integer.toString(la, 36) + Integer.toString(l, 36)).toUpperCase();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        enableMyLocation();
    }
    private boolean getLatitudeAndLongitude() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        // Create an instance of GoogleAPIClient. To get current location
        //  Log.e(TAG, "check " + mGoogleApiClient);
        //if (mGoogleApiClient == null) {
        //}


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation == null) {
            //    Log.e(TAG, "Last Location Was Null");
            displayLocationSettingsRequest(mGoogleApiClient);
        } else {

            receive_current_latitude = String.valueOf(mLastLocation.getLatitude());
            receive_current_longitude = String.valueOf(mLastLocation.getLongitude());
            //dialog_Settings.show();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 6.0f));
            //        Log.e(TAG, "LATITUDE = " + receive_current_latitude);
            //      Log.e(TAG, "LONGITUDE = " + receive_current_longitude);
        }

        return true;
    }

    //get current location without navigating to the settings page
    private void displayLocationSettingsRequest(GoogleApiClient mGoogleApiClient) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // Log.i(TAG, "All location settings are satisfied." + status);
                        getLatitudeAndLongitude();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings " + status);

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        // Log.e(TAG, "2Last Location Was Denied");
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Log.d(TAG, "resultCode=" + Integer.toString(resultCode));
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (resultCode) {
            case Activity.RESULT_OK: {
                // All required changes were successfully made
                // Log.i(TAG, "Location enabled by user!");
                if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.i(TAG, "Place: " + place.getLatLng());
                    createMarkerLatLng(convert(place.getLatLng().latitude,place.getLatLng().longitude),place.getLatLng());
                    Toast.makeText(getApplicationContext(),place.getName(),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Location Enabled", Toast.LENGTH_SHORT).show();
                    enableMyLocation();
                    startActivity(new Intent(MapsActivity.this, MapsActivity.class));
                }break;
            }
            case Activity.RESULT_CANCELED: {
                // The user was asked to change settings, but chose not to
                //TODO: display the below code
                Toast.makeText(getApplicationContext(),"No Connection Found",Toast.LENGTH_SHORT).show();
                /*Snackbar snackbar = Snackbar.make(, "No Connection Found", Snackbar.LENGTH_INDEFINITE)
                        .setAction("GO OFFLINE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                */// Log.i(TAG, "Location not enabled, user cancelled.");
                break;
            }
            default: {
                // Log.e(TAG, "Error in selecting the button in Auto-Switch On Location Feature");
                break;
            }
        }
    }

    private void createMarkerLatLng(CharSequence name, LatLng latLng_local) {
        Marker mMarker = null;
        if (name!= null) {

            mMap.setOnMarkerClickListener(this);
            mMap.addMarker(new MarkerOptions().position(latLng_local).anchor(0.5f, 0.5f).title(name.toString()));
            //mMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pegman));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng_local, 10);
            mMap.animateCamera(cameraUpdate);


        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
//            PermissionUtils.requestPermission(getApplicationContext(), LOCATION_PERMISSION_REQUEST_CODE,
  //                  Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        getCustomName(marker);
        return false;
    }

    private void getCustomName(final Marker marker) {

        Context context = MapsActivity.this;
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                new SendCustomName().execute(userInput.getText().toString(),convert(marker.getPosition().latitude,marker.getPosition().longitude));
                                Toast.makeText(getApplicationContext(), userInput.getText(), Toast.LENGTH_LONG).show();

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);

        // Retrieve the SearchView and plug it into SearchManager
      /*  final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
*/
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addCustomName:
                // TODO put your code here to respond to the button tap
                Toast.makeText(getApplicationContext(), "ADD!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
