package com.example.smarthome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.Model.HomeTypeModel;
import com.example.smarthome.R;
import com.example.smarthome.Utils.DatabaseFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    List<HomeTypeModel> homeArray = new ArrayList<>();
    private ItemClickListener itemClickListener;
    Context context;
    FirebaseUser mUser;
    public HomeAdapter(List<HomeTypeModel> homeArray, ItemClickListener itemClickListener, Context context) {
        this.homeArray = homeArray;
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_list_home, parent, false);
        return new HomeViewHolder(itemView, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        holder.setData(homeArray.get(position));
    }

    @Override
    public int getItemCount() {
        return homeArray.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_home)
        ImageView ivHome;
        @BindView(R.id.tv_home)
        TextView tvHome;
        private ItemClickListener itemClickListener;
        ImageView delete;


        public HomeViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemClickListener = itemClickListener;
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   homeArray.remove(getAdapterPosition());
                    DatabaseFirebase.deleteRoom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    for (HomeTypeModel model: homeArray){
                        DatabaseFirebase.PushRoom(FirebaseAuth.getInstance().getCurrentUser().getUid(),model.nameRoom,model.idDevice);
                    }

                    notifyDataSetChanged();
                }
            });
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);


        }


        public void setData(final HomeTypeModel homeTypeModel) {
            Picasso.get().load(homeTypeModel.image).into(ivHome);
            tvHome.setText(homeTypeModel.nameRoom);
        }


    }
}
