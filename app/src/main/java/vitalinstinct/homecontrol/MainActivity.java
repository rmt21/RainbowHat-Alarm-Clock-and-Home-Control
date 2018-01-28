package vitalinstinct.homecontrol;

import android.app.Activity;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.EditText;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {

    private static final String FIREBASE_HOME_CONTROL_URL = "";
    DocumentReference dbRef;
    Bundle send;
    Message msg;
    ProcessHandler handler;
    EditText time, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = (EditText)findViewById(R.id.time);
        date = (EditText)findViewById(R.id.date);

        dbRef = FirebaseFirestore.getInstance().collection("BedLights").document("LightDoc");
        HandlerThread ht = new HandlerThread("HandleProcesses", Process.THREAD_PRIORITY_BACKGROUND);
        ht.start();
        Looper looper = ht.getLooper();
        handler = new ProcessHandler(looper, getApplicationContext(), this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        dbRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists())
                {
                    send = new Bundle();
                    msg = new Message();
                    send.putString("type", "BedLights");
                    send.putBoolean("status", documentSnapshot.getBoolean("lightsOn"));
                    send.putString("colour", documentSnapshot.getString("colour"));
                    send.putString("operation", documentSnapshot.getString("operation"));
                    send.putString("lastChange", documentSnapshot.getString("lastChange"));
                    msg.setData(send);
                    handler.sendMessage(msg);
                }
                else if (e != null)
                {
                    Log.w("ERROR", e);
                }
            }
        });

    }

    public void changeLightStatus(boolean input)
    {
        Map<String, Boolean> saveData = new HashMap<String, Boolean>();
        saveData.put("lightsOn", input);
        dbRef.set(saveData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("DB", "UPDATED");
            }
        });

    }
}
