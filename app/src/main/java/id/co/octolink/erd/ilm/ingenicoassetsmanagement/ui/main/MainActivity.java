package id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.R;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.field.FieldActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.history.field.HistoryFieldScrollableTabsActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.history.gudang.HistoryScrollableTabsActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.in.InActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.out.OutActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.utility.SessionManager;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.txt_versioning)TextView versioning;
    @BindView(R.id.layout_gudang)LinearLayout layoutGudang;
    @BindView(R.id.layout_lapangan)LinearLayout layoutLapangan;

    private SessionManager sessionManager;
    private String staffId, staff_code, fName, lName, locCode;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        if (!sessionManager.isLoggedIn()) {
            sessionManager.setLogin(false);
            sessionManager.logoutUser();
            finish();
        }

        HashMap<String, String> user = sessionManager.getUserDetails();
        staffId = user.get(SessionManager.KEY_STAFF_ID);
        staff_code = user.get(SessionManager.KEY_STAFF_CODE);
        fName = user.get(SessionManager.KEY_FIRST_NAME);
        lName = user.get(SessionManager.KEY_LAST_NAME);
        locCode = user.get(SessionManager.KEY_LOCATION_CODE);

        setupToolbar();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        versioning.setText("v "+version);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(fName + " " + lName);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    @OnClick(R.id.btn_in)
    public void in(View view) {
        Intent i = new Intent(this, InActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.btn_out)
    public void out(View view) {
        Intent i = new Intent(this, OutActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.btn_history)
    public void history(View view) {
        Intent i = new Intent(this, HistoryScrollableTabsActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.btn_field)
    public void field(View view){
        Intent i = new Intent(this, FieldActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.btn_field_history)
    public void historyField(View view){
        Intent i = new Intent(this, HistoryFieldScrollableTabsActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                sessionManager.setLogin(false);
                sessionManager.logoutUser();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

}
