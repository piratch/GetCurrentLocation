package jirayu.peem.getcurrentlocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
,LocationListener{

    GoogleApiClient googleApiClient;
    TextView textView,temp,description,city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);
        temp = (TextView) findViewById(R.id.temp);
        description = (TextView) findViewById(R.id.description);
        city = (TextView) findViewById(R.id.city);

        googleApiClient = new GoogleApiClient.Builder(this)
                //Select API Connect
                .addApi(LocationServices.API)
                // add Callback
                .addConnectionCallbacks(this)
                // add failed connect Callback
                .addOnConnectionFailedListener(this)
                //pack this to googleApiClient
                .build();



    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if(locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);



        } else {
            Toast.makeText(this,"Open GPS",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        textView.setText("Latitude : " + location.getLatitude() + "\n" +
                "Longitude : " + location.getLongitude());
        final String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(location.getLatitude()) + "&lon=" + String.valueOf(location.getLongitude()) + "&appid=81200d104fc9da3d2b4610388bcfa526";
        makeRequest(url);

    }

    private void makeRequest(String url) {
        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Toast.makeText(MainActivity.this,"Connect Success",Toast.LENGTH_SHORT).show();
                            JSONObject respondFromServer = new JSONObject(response);
                            JSONObject main = respondFromServer.getJSONObject("main");
                            double C =  main.getDouble("temp") - 273.15;
                            temp.setText("อุณหภูมิ : " + String.valueOf(C));
                            JSONArray weather = respondFromServer.getJSONArray("weather");
                            JSONObject a = (JSONObject) weather.get(0);
                            description.setText("เมฆ : " +a.getString("description"));
                            city.setText("เมือง : " + respondFromServer.getString("name"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Request Error !!",Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance().getRequestQueue().add(req);
    }
}
