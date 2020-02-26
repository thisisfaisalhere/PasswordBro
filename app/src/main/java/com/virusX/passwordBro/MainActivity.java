package com.virusX.passwordBro;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Intent intent;
    private FloatingActionButton fab, fabGenerate, fabAdd;
    private Animation fabOpen, fabClose, fabRotateForward, fabRotateBackward;
    private TextView fabTxtGenerate, fabTxtAdd;
    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fabGenerate = findViewById(R.id.fabGenerate);
        fabAdd = findViewById(R.id.fabAdd);
        fabTxtAdd = findViewById(R.id.fabTxtAdd);
        fabTxtGenerate = findViewById(R.id.fabTxtGenerate);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabRotateBackward = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_backward);
        fabRotateForward = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_forward);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOpen = !isOpen;
                fabAnimation();
            }
        });
        fabGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, GenerateActivity.class);
                startActivity(intent);
                isOpen = !isOpen;
                fabAnimation();
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, AddRecordActivity.class);
                startActivity(intent);
                isOpen = !isOpen;
                fabAnimation();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_share, R.id.nav_privacy)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_share:
                        intent = new Intent(MainActivity.this, ShareActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_privacy:
                        intent = new Intent(MainActivity.this, PrivacyActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_account:
                        intent = new Intent(MainActivity.this, AddAccountActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void fabAnimation() {
        if(isOpen) {
            fab.startAnimation(fabRotateForward);
            fabGenerate.startAnimation(fabOpen);
            fabAdd.startAnimation(fabOpen);
            fabTxtAdd.startAnimation(fabOpen);
            fabTxtGenerate.startAnimation(fabOpen);
            fabGenerate.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.VISIBLE);
            fabTxtGenerate.setVisibility(View.VISIBLE);
            fabTxtAdd.setVisibility(View.VISIBLE);
            fabGenerate.setClickable(true);
            fabAdd.setClickable(true);
        } else {
            fab.startAnimation(fabRotateBackward);
            fabGenerate.startAnimation(fabClose);
            fabAdd.startAnimation(fabClose);
            fabTxtAdd.startAnimation(fabClose);
            fabTxtGenerate.startAnimation(fabClose);
            fabGenerate.setVisibility(View.GONE);
            fabAdd.setVisibility(View.GONE);
            fabTxtGenerate.setVisibility(View.GONE);
            fabTxtAdd.setVisibility(View.GONE);
            fabGenerate.setClickable(false);
            fabAdd.setClickable(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
