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
import com.practica.gpsodometr.data.model.SimpleItemTouchHelper;
import com.practica.gpsodometr.data.repository.ActionRep;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class SettingsAdapter extends RealmRecyclerViewAdapter<Action, SettingsAdapter.MyViewHolder> implements SimpleItemTouchHelper.ItemTouchHelperAdapter {

    public SettingsAdapter(OrderedRealmCollection<Action> data) {
        super(data, true);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Action action = getItem(position);
        holder.action = action;
        //noinspection ConstantConditions
        holder.nameWork.setText(action.getName());
        holder.kilometrs.setText(Helper.kmToString(action.getKilometers()));
        holder.dataStart.setText(Helper.dateToString(action.getDateStart()));
        //holder.leftKilo.setText(Helper.kmToString(action.leftKilometers));
    }

    @Override
    public void onItemDismiss(int position) {
        ActionRep.delete(getItem(position));
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nameWork;
        private TextView kilometrs;
        private TextView dataStart;
        private TextView leftKilo;
        public Action action;

        MyViewHolder(View view) {
            super(view);
            nameWork = itemView.findViewById(R.id.typeOfWork);
            kilometrs = itemView.findViewById(R.id.kilometrs);
            dataStart = itemView.findViewById(R.id.dataOfStart);
            leftKilo = itemView.findViewById(R.id.leftKilo);
        }
    }
}
}
