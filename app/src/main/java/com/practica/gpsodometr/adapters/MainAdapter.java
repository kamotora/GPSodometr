package com.practica.gpsodometr.adapters;

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

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class MainAdapter extends RealmRecyclerViewAdapter<Stat, MainAdapter.MyViewHolder> implements SimpleItemTouchHelper.ItemTouchHelperAdapter {
    public MainAdapter(OrderedRealmCollection<Stat> data) {
        super(data, true);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
        return new MainAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Stat obj = getItem(position);
        holder.stat = obj;
        //noinspection ConstantConditions
        holder.dateOfStart.setText(Helper.dateToString(obj.getDate()));
        holder.kilometrs.setText(Helper.kmToString(obj.getKilometers()));
    }


    @Override
    public void onItemDismiss(int position) {
        StatRep.delete(getItem(position));
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView dateOfStart;
        private TextView kilometrs;
        public Stat stat;

        MyViewHolder(View view) {
            super(view);
            dateOfStart = view.findViewById(R.id.dateOfStart);
            kilometrs = view.findViewById(R.id.kilometrs);
        }
    }
}
