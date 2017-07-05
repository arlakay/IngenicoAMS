package id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.out;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.BaseActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.R;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.RestApi;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.services.ApiService;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.Inventory;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.in.InConfirmActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.utility.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutConfirmActivity extends BaseActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.txt_staff_code)TextView tStaffCode;
    @BindView(R.id.txt_brand)TextView tBrand;
    @BindView(R.id.txt_serial_number)TextView tSN;
    @BindView(R.id.txt_fs_staff)TextView tFSStaff;

    private String TAG = InConfirmActivity.class.getSimpleName();
    private SessionManager sessionManager;
    private String staffId, staff_code,fName, lName, barcode, brand, currentDateTimeString, fsStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_confirm);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();
        if (!sessionManager.isLoggedIn()) {
            sessionManager.setLogin(false);
            sessionManager.logoutUser();
        }
        HashMap<String, String> user = sessionManager.getUserDetails();
        staffId = user.get(SessionManager.KEY_STAFF_ID);
        staff_code = user.get(SessionManager.KEY_STAFF_CODE);
        fName = user.get(SessionManager.KEY_FIRST_NAME);
        lName = user.get(SessionManager.KEY_LAST_NAME);
        barcode = user.get(SessionManager.KEY_BARCODE);
        brand = user.get(SessionManager.KEY_BRAND);
        fsStaff = user.get(SessionManager.KEY_FS_STAFF);

        setupToolbar();
        getData();

        tStaffCode.setText(staff_code);
        tBrand.setText(brand);
        tSN.setText(barcode);
        tFSStaff.setText(fsStaff);
    }

    private void getData(){
        Intent i = getIntent();
//        fsStaff = i.getStringExtra("fsStaff");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    @OnClick(R.id.btn_save)
    public void save(View view) {
        saveStockOut();
    }

    private void saveStockOut(){
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService = RestApi.getClient().create(ApiService.class);

        Call<Inventory> call = apiService.stockOut(staff_code, fsStaff, brand, barcode);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(Call<Inventory>call, Response<Inventory> response) {
                dialog.dismiss();

                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "Start Job received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().getStatus() ) {
                    currentDateTimeString = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

                    Bundle params = new Bundle();
                    params.putString("staff_code", staff_code);
                    params.putString("SN", barcode);
                    params.putString("stock_out_time", currentDateTimeString);
                    mFirebaseAnalytics.logEvent("stock_out", params);

                    AlertDialog alertDialog = new AlertDialog.Builder(OutConfirmActivity.this).create();
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Berhasil Tersimpan");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sessionManager.createBarcodeSession("");
                            finish();
                        }
                    });
                    alertDialog.show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(OutConfirmActivity.this).create();
                    alertDialog.setTitle("Warning!");
                    alertDialog.setMessage("Harap Lakukan Stock IN terlebih dahulu !");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<Inventory>call, Throwable t) {
                dialog.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(OutConfirmActivity.this).create();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                sessionManager.createBarcodeSession("");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sessionManager.createBarcodeSession("");
    }

}
