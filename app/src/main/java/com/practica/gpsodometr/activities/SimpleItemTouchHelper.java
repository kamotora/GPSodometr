package com.practica.gpsodometr.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter  mAdapter;

    public SimpleItemTouchHelper(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    public interface ItemTouchHelperAdapter{
        void onItemDismiss(int position);
    }

    @Override
    public boolean isLongPressDragEnabled(){
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled(){
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
