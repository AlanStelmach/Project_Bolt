package com.example.udrive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = CustomerMapActivity.class.getSimpleName();
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    private Button mRequest;
    private ImageView menu;
    private LatLng pickupLocation;
    private Boolean requestBol = false;
    private Marker pickupMarker;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private String data = "com.example.udrive";
    private String returnpoint = "1";
    private String user_value;
    private String destination;
    private LatLng destinationLatLng;
    private LinearLayout mDriverInfo;
    private TextView mDriverName, mDriverSurname, mDriverCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mapFragment.getMapAsync(this);
        }else{
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigationView1);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mRequest = (Button) findViewById(R.id.request);
        menu = (ImageView) findViewById(R.id.menuButton);

        destinationLatLng = new LatLng(0.0, 0.0);

        mDriverInfo = (LinearLayout) findViewById(R.id.driverInfo);
        mDriverName = (TextView) findViewById(R.id.driverName);
        mDriverSurname = (TextView) findViewById(R.id.driverSurname);
        mDriverCar = (TextView) findViewById(R.id.driverCar);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView1);
        View headerView = navigationView.getHeaderView(0);
        ImageView profilepic = (ImageView) headerView.findViewById(R.id.profilePic);


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapActivity.this, Settings.class);
                intent.putExtra(data, returnpoint);
                startActivity(intent);
            }
        });

        mapFragment.getMapAsync(this);
        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestBol) {     //zapytanie sprawdzające czy klient kliknął w przycisk aby anulować zamówienie przejazdu
                    endRide();
                } else {
                    requestBol = true;
                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    //Klikając w przycisk klient wysyła dane do bazy danych o tym gdzie się znajduje w danym momencie
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    ;

                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_figma)));

                    mRequest.setText("Getting your driver....");

                    getClosestDriver();
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Users").child(uid).child("name").getValue(String.class);
                String surname = dataSnapshot.child("Users").child(uid).child("surname").getValue(String.class);
                String balance = String.valueOf(dataSnapshot.child("Users").child(uid).child("wallet").getValue(String.class));
                NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView1);
                View headerView = navigationView.getHeaderView(0);
                TextView name_surname = (TextView) headerView.findViewById(R.id.name_surname);
                TextView account_balance = (TextView) headerView.findViewById(R.id.account_balance);
                user_value = balance;
                name_surname.setText(name + " " + surname);
                account_balance.setText(balance + " PLN");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        // Specify the types of place data to return.
        Objects.requireNonNull(autocompleteFragment).setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName();
                destinationLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView1);
        final View headerView = navigationView.getHeaderView(0);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("Users_Images").child(uid).child("1");
        reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView profile = (ImageView) headerView.findViewById(R.id.profilePic);
                profile.setImageBitmap(bitmap);
            }
        });
    }

    private int radius = 10;
    private Boolean driverFound = false;
    private String driverFoundID;
    GeoQuery geoQuery;

    private void getClosestDriver() {        //Funkcja znajdująca kierowce znajdującego się najbliżej klienta
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override //id drivera i pozycja
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol) {
                    driverFound = true;
                    driverFoundID = key;
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (destinationLatLng != null) {
                        HashMap map = new HashMap();
                        map.put("customerRideId", customerId);
                        map.put("destination", destination);
                        map.put("destinationLat", destinationLatLng.latitude);
                        map.put("destinationLng", destinationLatLng.longitude);
                        driverRef.updateChildren(map);
                    }

                    //Show driver location on Customer Map
                    getDriverLocation();
                    getDriverInfo();
                    getHasRideEnded();
                    mRequest.setText("Looking for a Driver Location...");
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound) {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;

    private void getDriverLocation() {   //gdy kierowca zostanie znaleziony jego instancja w bazie danych przejdzie do instancji kierowca pracujący , zmieni status, ten podkierowca poda nam aktualną pozycje kierowcy
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    mRequest.setText("Driver Found");
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString()); //wyciąganie pierwszej zmiennej x z pozycjio kierowcy
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat, locationLng);
                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2); //wyliczenie wartości pomiędzy dwoma punktami

                    if (distance < 100) //zmień napis
                    {
                        mRequest.setText("Driver's Here");
                    } else {
                        mRequest.setText("Driver Found: " + String.valueOf(distance));
                    }

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your driver")); //tworzenie znacznika w miejsu gdzie znajduje się kierowca
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void getDriverInfo() {
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        mDriverName.setText(map.get("name").toString());
                    }
                    if (map.get("surname") != null) {
                        mDriverSurname.setText(map.get("surname").toString());
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;

    private void getHasRideEnded() {
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    endRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void endRide() {
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;
        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (pickupMarker != null) {
            pickupMarker.remove();
        }
        if (mDriverMarker != null) {
            mDriverMarker.remove();
        }
        mRequest.setText("Call for driver");
        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverSurname.setText("");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //w tej funkcji o to że chcemy zmienić centralne położenie kamery w momencie gdy użytkownik zmieni swoją pozycję, aby ekran wyśrodkował tą pozycję
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }


    @Override // Funkcja wczytująca się przy starcie aktywności i odczytująca początkową lokalizacje klienta
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        //Będzie się odświeżać lokalizacja co sekundę
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        //Sprawdzanie czy uzytkownik zezwolił na dostęp do lokalizacji i transmisji danych
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(this);
                }else{
                    Toast.makeText(getApplicationContext(),"Please provide the permission",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.wallet_item:
            {
                Intent intent = new Intent(CustomerMapActivity.this, Wallet.class);
                intent.putExtra(data, user_value);
                startActivity(intent);
                break;
            }
            case R.id.history_item:
            {
                Intent intent = new Intent(CustomerMapActivity.this, History.class);
                startActivity(intent);
                break;
            }
            case R.id.notifications_item:
            {
                Intent intent = new Intent(CustomerMapActivity.this, Notification.class);
                 startActivity(intent);
                break;
            }
            case R.id.promo_item:
            {
                Intent intent = new Intent(CustomerMapActivity.this, Promo_code.class);
                intent.putExtra(data,user_value);
                startActivity(intent);
                break;
            }
            case R.id.logout_item:
            {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(CustomerMapActivity.this, "Logged out!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CustomerMapActivity.this, SignUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            default:
            {
                Toast.makeText(CustomerMapActivity.this,"Error! But how?!?!", Toast.LENGTH_LONG).show();
                break;
            }
        }
        return false;
    }
}