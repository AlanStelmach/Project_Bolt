package com.example.udrive;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = DriverMapActivity.class.getSimpleName();
    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private SupportMapFragment mapFragment;
    private String customerId = "";
    private float rideDistance;

    private LinearLayout mPopupNewRequest;
    private Button mAcceptRequest, mCancelRequest;

    private LinearLayout mCustomerInfo;
    private ImageView mCustomerProfileImage;
    private TextView mCustomerName, mCustomerSurname, mCustomerDestination, mPriceRequest, mPhoneNumber;
    private String priceRequest;

    private Switch mAvailableSwitch;
    private Button mRideStatus;
    private int status = 0;
    private String destination;
    private LatLng destinationLatLng;
    private String data = "com.example.udrive";
    private String returnpoint = "2";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ImageView menu;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        onStart();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer2);
        navigationView = (NavigationView) findViewById(R.id.navigationView2);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        polylines = new ArrayList<>();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mPopupNewRequest = (LinearLayout) findViewById(R.id.popupNewRequest);
        mAcceptRequest = (Button) findViewById(R.id.acceptRequestButton);
        mCancelRequest = (Button) findViewById(R.id.cancelRequestButton);

        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);
        mCustomerProfileImage = (ImageView) findViewById(R.id.customerProfileImage);
        mCustomerName = (TextView) findViewById(R.id.customerName);
        mCustomerSurname = (TextView) findViewById(R.id.customerSurname);
        mCustomerDestination = (TextView) findViewById(R.id.customerDestination);
        mPhoneNumber = (TextView) findViewById(R.id.phoneNumber);
        mPriceRequest = (TextView) findViewById(R.id.priceRequest);

        menu = (ImageView) findViewById(R.id.menuButton2);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView2);
        View headerView = navigationView.getHeaderView(0);
        ImageView profilepic = (ImageView) headerView.findViewById(R.id.profilePic2);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverMapActivity.this, Settings.class);
                intent.putExtra(data, returnpoint);
                startActivity(intent);
            }
        });

        mAvailableSwitch = (Switch) findViewById(R.id.availableSwitch);
        mAvailableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    connectDriver();
                    String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference driverAcceptation = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("Acceptation");
                    driverAcceptation.setValue(false);
                    DatabaseReference driverWorking = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("Working");
                    driverWorking.setValue(true);
                }else{
                    disconnectDriver();
                }
            }
        });

        mRideStatus = (Button) findViewById(R.id.rideStatus);
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(status){
                    case 1://kierowca odbiera klienta
                        status = 2;
                        erasePolylines();
                        if(destinationLatLng.latitude != 0.0 && destinationLatLng.longitude != 0.0){
                            getRouteToMarker(destinationLatLng);
                        }
                        mRideStatus.setText("Request completed");
                        break;
                    case 2: //kierowca jedzie do destination z klientem już w aucie
                        recordRide(); //tworzy rekord historii przejazdu
                        endRide();
                        break;
                }
            }
        });
        getAssignedCustomer();
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
                NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView2);
                View headerView = navigationView.getHeaderView(0);
                TextView name_surname = (TextView) headerView.findViewById(R.id.name_surname2);
                TextView account_balance = (TextView) headerView.findViewById(R.id.account_balance2);
                value = balance;
                name_surname.setText(name + " " + surname);
                account_balance.setText(balance + " PLN");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView2);
        final View headerView2 = navigationView.getHeaderView(0);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("Users_Images").child(uid).child("1");
        reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView profile = (ImageView) headerView2.findViewById(R.id.profilePic2);
                profile.setImageBitmap(bitmap);
            }
        });
    }

    private void getAssignedCustomer(){     //przypisz klienta dla kierowcy
        final String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
        final DatabaseReference driverAcceptation = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("Acceptation");
        final DatabaseReference driverWorking = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("Working");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                        status = 1;
                        customerId = dataSnapshot.getValue().toString();
                        mPopupNewRequest.setVisibility(View.VISIBLE);
                        mAcceptRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {   //Kliknięcie Accept
                            mPopupNewRequest.setVisibility(View.GONE);
                            status = 1;
                            FirebaseDatabase.getInstance().getReference().child("Notifications").child(driverId).child("1").push().setValue("You accepted new request!");
                            driverAcceptation.setValue(true);
                            driverWorking.setValue(true);
                            getAssignedCustomerPickupLocation();
                            getAssignedCustomerDestination();
                            getAssignedCustomerInfo();
                        }
                    });
                    mCancelRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {   //Kliknięcie Cancel
                            mPopupNewRequest.setVisibility(View.GONE);
                            FirebaseDatabase.getInstance().getReference().child("Notifications").child(driverId).child("1").push().setValue("You canceled new request!");
                            endRide();
                        }
                    });
                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;
    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString()); //wyciąganie pierwszej zmiennej x z pozycjio kierowcy
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng pickupLatLng = new LatLng(locationLat,locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_figma))); //tworzenie znacznika w miejsu gdzie znajduje się kierowca
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAssignedCustomerDestination(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination")!=null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    }
                    else{
                        mCustomerDestination.setText("Destination: --");
                    }

                    double destinationLat = 0.0;
                    double destinationLng = 0.0;
                    if(map.get("destinationLat") != null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLng") != null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAssignedCustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null) {
                        mCustomerName.setText(map.get("name").toString());
                    }
                    if(map.get("surname")!=null) {
                        mCustomerSurname.setText(map.get("surname").toString());
                    }
                    if(map.get("pnumber")!=null){
                        mPhoneNumber.setText(map.get("pnumber").toString());
                    }
                    getCustomerImage();
                    getPriceRequest();
                }
            }
            public void getCustomerImage(){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference().child("Users_Images").child(customerId).child("1");
                reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        mCustomerProfileImage.setImageBitmap(bitmap);
                    }
                });
            }
            public void getPriceRequest(){
                DatabaseReference mPriceCustomerRequest =  FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId);
                mPriceCustomerRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if(map.get("price")!=null){
                                mPriceRequest.setText(map.get("price").toString());
                                priceRequest = map.get("price").toString();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();
                                double result = Double.parseDouble(value) + Double.parseDouble(priceRequest);
                                HashMap<String, Object> money = new HashMap<>();
                                money.put("wallet", String.valueOf(result));
                                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(money);
                                onStart();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getRouteToMarker(LatLng pickupLatLng) { //tworzenie lini od lokalizacji kierowcy do lokalizacji klienta
        Routing routing = new Routing.Builder()
                .key("AIzaSyCSypFlR1MFVLajSkBmRqCqvDCeNBA08yA")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                .build();
        routing.execute();
    }

    private void endRide(){
        mRideStatus.setText("Picked customer");
        erasePolylines();
        priceRequest = ("");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverWorking = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("Working");
        driverWorking.setValue(false);
        DatabaseReference driverAcceptation = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("Acceptation");
        driverAcceptation.setValue(false);
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId = "";
        rideDistance = 0;

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (assignedCustomerPickupLocationRefListener != null){
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerSurname.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.drawable.ghost);
        mPhoneNumber.setText("");
        mPriceRequest.setText("");
    }

    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HistoryItem historyDriver = new HistoryItem(userId,"You were there!", destination, priceRequest+" PLN", "Completed");
        HistoryItem historyCustomer = new HistoryItem(userId,"You were there!", destination, priceRequest+" PLN", "Completed");
        FirebaseDatabase.getInstance().getReference().child("History").child(userId).child("1").push().setValue(historyDriver).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                return;
            }
        });
        FirebaseDatabase.getInstance().getReference().child("History").child(customerId).child("1").push().setValue(historyCustomer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                return;
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child("1").push().setValue("You completed new request!");
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(customerId).child("1").push().setValue("Your request has been completed!");

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
        mLocationRequest = new LocationRequest();
        //Będzie się odświeżać lokalizacja co sekundę
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext() != null) {

                    mLastLocation = location;
                    //w tej funkcji o to że chcemy zmienić centralne położenie kamery w momencie gdy użytkownik zmieni swoją pozycję, aby ekran wyśrodkował tą pozycję
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                    //W tej zmiennej będziemy przechowywać aktualnie zalogowanego użytkownika
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    switch (customerId){
                        case "":
                            geoFireWorking.removeLocation(userId);
                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;

                        default:
                            geoFireAvailable.removeLocation(userId);
                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }
                }
            }
        }
    };

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Give permission")
                        .setMessage("Give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                .create()
                .show();
            }
            else{
                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please provide the permission",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private void connectDriver(){
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    private void disconnectDriver(){
        if(mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) { //Błędy co do API
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortesRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            //polyOptions.color(getResources().getColor(R.color.blue));
            polyOptions.color(ResourcesCompat.getColor(getResources(), R.color.blue, null));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.wallet_item2:
            {
                Intent intent = new Intent(DriverMapActivity.this, Wallet_Driver.class);
                intent.putExtra(data, value);
                startActivity(intent);
                break;
            }
            case  R.id.history_item2:
            {
                Intent intent = new Intent(DriverMapActivity.this, History.class);
                intent.putExtra(data, returnpoint);
                startActivity(intent);
                break;
            }
            case R.id.notifications_item2:
            {
                Intent intent = new Intent(DriverMapActivity.this, Notification.class);
                intent.putExtra(data, returnpoint);
                startActivity(intent);
                break;
            }
            case  R.id.reviews_item:
            {
                Intent intent = new Intent(DriverMapActivity.this, Reviews.class);
                startActivity(intent);
                break;
            }
            case  R.id.logout_item2:
            {
                AlertDialog.Builder logout = new AlertDialog.Builder(DriverMapActivity.this);
                LayoutInflater factory = LayoutInflater.from(DriverMapActivity.this);
                final View custom = factory.inflate(R.layout.custom_layout, null);
                logout.setView(custom);
                logout.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(DriverMapActivity.this, "Logging out!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DriverMapActivity.this, SignUp.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                logout.setNeutralButton("Cancel", null);
                logout.setCancelable(true);
                AlertDialog dialog = logout.create();
                dialog.show();
                break;
            }
            default:
            {
                Toast.makeText(DriverMapActivity.this,"Error! But how?!?!", Toast.LENGTH_LONG).show();
                break;
            }
        }
        return false;
    }
}
