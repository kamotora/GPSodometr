package com.practica.gpsodometr.adapters;

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

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> implements SimpleItemTouchHelper.ItemTouchHelperAdapter {

    private ClickListener clickListener;
    private List<PairActionAndKilometers> listWork;

    public SettingsAdapter(ClickListener clickListener, List<PairActionAndKilometers> listWork) {
        this.clickListener = clickListener;
        this.listWork = listWork;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableforsettings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position, listWork.get(position));
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameWork;
        private TextView kilometrs;
        private TextView dataStart;
        private TextView leftKilo;
        private View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameWork = itemView.findViewById(R.id.typeOfWork);
            this.kilometrs = itemView.findViewById(R.id.kilometrs);
            this.dataStart = itemView.findViewById(R.id.dataOfStart);
            this.leftKilo = itemView.findViewById(R.id.leftKilo);
            this.itemView = itemView;
        }

        public void bind(final int position, final PairActionAndKilometers item) {
            this.nameWork.setText(item.action.getName());
            this.kilometrs.setText(Helper.kmToString(item.action.getKilometers()));
            this.dataStart.setText(Helper.dateToString(item.action.getDateStart()));
            this.leftKilo.setText(Helper.kmToString(item.leftKilometers));
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(position, item);
                }
            });
        }

    }


    public interface ClickListener {
        void onItemClick(int position, PairActionAndKilometers item);
    }

}