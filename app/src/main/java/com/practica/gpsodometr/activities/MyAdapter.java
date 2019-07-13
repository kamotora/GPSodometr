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
import com.practica.gpsodometr.data.model.SimpleItemTouchHelper;
import com.practica.gpsodometr.data.repository.ActionRep;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements SimpleItemTouchHelper.ItemTouchHelperAdapter {

    private static ClickListener clickListener;

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

    private List<PairOfActionAndKm> listWork = new ArrayList<>();

    /**
     * Удаление с таблицы и из бд
     */
    @Override
    public void onItemDismiss(int position) {
        Action action = listWork.get(position).action;
        ActionRep.delete(action);
        listWork.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameWork = itemView.findViewById(R.id.typeOfWork);
            kilometrs = itemView.findViewById(R.id.kilometrs);
            dataStart = itemView.findViewById(R.id.dataOfStart);
            leftKilo = itemView.findViewById(R.id.leftKilo);
        }

        public void bind(PairOfActionAndKm pair) {
            nameWork.setText(pair.action.getName());
            kilometrs.setText(Helper.kmToString(pair.action.getKilometers()));
            dataStart.setText(Helper.getDateStringInNeedFormat(pair.action.getDateStart()));
            leftKilo.setText(Helper.kmToString(pair.leftKilometers));
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    public void setItems(PairOfActionAndKm pair) {
        listWork.add(pair);
        notifyItemInserted(getItemCount());
    }

    public void setItems(Action action, Double leftKm) {
        listWork.add(new PairOfActionAndKm(action, leftKm));
        notifyItemInserted(getItemCount());
    }

    //Для обновления данных
    public void updateInfo(int position, PairOfActionAndKm e){
        listWork.set(position,e);
        notifyItemChanged(position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        MyAdapter.clickListener = clickListener;
    }

    public void clearItems(){
        listWork.clear();
        notifyDataSetChanged();
    }

    static class PairOfActionAndKm {
        final Action action;
        final Double leftKilometers;

        PairOfActionAndKm(Action action, Double leftKilometers) {
            this.action = action;
            this.leftKilometers = leftKilometers;
        }
    }

    public interface ClickListener{
        void onItemClick(int position, View v);
    }

}
