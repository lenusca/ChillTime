package com.example.chilltime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class User extends AppCompatActivity{
    // sidebar
    DrawerLayout sidebar;
    ActionBarDrawerToggle choice;
    // Apresentar os dados que estão na bd
    TextView name;
    ImageView image;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String userID;
    // Mudar para as outras atividades
    Intent intent;
    // Toolbar
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab1, tab2, tab3;
    TabBarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabs);
        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);
        viewPager = findViewById(R.id.viewpager);

        setSupportActionBar(toolbar);
        adapter = new TabBarAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        // quando selecionado ir para as outras paginas
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0){
                    adapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 1){
                    adapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 2){

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // SideBar
        sidebar = (DrawerLayout)findViewById(R.id.sidebar);
        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);
        name = headerView.findViewById(R.id.user_name);
        image = headerView.findViewById(R.id.user_photo);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        // ir buscar o identificador do utilizador
        userID = mAuth.getCurrentUser().getUid();

        // ir buscar o documento relacionado com o utilizador, usando o uid
        final DocumentReference documentReference = mStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>(){

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // dizer o que quer ir buscar
                String printname = "";
                String printimage = "";
                printname =documentSnapshot.getString("Name");
                printimage = documentSnapshot.getString("Image");
                name.setText(printname);
                Picasso.get().load(printimage).into(image);
            }
        });

        choice = new ActionBarDrawerToggle(this, sidebar, R.string.Open, R.string.Close);
        sidebar.addDrawerListener(choice);
        choice.setDrawerIndicatorEnabled(true);
        choice.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nav_view.setItemIconTintList(null);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch(id)
                {
                    case R.id.list_movies:
                        intent = new Intent(User.this, Movies.class);
                        startActivity(intent);
                        return true;
                    case R.id.list_series:
                        intent = new Intent(User.this, Series.class);
                        startActivity(intent);
                        User.this.finish();
                        return true;
                    case R.id.gps:
                        intent = new Intent(User.this, GPS.class);
                        startActivity(intent);
                        User.this.finish();
                        return true;
                    case R.id.qrcode:
                        intent = new Intent(User.this, QRCode.class);
                        startActivity(intent);
                        User.this.finish();
                        return true;
                    case R.id.userinfo:
                        intent = new Intent(User.this, User.class);
                        startActivity(intent);
                        User.this.finish();
                        return true;
                    case R.id.settings:
                        intent = new Intent(User.this, Settings.class);
                        startActivity(intent);
                        User.this.finish();
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(User.this, Login.class);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

    }

    //sidebar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return choice.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


}
