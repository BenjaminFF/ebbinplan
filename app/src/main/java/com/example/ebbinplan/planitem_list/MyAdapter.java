package com.example.ebbinplan.planitem_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebbinplan.R;
import com.example.ebbinplan.model.PlanItem;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private List<PlanItem> planItems;

    public MyAdapter(List<PlanItem> planItems) {
        this.planItems = planItems;
    }

     class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView planNameTextView;

        public MyViewHolder(View view) {
            super(view);
            planNameTextView = view.findViewById(R.id.plan_item_name);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_item_layout, parent, false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.planNameTextView.setText(planItems.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return planItems.size();
    }
}
