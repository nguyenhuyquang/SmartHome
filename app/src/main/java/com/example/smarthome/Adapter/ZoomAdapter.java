package com.example.smarthome.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.Model.HomeTypeModel;
import com.example.smarthome.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZoomAdapter extends RecyclerView.Adapter<ZoomAdapter.ZoomViewHolder> {
    List<HomeTypeModel> homeArray = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private static final String TAG = "ZoomAdapter";
    public ZoomAdapter(List<HomeTypeModel> homeArray, ItemClickListener itemClickListener) {
        this.homeArray = homeArray;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ZoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_list_device, parent, false);
        return new ZoomViewHolder(itemView,itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoomViewHolder holder, int position) {
        holder.setData(homeArray.get(position));
    }

    @Override
    public int getItemCount() {
        return homeArray.size();
    }

    public class ZoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_home)
        ImageView ivZoom;
        @BindView(R.id.tv_home)
        TextView tvZoom;
        private ItemClickListener itemClickListener;
        ImageView delete;

        public ZoomViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemClickListener=itemClickListener;
            itemView.setOnClickListener(this);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick delete: ");
//                    homeArray.remove(getAdapterPosition());
                    EventBus.getDefault().postSticky(new HomeTypeModel(homeArray.get(getAdapterPosition()).image, homeArray.get(getAdapterPosition()).nameRoom));
                    notifyDataSetChanged();
                }
            });
        }
        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(), false);

        }
        public void setData(final HomeTypeModel homeTypeModel) {
            Picasso.get().load(homeTypeModel.image).into(ivZoom);
            tvZoom.setText(homeTypeModel.nameRoom);
        }



    }
}
