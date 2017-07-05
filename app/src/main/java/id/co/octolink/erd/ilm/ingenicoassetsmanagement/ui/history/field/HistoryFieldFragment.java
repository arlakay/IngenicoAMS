package id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.history.field;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.R;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.RestApi;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.services.ApiService;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.History;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.HistoryResponse;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.history.adapter.HistoryAdapter;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.history.gudang.HistoryOutFragment;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.utility.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFieldFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFieldFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.recycler_history_in)RecyclerView recyclerViewHistoryField;
    @BindView(R.id.swipe_refresh_layout)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_list_kosong)TextView textListKosong;

    private static String TAG = HistoryOutFragment.class.getSimpleName();
    private FirebaseAnalytics mFirebaseAnalytics;
    private SessionManager sessionManager;
    private String staff_code;
    private List<History> reviewJobList = new ArrayList<>();
    private HistoryAdapter adapter;

    public HistoryFieldFragment() {
        // Required empty public constructor
    }

    public static HistoryFieldFragment newInstance() {
        return new HistoryFieldFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_history_in, container, false);
        ButterKnife.bind(this, rootView);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        sessionManager = new SessionManager(getActivity());
        sessionManager.checkLogin();

        if (!sessionManager.isLoggedIn()) {
            sessionManager.setLogin(false);
            sessionManager.logoutUser();
        }

        HashMap<String, String> user = sessionManager.getUserDetails();
        staff_code = user.get(SessionManager.KEY_STAFF_CODE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerViewHistoryField.setLayoutManager(linearLayoutManager);
        recyclerViewHistoryField.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getHistoryField();
            }
        });

        return rootView;
    }

    private void getHistoryField() {

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<HistoryResponse> call = apiService.historyField(staff_code);
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse>call, Response<HistoryResponse> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.body().getMessages().equalsIgnoreCase("Process Success") && response.body().getStatus()) {

                    textListKosong.setVisibility(View.GONE);

                    reviewJobList = response.body().getData();
                    Log.d(TAG, "Status Code = " + response.code());
                    Log.d(TAG, "History Field : " + new Gson().toJson(reviewJobList));

                    Bundle params = new Bundle();
                    params.putString("staff_code", staff_code);
                    mFirebaseAnalytics.logEvent("history_field", params);

                    adapter = new HistoryAdapter(reviewJobList, R.layout.list_item_review_job, getActivity(), new HistoryAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(History model) {
//                            int jobId = model.getJob_id();
//                            String tecCode = model.getTechnician_code();
//                            String startTime = model.getStart_time();
//                            String finishTime = model.getFinish_time();
//                            String customerCode = model.getCustomer_code();
//                            String terminalCode = model.getTerminal_code();
//                            String func = model.getFunction_code();
//                            String symp = model.getSymptom_code();
//
//                            Intent intent = new Intent(getActivity(), DetailReviewJobActivity.class);
//                            intent.putExtra("type", "edc");
//                            intent.putExtra("job_id", jobId);
//                            intent.putExtra("tech_code", tecCode);
//                            intent.putExtra("start_time", startTime);
//                            intent.putExtra("finish_time", finishTime);
//                            intent.putExtra("cust_code", customerCode);
//                            intent.putExtra("term_code", terminalCode);
//                            intent.putExtra("func_code", func);
//                            intent.putExtra("symp_code", symp);
//                            intent.putExtra("spare_code", spare_code);
//                            intent.putExtra("resol_code", resol_code);
//
//                            startActivity(intent);
                        }
                    });
                    recyclerViewHistoryField.setAdapter(adapter);
                }else {
                    //                    Toast.makeText(getActivity(), "Kosong", Toast.LENGTH_SHORT).show();
                    recyclerViewHistoryField.setVisibility(View.GONE);
                    textListKosong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse>call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    public void onRefresh() {
        if (reviewJobList != null) {
            reviewJobList.clear();
        }
        swipeRefreshLayout.setRefreshing(true);
        getHistoryField();
    }

}
