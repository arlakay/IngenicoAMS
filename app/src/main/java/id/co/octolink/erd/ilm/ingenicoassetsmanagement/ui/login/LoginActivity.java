package id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.BaseActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.R;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.RestApi;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.services.ApiService;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.LoginResponse;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.main.MainActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.utility.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.btn_login)Button login;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.et_staff_id)EditText etStaffId;
    @BindView(R.id.et_password)EditText etPass;
    @BindView(R.id.txt_versioning)TextView versioning;

    private String staffID, pass, currentDateTimeString;
    private SessionManager sessionManager;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setupToolbar();

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

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

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    @OnClick(R.id.btn_login)
    public void login(View view) {
        staffID = etStaffId.getText().toString();
        pass = etPass.getText().toString();

        staffLogin(staffID, pass);
    }

    private void staffLogin(String staffCode, final String pass) {

        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<LoginResponse> call = apiService.staff_auth_login(staffCode, pass);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse>call, Response<LoginResponse> response) {
                dialog.dismiss();

//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "CloseReason received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().getStatus()) {

                    if(response.body().getData().getLocationCode().equalsIgnoreCase("1")){
                        String staffId = response.body().getData().getStaffId();
                        String staffCode = response.body().getData().getStaffCode();
                        String fName = response.body().getData().getFirstName();
                        String lName = response.body().getData().getLastName();
                        String passw = response.body().getData().getPassword();
                        String locationCode = response.body().getData().getLocationCode();

                        sessionManager.setLogin(true);
                        sessionManager.createLoginSession(staffId, staffCode, fName, lName, passw, locationCode);

                        currentDateTimeString = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                        Log.e("JAM LOGIN", currentDateTimeString);

                        Bundle params = new Bundle();
                        params.putString("staff_code", staffCode);
                        params.putString("staff_name", fName + " " + lName);
                        params.putString("staff_login_time", currentDateTimeString);
                        mFirebaseAnalytics.logEvent("ams_staff_login", params);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("Login Failed");
                        alertDialog.setMessage("Something Wrong with your account \nPlease Contact Admin!");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                    }

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Login Failed");
                    alertDialog.setMessage("Staff Id and Password Not Match");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse>call, Throwable t) {
                dialog.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Tidak terhubung ke Server \n periksa koneksi internet !");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();

            }
        });
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
