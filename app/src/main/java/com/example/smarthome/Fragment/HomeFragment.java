package com.example.smarthome.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.Activity.LoginActivity;
import com.example.smarthome.Adapter.HomeAdapter;
import com.example.smarthome.Adapter.ItemClickListener;
import com.example.smarthome.Model.DataAccount;
import com.example.smarthome.Model.HomeTypeModel;
import com.example.smarthome.R;
import com.example.smarthome.Utils.DatabaseFirebase;
import com.example.smarthome.Utils.FragmentUtils;
import com.example.smarthome.Utils.OnClickItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class HomeFragment extends Fragment implements ItemClickListener {
    FirebaseUser mUser;
    
    Unbinder unbinder;
    HomeAdapter homeAdapter;
    String nameRoom;
    String idDevice;
    List<HomeTypeModel> homeTypeModelList;
    @BindView(R.id.rv)
    RecyclerView rvMusicTypes;
    private static final String TAG = "HomeActivity";
    Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "User: " + mUser.getUid());
        DatabaseFirebase.getRoom( mUser.getUid());
//        homeTypeModelList.add(new HomeTypeModel(R.raw.living_room, "living room"));
//        homeTypeModelList.add(new HomeTypeModel(R.raw.bathroom, "Bath room"));
//        homeTypeModelList.add(new HomeTypeModel(R.raw.kitchen, "Kitchen room"));
//        homeTypeModelList.add(new HomeTypeModel(R.raw.bed_room, "Bed room"));
//        homeTypeModelList.add(new HomeTypeModel(R.raw.jym_room, "Jym room"));
//        homeTypeModelList.add(new HomeTypeModel(R.raw.studi_room, "Study room"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        homeTypeModelList = new ArrayList<>();
        EventBus.getDefault().register(this);
        homeAdapter = new HomeAdapter(homeTypeModelList, this, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(),
                2,
                LinearLayoutManager.VERTICAL,
                false
        );
        //chia anh ra lam hai cot
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 0 ? 2 : 1;
            }
        });

        rvMusicTypes.setAdapter(homeAdapter);
        rvMusicTypes.setItemAnimator(new SlideInLeftAnimator());
//        rvMusicTypes.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
        rvMusicTypes.setLayoutManager(gridLayoutManager);
        rvMusicTypes.setItemAnimator(new ScaleInAnimator());


        return view;
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.d(TAG, "onClick: " + position );
        // dialogID(position);
        EventBus.getDefault().postSticky(new OnClickItem(homeTypeModelList.get(position), position ,homeTypeModelList.get(position).idDevice.toString()));
        FragmentUtils.openFragment(getFragmentManager(), R.id.ll_home_fm, new ZoomFragment());
    }

   /* public void dialogID(int position) {
        Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Language to translate");
        dialog.setContentView(R.layout.identify_id);
        dialog.setCancelable(false);
        Button add = dialog.findViewById(R.id.buttonOk);
        EditText txt = dialog.findViewById(R.id.room);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new OnClickItem(homeTypeModelList.get(position), position, txt.getText().toString()));
//                homeTypeModelList.add(new HomeTypeModel(R.raw.bathroom, txt.getText().toString()));
//                homeAdapter.notifyDataSetChanged();
//                Log.d(TAG, "onClick1: " + homeTypeModelList.size());

                dialog.cancel();
            }
        });
        dialog.show();
    }
*/
    @OnClick({R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add:
                openDialog();
                break;
        }
    }

    private void openDialog() {
        final int check = 0;
        Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Language to translate");
        dialog.setContentView(R.layout.dialod_add_room);
        dialog.setCancelable(false);
        Button add = dialog.findViewById(R.id.buttonOk);
        EditText txt = dialog.findViewById(R.id.txt_result);
        EditText id = dialog.findViewById(R.id.txt_idDevice);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick1: " + homeTypeModelList.size());
                nameRoom = txt.getText().toString();
                idDevice =  id.getText().toString();
                DatabaseFirebase.PushRoom(mUser.getUid(),nameRoom,idDevice);
                dialog.cancel();
            }
        });
        dialog.show();
    }



    @Subscribe(sticky = true)
    public void onReceivedData(List<HomeTypeModel> list) {
        homeTypeModelList.clear();
        Log.d(TAG, "onReceivedTopSong: " +list.size());
        homeTypeModelList.addAll(list);
        Log.d(TAG, "onReceivedTopSong1: "+homeTypeModelList.size());
//        homeTypeModelList.add(new HomeTypeModel(R.raw.bathroom, "a"));
        homeAdapter.notifyItemChanged(list.size()-1);
    }

}