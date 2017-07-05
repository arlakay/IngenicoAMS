package id.co.octolink.erd.ilm.ingenicoassetsmanagement.ui.history.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.R;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.History;

/**
 * Created by ILM on 8/1/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VersionViewHolder> {

    private List<History> loginList;
    private int rowLayout;
    private String newDate;
    Context context;
    OnItemClickListener clickListener;

    public HistoryAdapter(List<History> login, int rowLayout, Context context, OnItemClickListener listener) {
        this.loginList = login;
        this.rowLayout = rowLayout;
        this.context = context;
        this.clickListener = listener;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
        final History model = loginList.get(i);
        versionViewHolder.bind(model, clickListener);
    }

    @Override
    public int getItemCount() {
        return loginList == null ? 0 : loginList.size();
    }

    public void animateTo(List<History> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<History> newModels) {
        for (int i = loginList.size() - 1; i >= 0; i--) {
            final History model = loginList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<History> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final History model = newModels.get(i);
            if (!loginList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<History> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final History model = newModels.get(toPosition);
            final int fromPosition = loginList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public History removeItem(int position) {
        final History model = loginList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, History model) {
        loginList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final History model = loginList.remove(fromPosition);
        loginList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_history_staff_code_from) TextView tStaffFrom;
        @BindView(R.id.txt_history_staff_code_to) TextView tStaffTo;
        @BindView(R.id.txt_history_time) TextView tTime;
        @BindView(R.id.txt_history_brand) TextView tBrand;
        @BindView(R.id.txt_history_serial_number) TextView tSN;

        public VersionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final History model, final OnItemClickListener listener) {

            if (model.getDeviceStatus().equalsIgnoreCase("1")){
                tStaffFrom.setText(model.getStaffCodeIn());
                tStaffTo.setText(model.getStaffCode());

                tTime.setText(convertDateTime(model.getImportTime()));
                tBrand.setText(model.getBrandCodeNew());
                tSN.setText(String.valueOf(model.getSerialNumberNew()));
            }else {
                tStaffFrom.setText(model.getStaffCode());
                tStaffTo.setText(model.getStaffCodeOut());

                tTime.setText(convertDateTime(model.getImportTime()));
                tBrand.setText(model.getBrandCodeNew());
                tSN.setText(String.valueOf(model.getSerialNumberNew()));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(model);

                }
            });
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(History model);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public String convertDateTime(String date){
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = null;
        try {
            dt = sd1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sd2 = new SimpleDateFormat("d MMM yyyy HH:mm");
        newDate = sd2.format(dt);

        return newDate;
    }

}
