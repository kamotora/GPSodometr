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
import com.practica.gpsodometr.servicies.MyApplication;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AdapterForMain extends RecyclerView.Adapter<AdapterForMain.ViewHolder>implements SimpleItemTouchHelper.ItemTouchHelperAdapter {
    //private final MyApplication context;

    public AdapterForMain(ArrayList<Stat> tests) {
        this.listRes = tests;
    }
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


    /**
     * Добавить строку в таблицу
     */
    public void setItems(Stat pair) {
        listRes.add(pair);
        notifyDataSetChanged();
    }

    //Для обновления данных
    public void updateInfo(int position, Stat e) {
        if (getItemCount() == 0)
            listRes.add(new Stat(0.0));
        else
            listRes.set(position, e);
        notifyItemChanged(position, e);
    }

    public void clearItems(){
        listRes.clear();
        notifyDataSetChanged();
    }

    ArrayList<Stat> listRes = new ArrayList<>();

    @Override
    public void onItemDismiss(int position) {
        StatRep.delete(listRes.get(position));
        listRes.remove(position);
        //context.todayStatWasDeleted();
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dateOfStart;
        private TextView kilometrs;

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
