package id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.in;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.BaseActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.R;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.RestApi;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.services.ApiService;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.Customer;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.CustomerResponse;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.Inventory;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.Staff;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.StaffResponse;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.in.customerscan.StaffActivity;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.utility.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.spin_brand)Spinner spinBrand;
    @BindView(R.id.txt_serial_number)TextView txtSerialNumber;
    @BindView(R.id.layout_customer)LinearLayout layoutCustomer;
    @BindView(R.id.spin_customer)Spinner spinCustomer;
    @BindView(R.id.spin_from_fs_staff)Spinner spinFSStaff;
    @BindView(R.id.in_customer_scan)Button btnScanStaff;
    @BindView(R.id.txt_hasil_staff)TextView txtHasilStaff;

    private String TAG = InActivity.class.getSimpleName();
    private SessionManager sessionManager;
    private ArrayAdapter<CharSequence> adapterBrand;
    private String valueBrand, staffId, staff_code, fName, lName, barcodeValue, barcode, brand, valueTempCustomer,
        customerParse, valueFSStaff, fsStaff;

    private List<Customer> custmrList;
    private ArrayList<String> cstmrAr;
    private ArrayAdapter<String> adapterCstmr;

    private List<Staff> staffList;
    private ArrayList<String> staffAr;
    private ArrayAdapter<String> adapterStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in);
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

        cstmrAr = new ArrayList<String>();
        staffAr = new ArrayList<String>();

        setupToolbar();
        getAllStaff();

        if (cstmrAr != null && staffAr != null ){
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

        spinCustomer.setOnItemSelectedListener(this);
        spinFSStaff.setOnItemSelectedListener(this);

    }

    @OnClick(R.id.in_customer_scan)
    public void toStaffScan(View view) {
        Intent i = new Intent(this, StaffActivity.class);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.btn_next)
    public void next(View view) {
        Intent i = new Intent(this, InConfirmActivity.class);
        i.putExtra("customer", customerParse);
//        i.putExtra("FSStaff", valueFSStaff);
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
                check(value, valueBrand);
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
                Log.e("TAG", "IN : " + valueBrand);
                break;
            case R.id.spin_customer:
                valueTempCustomer = spinCustomer.getSelectedItem().toString();

                String[] parts = valueTempCustomer.split("\\-"); // explode di android .
                String part1 = parts[0];
                String part2 = parts[1];

                customerParse = part1;
                Log.e("TAG", "Customer : " + customerParse);
                break;
            case R.id.spin_from_fs_staff:
                valueFSStaff = spinFSStaff.getSelectedItem().toString();
                Log.e("TAG", "Staff FS : " + valueFSStaff);

                if(valueFSStaff != null && !valueFSStaff.contains("Choose From FS Staff")){
                    sessionManager.createStaffFSSession(valueFSStaff);
                    onResume();
                }if(valueFSStaff.contains("Choose From FS Staff")){
                    valueFSStaff = "";
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
                check(result.getContents(), valueBrand);
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
        barcode = user.get(SessionManager.KEY_BARCODE);
        brand = user.get(SessionManager.KEY_BRAND);
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

    private void check(String barcode, String brand){
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService = RestApi.getClient().create(ApiService.class);

        Call<Inventory> call = apiService.checkSerialNumber(brand, barcode);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(Call<Inventory>call, Response<Inventory> response) {
                dialog.dismiss();

                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "Start Job received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().getStatus() ) {
//                    currentDateTimeString = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
//
//                    Bundle params = new Bundle();
//                    params.putString("staff_code", staff_code);
//                    params.putString("SN", barcode);
//                    params.putString("stock_in_time", currentDateTimeString);
//                    mFirebaseAnalytics.logEvent("stock_in", params);

                    layoutCustomer.setVisibility(View.GONE);

                    AlertDialog alertDialog = new AlertDialog.Builder(InActivity.this).create();
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Serial Number ada dilist order");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog.show();
                } else {
                    layoutCustomer.setVisibility(View.VISIBLE);

                    AlertDialog alertDialog = new AlertDialog.Builder(InActivity.this).create();
                    alertDialog.setTitle("Warning!");
                    alertDialog.setMessage("Harap pilih customer terlebih dahulu !");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getAllCustomer();
                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<Inventory>call, Throwable t) {
                dialog.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(InActivity.this).create();
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

    private void getAllCustomer(){
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService = RestApi.getClient().create(ApiService.class);

        Call<CustomerResponse> call = apiService.getAllCustomer();
        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse>call, Response<CustomerResponse> response) {
                dialog.dismiss();

                custmrList = response.body().getData();
                Log.e(TAG, "Status Code = " + response.code());
                Log.e(TAG, "Start Job received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().getStatus() ) {
//                    currentDateTimeString = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
//
//                    Bundle params = new Bundle();
//                    params.putString("staff_code", staff_code);
//                    params.putString("SN", barcode);
//                    params.putString("stock_in_time", currentDateTimeString);
//                    mFirebaseAnalytics.logEvent("stock_in", params);

                    if(custmrList != null) {
                        for (int i = 0; i < custmrList.size(); i++) {
                            cstmrAr.add(custmrList.get(i).getCustomerCode() + "-" + custmrList.get(i).getCustomerDescription());
                        }
                        adapterCstmr = new ArrayAdapter<>(InActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, cstmrAr);
                        adapterCstmr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinCustomer.setAdapter(adapterCstmr);
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(InActivity.this).create();
                    alertDialog.setTitle("Warning!");
                    alertDialog.setMessage("Gagal load data customer!");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<CustomerResponse>call, Throwable t) {
                dialog.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(InActivity.this).create();
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
                            staffAr.add( staffList.get(i).getStaffCode().toString() );
                        }
                        adapterStaff = new ArrayAdapter<>(InActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, staffAr);
                        adapterStaff.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinFSStaff.setAdapter(adapterStaff);
                    }
                } else {
//                    Toast.makeText(RepairingActivity.this, "Function gagal", Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog = new AlertDialog.Builder(InActivity.this).create();
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

                AlertDialog alertDialog = new AlertDialog.Builder(InActivity.this).create();
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
