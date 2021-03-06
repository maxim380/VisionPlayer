package com.maxim.visionplayer.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maxim.visionplayer.MainActivity;
import com.maxim.visionplayer.R;
import com.maxim.visionplayer.Models.UserFriend;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsPage extends Fragment {

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    private Connection conn;
    private ProgressBar progressBar;
    private ArrayList<UserFriend> friends;
    private View thisView;
    private MainActivity activity;

    public FriendsPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_page, container, false);
        thisView = view;
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } else {
            FloatingActionButton fabAddFriend = getActivity().findViewById(R.id.fabAddFriend);
            FloatingActionButton fabRefresh = getActivity().findViewById(R.id.fabRefresh);

            addFABListeners(fabAddFriend, fabRefresh);
            user = mAuth.getCurrentUser();
            updateUI(view);
        }

        activity = (MainActivity) getActivity();
        return view;
    }

    private void addFABListeners(FloatingActionButton fabAddFriend, FloatingActionButton fabRefresh) {
        fabAddFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();
            }
        });

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Get permission for location
                activity.checkPermission(MainActivity.GET_LOCATION_REQUEST_CODE);
                boolean permission = activity.isLocationPermissionGranted();
                //Push to DB, with or without location
                String lat = "";
                String lon = "";
                String location = "";
                if(permission) {
                    location = getLocation();
                }

                DatabaseUpdate db = new DatabaseUpdate();
                db.doInBackground(activity.getCurrentSong().getTitle(), lat, lon, activity.getCurrentSong().getArtist(), location);
                //Update UI
                Toast.makeText(getActivity(), R.string.refresh,
                        Toast.LENGTH_SHORT).show();
                updateUI(thisView);
            }
        });
    }

    public void updateDB() {
        String lat = "";
        String lon = "";
        String location = getLocation();
        DatabaseUpdate db = new DatabaseUpdate();
        db.doInBackground(activity.getCurrentSong().getTitle(), lat, lon, activity.getCurrentSong().getArtist(), location);
    }

    @SuppressLint("MissingPermission")
    private String getLocation() {
        double[] locationArray = new double[2];
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude=location.getLatitude();
                double longitude=location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            locationArray[0] = location.getLatitude();
            locationArray[1] = location.getLongitude();
        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(locationArray[0], locationArray[1], 1);
            if(addresses != null && addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                return city;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter mail address of friend");

        final EditText input = new EditText(getActivity());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String friendMail = input.getText().toString();
                DatabaseAddFriend db = new DatabaseAddFriend();
                if(db.doInBackground(friendMail)) {
                    updateUI(thisView);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        System.out.print("on start");
        if (user != null) {
//            updateUI(user);
        }
    }

    private void updateUI(View view) {
        DatabaseFriends db = new DatabaseFriends();
        friends = db.doInBackground();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.friendsRecycler);
        recyclerView.setAdapter(new ListAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FrameLayout.LayoutParams contentViewLayout = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(contentViewLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseNewUser db = new DatabaseNewUser();
                db.doInBackground();
                updateUI(getView());
            } else {
                // Sign in failed, check response for error code
                TextView textView = getView().findViewById(R.id.errorTextView);
                textView.setText(R.string.notSignedIn);
                Toast.makeText(getActivity(), R.string.notSignedIn,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DatabaseUpdate extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            conn = null;
            String currentSong = strings[0];
            String artist = strings[3];
            String location = strings[4];
            try {
                conn = createConnection();
                if (conn != null) {
                    String query = "UPDATE [dbo].VisionPlayerUser SET currentSong = ?, currentSongArtist = ?, location = '" + location + "' WHERE fireBaseUID = '" + user.getUid() + "';";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, currentSong);
                    statement.setString(2, artist);

                    ResultSet rs = statement.executeQuery();
                }
                if(conn != null) {
                    conn.close();
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        return false;
                    }
                }
            }
            return false;
        }
    }

    public class DatabaseAddFriend extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            conn = null;
            String mail = strings[0];
            try {
                conn = createConnection();
                if (conn != null) {
                    String query = "SELECT * FROM [dbo].VisionPlayerUser WHERE fireBaseMail = '" + mail + "';";
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery(query);
                    if (rs.next()) {
                        query = "INSERT INTO [dbo].VisionPlayerFriendList VALUES((SELECT id FROM [dbo].VisionPlayerUser WHERE fireBaseUID = '" + user.getUid() + "'), " + rs.getInt("id") + ");";
                        statement.execute(query);
                    }
                }
                if(conn != null) {
                    conn.close();
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        return false;
                    }
                }
                return false;
            }
        }
    }

    public class DatabaseNewUser extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            conn = null;
            try {
                conn = createConnection();
                if (conn != null) {
                    String query = "SELECT * FROM [dbo].VisionPlayerUser WHERE fireBaseUID = '" + user.getUid() + "';";
                    PreparedStatement statement = conn.prepareStatement(query);
                    ResultSet rs = statement.executeQuery(query);
                    if (!rs.next()) {
                        query = "INSERT INTO [dbo].VisionPlayerUser (currentSong, locationLong, locationLat, fireBaseUID, fireBaseMail, fireBaseName) VALUES ('', '', '','" + user.getUid() + "','" + user.getEmail() + "', '" + user.getDisplayName() + "')";
                        statement = conn.prepareStatement(query);
                        statement.executeQuery();
                    }
                }
                conn.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        return false;
                    }
                }
                return false;
            }
        }
    }

    public class DatabaseFriends extends AsyncTask<String, String, ArrayList<UserFriend>> {
        @Override
        protected ArrayList<UserFriend> doInBackground(String... strings) {
            ArrayList<UserFriend> friends = new ArrayList<>();
            try {
                conn = createConnection();
                if (conn != null) {
                    String query = "SELECT * FROM [dbo].VisionPlayerUser WHERE fireBaseUID = '" + user.getUid() + "' OR id IN ( SELECT friendID FROM [dbo].VisionPlayerFriendList WHERE userID IN (SELECT id FROM [dbo].VisionPlayerUser WHERE fireBaseUID = '" + user.getUid() + "'));";
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery(query);
                    while (rs.next()) {
                        friends.add(new UserFriend(rs.getInt("id"), rs.getString("currentSong"), rs.getString("locationLat"), rs.getString("locationLong"), rs.getString("fireBaseName"), rs.getString("currentSongArtist"), rs.getString("location"), rs.getString("fireBaseUID")));
                    }
                    conn.close();
                    return friends;
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return friends;
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<UserFriend> list) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private Connection createConnection() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connectionURL = "jdbc:jtds:sqlserver://mssql.fhict.local;" + "databaseName=dbi363112;user=dbi363112;password=visionplayer";
            connection = DriverManager.getConnection(connectionURL);
            return connection;
        } catch (Exception e) {
            TextView textView = thisView.findViewById(R.id.errorTextView);
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.noInternet);
            Toast.makeText(getActivity(), R.string.noConnection,
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private class ListAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_friend_item, parent, false);
            return new FriendsPage.ListviewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((FriendsPage.ListviewHolder) holder).bindView(position);
        }

        @Override
        public int getItemCount() {
            if(friends != null) {
                return friends.size();
            } else {
                return 0;
            }
        }
    }

    private class ListviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView userName;
        private TextView songName;
        private TextView location;
        private TextView artist;

        public ListviewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.friendName);
            songName = itemView.findViewById(R.id.songName);
            location = itemView.findViewById(R.id.locationText);
            artist = itemView.findViewById(R.id.artistName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        public void bindView(int position) {
            if(position == 0) {
                String s = friends.get(position).getName() + " (you)";
                userName.setText(s);
            } else {
                userName.setText(friends.get(position).getName());
            }
            songName.setText(friends.get(position).getCurrentSong());
            artist.setText(friends.get(position).getCurrentSongArtist());
            location.setText(friends.get(position).getLocation());
        }

    }
}
