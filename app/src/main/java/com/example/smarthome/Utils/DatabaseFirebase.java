package com.example.smarthome.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smarthome.Model.DataAccount;
import com.example.smarthome.Model.FirebaseModel;
import com.example.smarthome.Model.HomeTypeModel;
import com.example.smarthome.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DatabaseFirebase {

    public static int a = 0;
    public static FirebaseDatabase firebaseDatabase;
    private static final String TAG = "DatabaseFirebase";
    public static DatabaseReference re;

    public static void pushDataFirebase(FirebaseModel firebaseModel, String id, String name, String feature) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child(id).child(name).child(feature).setValue(firebaseModel.code);
    }


    public static List<FirebaseModel> Read(String name) {
        List<FirebaseModel> list = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(name);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    FirebaseModel firebaseModel = data.getValue(FirebaseModel.class);
                    list.add(firebaseModel);
                }
                EventBus.getDefault().postSticky(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return list;
    }

    public static void pushAccount(String uid,DataAccount data){
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("user");
        reference.child(uid).setValue(data);
    }
    public static void getRoom(String uid) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("user").child(uid).child("List Room");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HomeTypeModel> list = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: " + data.getKey());
                    String name = data.getKey();
                    Log.d(TAG, "onClick: " + data.child("idDevice").getValue());
                    // String id = data.child("device").getValue().;
                    list.add(new HomeTypeModel(R.raw.bathroom, name,data.child("idDevice").getValue().toString()));
                    Log.d(TAG, "onDataChange: " + list.size());

                }
                EventBus.getDefault().postSticky(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void PushRoom(String uid,String nameRoom, String idDevice) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("user");
        HomeTypeModel temp = new HomeTypeModel(nameRoom,idDevice);
        databaseReference.child(uid).child("List Room").child(nameRoom).setValue(temp);
    }

    public static void deleteRoom(String uid) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("user").child(uid).child("List Room");
        reference.removeValue();

    }

    public static void pushDataFirebaseFuture(FirebaseModel firebaseModel, String id, String name, String feature) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(id).child(name);
        databaseReference.child("Please add new feauture").removeValue();
        databaseReference.child(feature).setValue(firebaseModel.code);
    }

    public static void deleteDevice(String id, String name){
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child(id).child(name).removeValue();
    }
    public static void deleteFuture(String id, String name, String future){
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(id).child(name);

        databaseReference.child(future).removeValue();
    }
}
