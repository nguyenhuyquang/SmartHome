package com.example.smarthome.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.Adapter.DeviceAdapter;
import com.example.smarthome.Adapter.ItemClickListener;
import com.example.smarthome.Model.FirebaseModel;
import com.example.smarthome.Model.HomeTypeModel;
import com.example.smarthome.R;
import com.example.smarthome.Utils.DatabaseFirebase;
import com.example.smarthome.Utils.MQTT;
import com.example.smarthome.Utils.OnClickItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class DeviceFragment extends Fragment implements ItemClickListener {
    Unbinder unbinder;
    String id;
    FirebaseDatabase firebaseDatabase;
    FirebaseModel firebaseModel;
    String nameDevice;
    @BindView(R.id.add)
    FloatingActionButton add;
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    List<HomeTypeModel> FutureList;
    List<String> codeDeviceList;
    DeviceAdapter deviceAdapter;
    private static final String TAG = "DeviceFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String code;
    String cmd;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DeviceFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_divice, container, false);
        unbinder = ButterKnife.bind(this, view);
        codeDeviceList = new ArrayList<>();
        FutureList = new ArrayList<>();

        deviceAdapter = new DeviceAdapter(FutureList, this);
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
        recyclerView.setAdapter(deviceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }


    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.d(TAG, "onClick: " + FutureList.get(position).nameRoom);
        Log.d(TAG, "code future:" + codeDeviceList.get(position).toString());
        String a = "{\"cmd\":\"" + id + "\", \"code\":\"" + codeDeviceList.get(position).toString() + "\"}";
        String payload = "{\"code\":\"" + codeDeviceList.get(position).toString() + "\"}";
        // dang ki MQTT broker

        MQTT.callback(getContext(), a);
    }

    @OnClick({R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add:
                openDialog();
                MQTT.callback(getContext(), " ");
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
                //subscribe mot topic de nhan code ve
                //lay dl tu broker ve up len firebase
                Log.d(TAG, "onClick: " + id);
                Log.d(TAG, "onClick: " + cmd);
                if (cmd.equals(id)) {
                    firebaseModel = new FirebaseModel(code, cmd);

                    Log.d(TAG, "name:" + nameDevice);
                    //ssubcribe

                    DatabaseFirebase.pushDataFirebaseFuture(firebaseModel, id, nameDevice, txt.getText().toString());
                    Log.d(TAG, "push len firebae " + firebaseModel.cmd + firebaseModel.code);
                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }
        });
        dialog.show();
    }

    @Subscribe(sticky = true)
    public void OnReceive(HomeTypeModel homeTypeModel) {
        Log.d(TAG, "OnReceive1: "+homeTypeModel.nameRoom);
        Log.d(TAG, "OnReceive device: "+ nameDevice);
        DatabaseFirebase.deleteFuture(id,nameDevice, homeTypeModel.nameRoom);

    }

    @Subscribe(sticky = true)
    public void onReceivedTopSong(OnClickItem onClickItem) {
        Log.d(TAG, "id:" + onClickItem.idDevice);
        Log.d(TAG, "name Device:" + onClickItem.homeTypeModel.nameRoom);
        getRoom(onClickItem.idDevice, onClickItem.homeTypeModel.nameRoom);
        id = onClickItem.idDevice;
        nameDevice = onClickItem.homeTypeModel.nameRoom;
    }

    @Subscribe(sticky = true)
    public void onReceived(FirebaseModel fire) {
        firebaseModel = new FirebaseModel(fire.code.toString(), fire.cmd.toLowerCase());
        code = fire.code.toString();
        cmd = fire.cmd.toString();
        Log.d(TAG, "received:" + fire.code + "\t" + fire.cmd);
    }

    public void getRoom(String id, String nameDevice) {
        List<HomeTypeModel> list = new ArrayList<>();
        List<String> listCode = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals(nameDevice)) {
                        for (DataSnapshot model : data.getChildren()) {
                            String dataCode = model.getValue().toString();
                            Log.d(TAG, "get code :" + dataCode);
                            listCode.add(dataCode);
                            list.add(new HomeTypeModel(R.raw.quat, model.getKey()));
                        }
                        break;
                    }
                }
                Log.d(TAG, "add lan so :" + list.size());
                FutureList.clear();
                codeDeviceList.clear();

                FutureList.addAll(list);
                codeDeviceList.addAll(listCode);

                list.clear();
                listCode.clear();

                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}