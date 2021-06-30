package com.example.smarthome.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.Adapter.ItemClickListener;
import com.example.smarthome.Adapter.ZoomAdapter;
import com.example.smarthome.Model.DeviceModel;
import com.example.smarthome.Model.FirebaseModel;
import com.example.smarthome.Model.HomeTypeModel;
import com.example.smarthome.R;
import com.example.smarthome.Utils.DatabaseFirebase;
import com.example.smarthome.Utils.FragmentUtils;
import com.example.smarthome.Utils.OnClickItem;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class ZoomFragment extends Fragment implements ItemClickListener {
    Unbinder unbinder;
    FirebaseModel firebaseModel;
    String id;
    String nameDevice;

    public static FirebaseDatabase firebaseDatabase;
    private static final String TAG = "ZoomFragment";
    List<HomeTypeModel> typeModelListHome;
    @BindView(R.id.iv_type)
    ImageView ivType;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.back)
    ImageView back;
    ZoomAdapter zoomAdapter;

    public ZoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zoom, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        typeModelListHome = new ArrayList<>();

        //ham lay vi tri cua text trong
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

                if (i == 0) {

                    toolbar.setBackground(getResources().getDrawable(
                            R.drawable.custom_gradien));
                } else {
                    toolbar.setBackground(null);
                }
            }
        });
        rv.setItemAnimator(new FadeInLeftAnimator());
        zoomAdapter = new ZoomAdapter(typeModelListHome, this);
        rv.setAdapter(zoomAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
        FragmentUtils.openFragment(getFragmentManager(), R.id.ll_home_fm, new DeviceFragment());
        Log.d(TAG, "send device :  " + id + typeModelListHome.get(position).nameRoom);
        EventBus.getDefault().postSticky(new OnClickItem(typeModelListHome.get(position), position, id));

    }


    @Subscribe(sticky = true)
    public void onReceivedData(OnClickItem homeTypeModel) {

        List<HomeTypeModel> list = new ArrayList<>();
        HomeTypeModel model = homeTypeModel.homeTypeModel;
        Picasso.get().load(model.image).into(ivType);
        tvType.setText(model.nameRoom);
        id = homeTypeModel.idDevice;
//        Log.d(TAG, "onReceivedTopSong: " + model.nameRoom);
//        Log.d(TAG, "onReceivedID" + homeTypeModel.id);
        getRoom(id);

    }

    @Subscribe(sticky = true)
    public void OnReceive(HomeTypeModel homeTypeModel) {
        Log.d(TAG, "OnReceive1: "+homeTypeModel.nameRoom);
        DatabaseFirebase.deleteDevice(id,homeTypeModel.nameRoom);
    }

    public void getRoom(String id) {
        List<HomeTypeModel> list = new ArrayList<>();
        // List<ReadDeviceModel> futureAndCodeModelList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {


                    list.add(new HomeTypeModel(R.raw.quat, data.getKey()));


                }
                Log.d(TAG, "add lan so :" + list.size());
                typeModelListHome.clear();
                typeModelListHome.addAll(list);
                list.clear();
                zoomAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @OnClick({R.id.back, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                getActivity().onBackPressed();
                break;
            case R.id.fab:
                openDialog();
                break;
        }
    }

    private void openDialog() {
        final int check = 0;
        Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Language to translate");
        dialog.setContentView(R.layout.add_device);
        EditText txt = dialog.findViewById(R.id.txt_result);
        Button oke = dialog.findViewById(R.id.buttonOk);
        dialog.setCancelable(false);
        oke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseModel = new FirebaseModel("none", "none");
                DatabaseFirebase.pushDataFirebase(firebaseModel, id, txt.getText().toString(), "Please add new feauture");

                nameDevice = txt.getText().toString();
                EventBus.getDefault().postSticky(new DeviceModel(nameDevice, id));
                dialog.cancel();

            }
        });

        dialog.show();
    }
}