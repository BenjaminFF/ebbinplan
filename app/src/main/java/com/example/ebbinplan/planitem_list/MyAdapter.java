package com.example.ebbinplan.planitem_list;

import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.ebbinplan.R;
import com.example.ebbinplan.model.PlanItem;

import org.litepal.LitePal;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private List<PlanItem> planItems;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private OnDeleteItemListener onDeleteItemListener;

    public MyAdapter(List<PlanItem> planItems) {
        this.planItems = planItems;
    }

     class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView planNameTextView;
        public FrameLayout deleteItemButton;
        public RelativeLayout planItemMain;
        public SwipeRevealLayout swipeRevealLayout;

        public MyViewHolder(View view) {
            super(view);
            swipeRevealLayout=view.findViewById(R.id.plan_item_swipe_reveal_layout);
            planNameTextView = view.findViewById(R.id.plan_item_name);
            deleteItemButton=view.findViewById(R.id.plan_item_delete);
            planItemMain=view.findViewById(R.id.plan_item_main);
        }
    }

    public interface OnDeleteItemListener{
        void onDeleteItem(int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_item_layout, parent, false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.planNameTextView.setText(planItems.get(position).getName());
        viewBinderHelper.bind(holder.swipeRevealLayout,planItems.get(position).getId()+"");
        holder.deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String deletedId=planItems.get(position).getId()+"";
                LitePal.delete(PlanItem.class,planItems.get(position).getId());
                planItems.remove(position);
                notifyItemRemoved(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewBinderHelper.closeLayout(deletedId);
                        notifyDataSetChanged();
                    }
                },500);
                if(onDeleteItemListener!=null){
                    onDeleteItemListener.onDeleteItem(position);
                }
            }
        });
        holder.planItemMain.setBackgroundColor(position%2==0?Color.WHITE:Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return planItems.size();
    }

    public void setOnDeleteItemListener(OnDeleteItemListener onDeleteItemListener){
        this.onDeleteItemListener=onDeleteItemListener;
    }
}
