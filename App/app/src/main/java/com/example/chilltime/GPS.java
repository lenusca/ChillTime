package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import static com.google.android.material.navigation.NavigationView.*;

public class GPS extends AppCompatActivity {
    //SIDEBAR
    private DrawerLayout sidebar;
    private ActionBarDrawerToggle choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_p_s);
        sidebar = (DrawerLayout)findViewById(R.id.sidebar);
        choice = new ActionBarDrawerToggle(this, sidebar, R.string.Open, R.string.Close);
        choice.setDrawerIndicatorEnabled(true);

        sidebar.addDrawerListener(choice);
        choice.syncState();

        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setItemIconTintList(null);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch(id)
                {
                    case R.id.list_movies:
                        Toast.makeText(GPS.this, "MOVIES",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.list_series:
                        Toast.makeText(GPS.this, "SERIES",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.gps:
                        Toast.makeText(GPS.this, "GPS",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.qrcode:
                        Toast.makeText(GPS.this, "QR Code",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.userinfo:
                        Toast.makeText(GPS.this, "User Activities",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.settings:
                        Toast.makeText(GPS.this, "Settings",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(GPS.this, Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }




            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return choice.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


}
