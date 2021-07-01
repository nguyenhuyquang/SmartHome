#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <IRremoteESP8266.h>
#include <IRrecv.h>
#include <IRsend.h>
#include <IRutils.h>
#include <Stream.h>
#include <string>
#include <ArduinoJson.h>
#include <WiFiManager.h>

int RECV_PIN = D3; //an IR detector/demodulatord is connected to GPIO pin D2
int SEND_PIN = D2;
// Update these with values suitable for your network.
//const char* ssid = "Galaxy S20 Ultra";
//const char* password = "22345678";
const char* mqtt_server = "broker.mqttdashboard.com";
//const char* mqtt_server = "iot.eclipse.org";


WiFiClient espClient;
PubSubClient client(espClient);
IRrecv irrecv(RECV_PIN);
IRsend irsend(SEND_PIN);
decode_results results;
WiFiManager wifiManager;

long lastMsg = 0;
char msg[50];
int value = 0;
String res="";
const char* idDevice = "testx";

void setup_wifi() {
  delay(100);
  irsend.begin();
  wifiManager.setBreakAfterConfig(true);
  wifiManager.resetSettings();
  if (!wifiManager.autoConnect("testx", "12345678")) {
    Serial.println("failed to connect, we should reset as see if it connects");
    delay(3000);
    ESP.restart();
    delay(5000);
  }
  //if you get here you have connected to the WiFi
  Serial.println("connected...yeey :)");
  Serial.println("local ip");
  Serial.println(WiFi.localIP());
  // We start by connecting to a WiFi network
  /*
  
   
    Serial.print("Connecting to ");
    Serial.println(ssid);
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) 
    {
      delay(500);
      Serial.print(".");
    }
  randomSeed(micros());
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
   */
}

void callback(char* topic, byte* payload, unsigned int length) 
{
  
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  char inData[80];

  for (int i = 0; i < length; i++) {
     inData[(i)] = (char)payload[i];
  
} //end callback

const size_t bufferSize = JSON_OBJECT_SIZE(2) + JSON_OBJECT_SIZE(3) + JSON_OBJECT_SIZE(5) + JSON_OBJECT_SIZE(8) + 370;
DynamicJsonBuffer jsonBuffer(bufferSize);
 JsonObject& root = jsonBuffer.parseObject(inData);  
 String code = root["code"];
 String idD = root["cmd"];
 Serial.println(idD);
 if(idD == idDevice)
 {
  uint64_t a;
 a = stringToUint64(code);
   Serial.print("Message: ");
 serialPrintUint64(a);
 irsend.sendNEC(a);   
 } 
}
void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) 
  {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    //if you MQTT broker has clientID,username and password
    //please change following line to    if (client.connect(clientId,userName,passWord))
    if (client.connect(clientId.c_str()))
    {
      Serial.println("connected");
     //once connected to MQTT broker, subscribe command if any
      client.subscribe("receivedata");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
} //end reconnect()

void setup() {
  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server,1883);
  client.setCallback(callback);
  irrecv.enableIRIn(); // Start the receiver
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  long now = millis();
  if (now - lastMsg > 2000) {
     lastMsg = now;
    if (irrecv.decode(&results)) 
    {
      dump(&results);
      irrecv.resume(); // Receive the next value
      String msg="";
       res = uint64ToString(results.value,10);
       msg += res;
       char message[58];
       strcpy(message, msg.c_str()); 
       const size_t m_bufferSize = JSON_OBJECT_SIZE(2) + JSON_OBJECT_SIZE(3) + JSON_OBJECT_SIZE(5) + JSON_OBJECT_SIZE(8) + 370;
        DynamicJsonBuffer JSONbuffer(m_bufferSize);
        JsonObject& JSONencoder = JSONbuffer.createObject();
        JSONencoder["cmd"] = idDevice;
        JSONencoder["code"] = message;
        char JSONmessageBuffer[100];
        JSONencoder.printTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
       Serial.println(res);
       int t = 0;
       while(t<5){
         client.publish("pushdata1", JSONmessageBuffer);
         delay(1000);
         t++;
       }
       //publish sensor data to MQTT broker
     
    } 
  }
}

String uint64ToString(uint64_t input) {
  String result = "";
  uint8_t base = 10;

  do {
    char c = input % base;
    input /= base;

    if (c < 10)
      c +='0';
    else
      c += 'A' - 10;
    result = c + result;
  } while (input);
  return result;
}
uint64_t stringToUint64(String input){
  uint64_t a;
  char* end;
  a= strtoull( input.c_str(), &end,10 );
  return a;
}
void dump(decode_results *results) {
  // Dumps out the decode_results structure.
  // Call this after IRrecv::decode()
  int count = results->rawlen;
  if (results->decode_type == UNKNOWN) {
    Serial.print("Unknown encoding: ");
  }
  else if (results->decode_type == NEC) {
    Serial.print("Decoded NEC: ");
  }
  else if (results->decode_type == SONY) {
    Serial.print("Decoded SONY: ");
  }
  else if (results->decode_type == RC5) {
    Serial.print("Decoded RC5: ");
  }
  else if (results->decode_type == RC6) {
    Serial.print("Decoded RC6: ");
  }
  else if (results->decode_type == PANASONIC) {
    Serial.print("Decoded PANASONIC - Address: ");
    Serial.print(results->address, HEX);
    Serial.print(" Value: ");
  }
  else if (results->decode_type == LG) {
    Serial.print("Decoded LG: ");
  }
  else if (results->decode_type == JVC) {
    Serial.print("Decoded JVC: ");
  }
  else if (results->decode_type == AIWA_RC_T501) {
    Serial.print("Decoded AIWA RC T501: ");
  }
  else if (results->decode_type == WHYNTER) {
    Serial.print("Decoded Whynter: ");
  }
}
