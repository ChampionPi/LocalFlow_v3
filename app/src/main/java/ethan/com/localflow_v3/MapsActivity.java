package ethan.com.localflow_v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.LinkedList;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    double latitude, longitude;

    private GoogleMap mMap;
    Boolean Is_MAP_Moveable = true; // to detect map is movable
    Projection projection;
    FrameLayout fram_map;
    Button btn_draw_State;
    static LinkedList<LatLng> val = new LinkedList<>();
    //Button btn_begin_draw;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fram_map = (FrameLayout) findViewById(R.id.fram_map);
        btn_draw_State = (Button) findViewById(R.id.btn_draw_State);

        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Is_MAP_Moveable = !Is_MAP_Moveable;
                if (Is_MAP_Moveable == false) {
                    fram_map.setOnTouchListener(new View.OnTouchListener() {     @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            float x = event.getX();
                            float y = event.getY();

                            int x_co = Math.round(x);
                            int y_co = Math.round(y);

                            projection = mMap.getProjection();
                            Point x_y_points = new Point(x_co, y_co);

                            LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
                            latitude = latLng.latitude;
                            longitude = latLng.longitude;

                        int eventaction = event.getAction();
                            switch (eventaction) {
                                case MotionEvent.ACTION_DOWN:
                                    // finger touches the screen
                                    val.add(latLng);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    // finger moves on the screen
                                    val.add(latLng);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    // finger leaves the screen
                                    System.out.print("Finger Up");
                                    Draw_Map(val);
                                    break;
                            }
                            return Is_MAP_Moveable;
                        }
                    });
                }
                else {
//                    fram_map.removeAllViews();
                    while(!val.isEmpty()){val.removeFirst();};
                }
            }
        });
    }

    public void Draw_Map(LinkedList<LatLng> vals) {
        for (LatLng ll : vals){System.out.printf("Lat: %f\nLong: %f\n",ll.latitude,ll.longitude);};
        PolygonOptions rectOptions;
        rectOptions = new PolygonOptions();
        rectOptions.addAll(vals);
//        while (!vals.isEmpty()) {
//            LatLng first = vals.getFirst();
//            rectOptions.add(first);
//            vals.remove(first);
//        }
        rectOptions.strokeColor(Color.BLUE);
        rectOptions.strokeWidth(7);
        rectOptions.fillColor(Color.CYAN);
        rectOptions.visible(true);
        if (!vals.isEmpty()){
            Polygon polygon = mMap.addPolygon(rectOptions);
            polygon.setVisible(true);
        }
        while(!vals.isEmpty()){vals.removeFirst();};
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);

            LatLng mLatLng = new LatLng(latitude,longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));

        } else {
            // Show rationale and request permission.

            LatLng bozeman = new LatLng(45.668993, -111.048728);
            mMap.addMarker(new MarkerOptions().position(bozeman).title("Marker in Bozeman"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(bozeman));
        }

    }

    @Override
    public void onMyLocationClick(Location location) {
        Toast.makeText(this, "Current location:"
                + "\nLatitude: " + location.getLatitude()
                + "\nLongitude:" + location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}

