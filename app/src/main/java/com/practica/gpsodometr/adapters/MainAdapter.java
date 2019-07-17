package com.practica.gpsodometr.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practica.gpsodometr.R;
import com.practica.gpsodometr.data.Helper;
import com.practica.gpsodometr.data.model.SimpleItemTouchHelper;
import com.practica.gpsodometr.data.model.Stat;
import com.practica.gpsodometr.data.repository.StatRep;
import com.practica.gpsodometr.servicies.MyApplication;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class MainAdapter extends RealmRecyclerViewAdapter<Stat, MainAdapter.MyViewHolder> implements SimpleItemTouchHelper.ItemTouchHelperAdapter {
    private MyApplication context;

    private Typeface tf2;

    public MainAdapter(OrderedRealmCollection<Stat> data, MyApplication context) {
        super(data, true);
        this.context = context;
        tf2 = Typeface.createFromAsset(context.getAssets(),"PFAgoraSlabPro Bold.ttf");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
        return new MainAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Stat obj = getItem(position);
        holder.stat = obj;
        //noinspection ConstantConditions
        holder.dateOfStart.setText(Helper.dateToString(obj.getDate()));
        holder.kilometrs.setText(Helper.kmToString(obj.getKilometers()));
        holder.dateOfStart.setTypeface(tf2);
        holder.kilometrs.setTypeface(tf2);
    }


    @Override
    public void onItemDismiss(int position) {
        Stat item = getItem(position);
        if (item == null)
            return;
        StatRep.delete(item);
        notifyItemRemoved(position);
        context.statWasDeleted();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView dateOfStart;
        private TextView kilometrs;
        Stat stat;

        MyViewHolder(View view) {
            super(view);
            dateOfStart = view.findViewById(R.id.dateOfStart);
            kilometrs = view.findViewById(R.id.kilometrs);
        }
    }
}
