package com.citomoltd.englishspeakingpracticeapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class MainActivity extends AppCompatActivity {
    AppCompatButton btn_beginner, btn_regular;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar materialToolbar;
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    AppUpdateManager appUpdateManager;
    private long backPressedTime = 0;
    private static final long BACK_PRESS_INTERVAL = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_beginner = findViewById(R.id.btn_beginner);
        btn_regular = findViewById(R.id.btn_regular);
        drawerLayout = findViewById(R.id.drawerLayout);
        materialToolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);
        TextView modeLabel = headerView.findViewById(R.id.textViewDarkMode);
        SwitchCompat darkModeSwitch = headerView.findViewById(R.id.darkModeSwitch);
        fixableUpdateDemu();

        if (darkModeSwitch == null) {
            Log.e("DEBUG", "darkModeSwitch is NULL!");
        } else {
            darkModeSwitch.setChecked(true); // বা তোমার logic
        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (MainActivity.this, drawerLayout, materialToolbar, R.string.drawerLayout_open, R.string.drawerLayout_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        darkModeSwitch.setChecked(isDarkMode);
        modeLabel.setText(isDarkMode ? "Dark Mode" : "Light Mode");

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            modeLabel.setText(isChecked ? "Dark Mode" : "Light Mode");
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.bookMark) {
                    Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                }
                if (item.getItemId() == R.id.itemFavorite) {
                    Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();

                } else if (item.getItemId() == R.id.mRate) {
                    openAppRating();
                    drawerLayout.closeDrawers();

                } else if (item.getItemId() == R.id.shareapp) {
                    ShareApp(MainActivity.this);
                    drawerLayout.closeDrawers();
                } else if (item.getItemId() == R.id.policy) {
                    openPrivacyPolicy();
                    drawerLayout.closeDrawers();
                } else if (item.getItemId() == R.id.more_app) {
                    openMoreApps();
                    drawerLayout.closeDrawers();
                }
                return true;
            }
        });


        btn_beginner.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            intent.putExtra("topic", 1);
            startActivity(intent);
        });

        btn_regular.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            intent.putExtra("topic", 2);
            startActivity(intent);
        });


        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < BACK_PRESS_INTERVAL) {
                    finish(); // Exit app
                } else {
                    backPressedTime = System.currentTimeMillis();
                    Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void openAppRating() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

        private void openPrivacyPolicy() {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/english-speaking-practice-/"));
            startActivity(intent);
        }

    private void openMoreApps() {
        String developerName = "https://play.google.com/store/apps/developer?id=citomoltd";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(developerName)));

        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(developerName)));

        }
    }

    public void ShareApp(Context context) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Download now: https://play.google.com/store/apps/details?id=" + context.getPackageName());
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "Share via"));
    }


    public void fixableUpdateDemu() {
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener);

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // Request the update.

                appUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // an activity result launcher registered via registerForActivityResult
                        activityResultLauncher,
                        // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                        // flexible updates.
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build());
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // handle callback
                        if (result.getResultCode() != RESULT_OK) {
                            // log(Double.parseDouble("Update flow failed! Result code: " + result.getResultCode()));
                            // If the update is canceled or fails,
                            // you can request to start the update again.
                        }
                    }
                });
    }

    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate();
        }

    };


    // Displays the snackbar notification and call to action.
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.main),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());

        snackbar.setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        snackbar.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        appUpdateManager.unregisterListener(listener);
    }

    // Checks that the update is not stalled during 'onResume()'.
// However, you should execute this check at all app entry points.
    @Override
    protected void onResume() {
        super.onResume();


        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {

                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    }
                });
    }

}