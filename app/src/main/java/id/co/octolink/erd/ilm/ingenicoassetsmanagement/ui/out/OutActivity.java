package id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.out;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.R;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.RestApi;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.services.ApiService;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.Staff;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.StaffResponse;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.in.InActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.out.scanstaff.StaffOutScanActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.utility.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.spin_brand)Spinner spinBrand;
    @BindView(R.id.txt_serial_number)TextView txtSerialNumber;
    @BindView(R.id.spin_to_fs_staff)Spinner spinFSStaff;
    @BindView(R.id.in_customer_scan)Button btnScanStaff;
    @BindView(R.id.txt_hasil_staff)TextView txtHasilStaff;

    private String TAG = InActivity.class.getSimpleName();
    private SessionManager sessionManager;
    private ArrayAdapter<CharSequence> adapterBrand;
    private String valueBrand, staffId, staff_code, fName, lName, barcodeValue, valueFSStaffOUT, fsStaff;

    private List<Staff> staffList;
    private ArrayList<String> staffAr;
    private ArrayAdapter<String> adapterStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out);
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
        fsStaff = user.get(SessionManager.KEY_FS_STAFF);

        staffAr = new ArrayList<String>();

        setupToolbar();
        getAllStaff();

        if ( staffAr != null ){
            setupSpinner();
        }

        txtHasilStaff.setText(fsStaff);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    private void setupSpinner() {
        adapterBrand = ArrayAdapter.createFromResource(this, R.array.brand_edc,
                android.R.layout.simple_spinner_item);
        adapterBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBrand.setAdapter(adapterBrand);
        spinBrand.setOnItemSelectedListener(this);

        spinFSStaff.setOnItemSelectedListener(this);
    }

    @OnClick(R.id.in_customer_scan)
    public void toStaffScan(View view) {
        Intent i = new Intent(this, StaffOutScanActivity.class);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.btn_next)
    public void next(View view) {
        Intent i = new Intent(this, OutConfirmActivity.class);
        i.putExtra("fsStaff", valueFSStaffOUT);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.btn_scan)
    public void scan(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Posisikan Barcode dalam kotak diatas");
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @OnClick(R.id.txt_serial_number)
    public void editEDCSN() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_edit_serial_number, null);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);

        final EditText input = (EditText) promptsView
                .findViewById(R.id.et_dialog_edit_serialnumber);

        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
//                Log.d(TAG, "New Serial Number: " + value);
                sessionManager.setLogin(true);
                sessionManager.createBarcodeSession(value);
                onResume();
                return;
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog b = alertDialog.create();
        b.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spin_brand:
                valueBrand = parent.getSelectedItem().toString();
                sessionManager.createBrandSession(valueBrand);
                Log.e("TAG", "OUT : " + valueBrand);
                break;
            case R.id.spin_to_fs_staff:
                valueFSStaffOUT = spinFSStaff.getSelectedItem().toString();
                Log.e("TAG", "FS STAFF OUT : " + valueFSStaffOUT);

                if(valueFSStaffOUT != null && !valueFSStaffOUT.contains("Choose From FS Staff")){
                    sessionManager.createStaffFSSession(valueFSStaffOUT);
                    onResume();
                }if(valueFSStaffOUT.contains("Choose From FS Staff")){
                valueFSStaffOUT = "";
            }

                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                txtSerialNumber.setText(result.getContents());
                sessionManager.createBarcodeSession(result.getContents());

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
    protected void onResume() {
        super.onResume();

        HashMap<String, String> user = sessionManager.getUserDetails();
        barcodeValue = user.get(SessionManager.KEY_BARCODE);
        fsStaff = user.get(SessionManager.KEY_FS_STAFF);

        txtSerialNumber.invalidate();
        txtSerialNumber.setText(barcodeValue);
        txtHasilStaff.setText(fsStaff);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sessionManager.createBarcodeSession("");
    }

    private void getAllStaff() {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<StaffResponse> call = apiService.getAllStaff();
        call.enqueue(new Callback<StaffResponse>() {
            @Override
            public void onResponse(Call<StaffResponse>call, Response<StaffResponse> response) {
                dialog.dismiss();

                staffList = response.body().getData();
                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "CloseReason received: " + new Gson().toJson(staffList));

                if (response.code() == 200 && response.body().getStatus()){
                    staffAr.add("- Choose From FS Staff -");

                    if(staffList != null) {
                        for (int i = 0; i < staffList.size(); i++) {
                            staffAr.add( staffList.get(i).getStaffCode() );
                        }
                        adapterStaff = new ArrayAdapter<>(OutActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, staffAr);
                        adapterStaff.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinFSStaff.setAdapter(adapterStaff);
                    }
                } else {
//                    Toast.makeText(RepairingActivity.this, "Function gagal", Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog = new AlertDialog.Builder(OutActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Staff Tidak Ada");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<StaffResponse>call, Throwable t) {
                dialog.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(OutActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Tidak terhubung ke Server \n periksa koneksi internet !");
                //alertDialog.setIcon(R.drawable.tick);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });
                alertDialog.show();
            }
        });
    }

}
