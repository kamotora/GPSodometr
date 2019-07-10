package com.practica.gpsodometr.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practica.gpsodometr.R;

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

    private List<Work> listWork = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameWork = itemView.findViewById(R.id.typeOfWork);
            kilometrs = itemView.findViewById(R.id.kilometrs);
            dataStart = itemView.findViewById(R.id.dataOfStart);
            leftKilo = itemView.findViewById(R.id.leftKilo);
        }

        public void bind(Work work){
            nameWork.setText(work.getNameWork());
            kilometrs.setText(work.getKilometrs());
            dataStart.setText(work.getDataStart());
            leftKilo.setText(work.getLeftKilo());
        }

    }

    public void setItems(Work work){
        listWork.add(work);
        notifyItemInserted(getItemCount());
    }

    public void clearItems(){
        listWork.clear();
        notifyDataSetChanged();
    }

}
