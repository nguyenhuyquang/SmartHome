package com.example.smarthome.Utils;

import android.content.Context;
import android.util.Log;

import com.example.smarthome.Model.FirebaseModel;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MQTT {
    public static MqttAndroidClient client;
    private static final String TAG = "MQTT";

    public static void callback(Context context, String payload) {
        MqttConnectOptions options = new MqttConnectOptions();
        String clientID = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, "tcp://broker.mqttdashboard.com:1883", clientID);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "messageArrived: " + message.toString());
                ArrayList<String> arrayList = new ArrayList<>();
                Log.d(TAG, "messageArrived: "+message);
                String data = message.toString();
                String regex = data.replaceAll("\"", "");
                String mess = regex.substring(1, regex.length() - 1);

                for (String a : mess.split(",")) {
                    for (String b : a.split(":"))
                        arrayList.add(b.toString());
                }

                //unsubscribe
                FirebaseModel firebaseModel = new FirebaseModel(arrayList.get(3), arrayList.get(1));
                EventBus.getDefault().postSticky(firebaseModel );
                if (!message.toString().equals("")) {
                    Unsubscribe();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess: ");
                    if( payload.equals(" ")){
                        subscribe();
                    }else{
                        subscribe();
                        publish(payload);
                    }


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "onFailure: ");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void publish(String payload) {
        String topic = "receivedata";

        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public static void subscribe() {
        String topic = "pushdata1";
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess: subscribe");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void Unsubscribe() {
        final String topic = "pushdata1";
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Unsubscribe Success: ");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
