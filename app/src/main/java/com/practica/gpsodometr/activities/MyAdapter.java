package com.practica.gpsodometr.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practica.gpsodometr.R;
import com.practica.gpsodometr.data.Helper;
import com.practica.gpsodometr.data.model.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableforsettings, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listWork.get(position));
    }

    @Override
    public int getItemCount() {
        return listWork.size();
    }

    private TextView nameWork;
    private TextView kilometrs;
    private TextView dataStart;
    private TextView leftKilo;

    private List<Action> listWork = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameWork = itemView.findViewById(R.id.typeOfWork);
            kilometrs = itemView.findViewById(R.id.kilometrs);
            dataStart = itemView.findViewById(R.id.dataOfStart);
            leftKilo = itemView.findViewById(R.id.leftKilo);
        }

        public void bind(Action work){
            nameWork.setText(work.getName());
            kilometrs.setText(Helper.kmToString(work.getKilometers()));
            dataStart.setText(Helper.getDateStringInNeedFormat(work.getDateStart()));
            //leftKilo.setText(work.getLeftKilo());
        }

    }

    public void setItems(Action work){
        listWork.add(work);
        notifyItemInserted(getItemCount());
    }

    public void clearItems(){
        listWork.clear();
        notifyDataSetChanged();
    }

}
