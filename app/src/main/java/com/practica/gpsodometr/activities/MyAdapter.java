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
import com.practica.gpsodometr.data.model.PairActionAndKilometers;
import com.practica.gpsodometr.data.model.SimpleItemTouchHelper;
import com.practica.gpsodometr.data.repository.ActionRep;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements SimpleItemTouchHelper.ItemTouchHelperAdapter {

    private static ClickListener clickListener;

    private TextView nameWork;
    private TextView kilometrs;
    private TextView dataStart;
    private TextView leftKilo;

    private List<PairActionAndKilometers> listWork;

    MyAdapter(List<PairActionAndKilometers> pairActionAndKilometers) {
        this.listWork = pairActionAndKilometers;
    }

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

    public void print() {
        for (PairActionAndKilometers pairActionAndKilometers : listWork) {
            System.out.println(pairActionAndKilometers.action + " " + Helper.kmToString(pairActionAndKilometers.leftKilometers));
        }
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

        public void bind(PairActionAndKilometers pair) {
            nameWork.setText(pair.action.getName());
            kilometrs.setText(Helper.kmToString(pair.action.getKilometers()));
            dataStart.setText(Helper.dateToString(pair.action.getDateStart()));
            leftKilo.setText(Helper.kmToString(pair.leftKilometers));
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    /**
     * Вывод всех работ
     **/
    public void setItems(ArrayList<PairActionAndKilometers> pairActionAndKilometers) {
        //if(pairActionAndKilometers != null)
        listWork = pairActionAndKilometers;
    }

    public void addItem(PairActionAndKilometers e) {
        if (listWork == null)
            listWork = new ArrayList<>();
        listWork.add(e);
        notifyItemInserted(getItemCount());
    }

    public PairActionAndKilometers getItem(int position) {
        if (position >= 0 && position < getItemCount())
            return listWork.get(position);
        else
            return null;
    }

    //Для обновления данных
    public void updateInfo(int position, PairActionAndKilometers e) {
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


    public interface ClickListener{
        void onItemClick(int position, View v);
    }

}
