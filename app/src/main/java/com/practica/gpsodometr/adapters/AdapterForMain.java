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

import java.util.ArrayList;
import java.util.List;

public class AdapterForMain extends RecyclerView.Adapter<AdapterForMain.ViewHolder>implements SimpleItemTouchHelper.ItemTouchHelperAdapter {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent,false);
        return new AdapterForMain.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listRes.get(position));
    }

    @Override
    public int getItemCount() {
        return listRes.size();
    }

    public void setItems(Stat pair) {
        listRes.add(pair);
        notifyItemInserted(getItemCount());
    }

    public void clearItems(){
        listRes.clear();
        notifyDataSetChanged();
    }

    private TextView dateOfStart;
    private TextView kilometrs;

    private List<Stat> listRes = new ArrayList<>();

    @Override
    public void onItemDismiss(int position) {
        listRes.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateOfStart = itemView.findViewById(R.id.dateOfStart);
            kilometrs = itemView.findViewById(R.id.kilometrs);
        }

        public void bind(Stat pair){
            dateOfStart.setText(Helper.dateToString(pair.getDate()));
            kilometrs.setText(Helper.kmToString(pair.getKilometers()));
        }
    }

}
