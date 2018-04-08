package com.maxim.visionplayer.Fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maxim.visionplayer.R;
import com.maxim.visionplayer.Models.UserFriend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public FriendsPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_page, container, false);
        thisView = view;
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) {
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
            user = mAuth.getCurrentUser();
            updateUI(view);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        System.out.print("on start");
        if(user != null) {
//            updateUI(user);
        }
    }

    private void updateUI(View view) {
        GetDatabaseFriends db = new GetDatabaseFriends();
        friends = db.doInBackground();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.friendsRecycler);
        recyclerView.setAdapter(new ListAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayout.LayoutParams contentViewLayout = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT );
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
                updateUI(getView());
            } else {
                // Sign in failed, check response for error code
                TextView textView = getView().findViewById(R.id.errorTextView);
                textView.setText(R.string.notSignedIn);
            }
        }
    }

    public class GetDatabaseFriends extends AsyncTask<String, String, ArrayList<UserFriend>>
    {
        @Override
        protected ArrayList<UserFriend> doInBackground(String... strings) {
            ArrayList<UserFriend> friends = new ArrayList<>();
            try {
                conn = createConnection();
                if (conn != null) {
                    String query = "SELECT * FROM [dbo].VisionPlayerUser WHERE id IN ( SELECT friendID FROM [dbo].VisionPlayerFriendList WHERE userID IN (SELECT id FROM [dbo].VisionPlayerUser WHERE fireBaseUID = '" + user.getUid() + "'));";
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery(query);
                    while(rs.next()) {
                        friends.add(new UserFriend(rs.getInt("id"),rs.getString("currentSong"), rs.getString("locationLat"), rs.getString("locationLong"), rs.getString("fireBaseName")));
                    }
                    conn.close();
                    return friends;
                }
                return friends;
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
            textView.setText(R.string.noInternet);
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
            return friends.size();
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
            userName.setText(friends.get(position).getName());
            songName.setText(friends.get(position).getCurrentSong());
        }
    }
}
