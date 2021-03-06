package myosample.batzeesappstudio.com.myo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.os.Handler;

import com.dtw.FastDtwTest;

import org.json.JSONException;
import org.json.JSONObject;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class HomeScreen extends Activity {


    private TextView mTextView;
    private Button start;
    private ToggleButton switchMode;
    private EditText gestureName;
    private EditText gestureWord;
    private TextView state;
    TextToSpeech t1;

    State currState;
    ArrayList<String> bufferedData;
    ArrayList<String> writeBuffer;
    int count =0;
    File dir;
    String filename;
    AIDataService aiDataService;
    AIRequest aiRequest;
    TestQueue queue;

    boolean isRecording = false;
    boolean isConnected = false;

    boolean prevIntertialStatus=false;
    private long counter=0;
    String msg;
    String word;
    String answer;


    String ACCESS_TOKEN = "0400727d47564f1a9b279aca7e84fbc0";

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            Log.v("MainActivity", "Myo Connected");
            isConnected = true;

            mTextView.setTextColor(Color.CYAN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
            isConnected = false;
            Log.v("MainActivity", "Myo Diconnected");
            mTextView.setTextColor(Color.RED);
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            Log.v("MainActivity", "onArmSync");
            mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            Log.v("MainActivity", "onArmUnsync");
            mTextView.setText(R.string.hello_world);
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
            Log.v("MainActivity", "Myo unlocked");

        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
            Log.v("MainActivity", "Myo locked");

        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
            float x = (float) rotation.x();
            float y = (float) rotation.y();
            float z = (float) rotation.z();
            float w = (float) rotation.w();

            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }


            if(switchMode.isChecked())
            {
                queue.addDataQueue(x,y,z,w,roll,pitch,yaw,currState.pose);

            }

            if(isRecording == true) {

                currState.roll = roll;
                currState.pitch = pitch;
                currState.yaw = yaw;
                currState.x = x;
                currState.y = y;
                currState.z = z;
                currState.w = w;

                String data = currState.x + ", " + currState.y + "," + currState.z + "," + currState.w + "," + currState.roll + "," + currState.pitch + "," + currState.yaw + "," + currState.pose;

                if (count == 100) {
                    count = 0;
                    Log.v("SensorService", "100 lines reached");
                    writeBuffer = new ArrayList<String>(bufferedData);
                    bufferedData.clear();
                    new WriteFile().execute();


                } else {
                    bufferedData.add(data);

                }
                count++;

            }


            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            //mTextView.setRotation(roll);
            //mTextView.setRotationX(pitch);
            //mTextView.setRotationY(yaw);
            triggerGestureRecognition();

        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            switch (pose) {
                case UNKNOWN:
                    mTextView.setText("UNKNOWN");
                    currState.pose = "Unknown";
                    break;
                case REST:
                case DOUBLE_TAP:
                    int restTextId = R.string.hello_world;

                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            //currState.pose = "Left_DoubleTap";
                            //mTextView.setText("Double Tap");
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            //currState.pose = "Right_DoubleTap";
                            //mTextView.setText("Double Tap");
                            break;
                    }
                    //mTextView.setText("Double Tap");
                    break;
                case FIST:
                    mTextView.setText(getString(R.string.pose_fist));
                    currState.pose = "Fist";
                    Log.v("MainActivity", "Fist");

                    break;
                case WAVE_IN:
                    mTextView.setText(getString(R.string.pose_wavein));
                    currState.pose = "WaveIn";
                    Log.v("MainActivity", "WaveIn");
                    break;
                case WAVE_OUT:
                    mTextView.setText(getString(R.string.pose_waveout));
                    currState.pose = "WaveOut";
                    Log.v("MainActivity", "Wave out");
                    break;
                case FINGERS_SPREAD:
                    mTextView.setText(getString(R.string.pose_fingersspread));
                    currState.pose = "FingersSpread";
                    Log.v("MainActivity", "Fingers Spread");
                    break;
            }

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);


        currState = State.getInstance();
        bufferedData = new ArrayList<String>();
        mTextView = (TextView) findViewById(R.id.text);
        start = (Button) findViewById(R.id.start);
        switchMode = (ToggleButton) findViewById(R.id.switchMode);
        gestureName = (EditText) findViewById(R.id.gestureName);


        gestureWord = (EditText) findViewById(R.id.gestureWord);
        isRecording = false;
        queue = new TestQueue();
        Log.v("OnCreate Switch", ""+switchMode.isChecked());

        File root = android.os.Environment.getExternalStorageDirectory();
        dir = new File (root.getAbsolutePath() + File.separator + "MYO");
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Training");
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Trained");
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Test");
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Test");



        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    //Testing Mode
                    switchMode.setText("Test");
                    //isTrain = false;

                    gestureName.setVisibility(View.INVISIBLE);
                    gestureWord.setVisibility(View.INVISIBLE);
                    File root = android.os.Environment.getExternalStorageDirectory();
                    dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Test");

                }
                else
                {
                    //Training Mode
                    switchMode.setText("Train");
                    //isTrain = true;

                    gestureName.setVisibility(View.VISIBLE);
                    gestureWord.setVisibility(View.VISIBLE);
                    File root = android.os.Environment.getExternalStorageDirectory();
                    dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Training");
                }
                Log.v("Switch", Boolean.toString(isChecked));
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isRecording == false)
                {
                    //Start Recording
                    if(!switchMode.isChecked())
                    {
                        //Start Recording in Training Mode

                        String gesture = gestureName.getText().toString();
                        String word = gestureWord.getText().toString();
                        filename = "train" + "_" + gesture + "_" +  word + "_" + getFileName();

                    }
                    else
                    {
                        //Start Recording in Test Mode
                        filename = "test_" + getFileName();

                        new AutomaticStop().execute();

                    }
                    isRecording = true;
                    start.setText("Stop");
                    Log.v("Data Logging", "Started Recording");
                }
                else
                {
                    //Stop Recording
                    isRecording = false;
                    start.setText("Start");
                    Log.v("Logging Data", "Stopped Recording");


                    if(!switchMode.isChecked())
                    {
                        //Stopped after Training
                        Log.v("Data Logging", "Stopped after training");
                        File root = android.os.Environment.getExternalStorageDirectory();
                        dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Training");
                        //String gesturename = filename.split("_")[]

                        File lastModified = lastFileModified(dir.getAbsolutePath());




                        // Take average
                        CSVNormalizer csv = new CSVNormalizer();
                        try {
                            csv.readCSV(lastModified);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        csv.normalize();
                        String filename = lastModified.getName();
                        Log.v("Last Modified", filename);
                        File newDir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Trained");
                        csv.writeData(newDir.getAbsolutePath(),filename);

                        Log.v("Training", "Trained");

                    }
                    else
                    {

                        Log.v("Data Logging", "Stopped after testing");
                        File root = android.os.Environment.getExternalStorageDirectory();
                        dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Test");

                        // Take average
                        File lastModified = lastFileModified(dir.getAbsolutePath());

                        CSVNormalizer csv = new CSVNormalizer();
                        try {
                            csv.readCSV(lastModified);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //csv.normalize();
                        //String pose = csv.findMode();
                        List pose = csv.findMode2();

                        //Log.v("Mode Pose",pose);
                        String filename = lastModified.getName();
                        dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Test");
                        truncateSDFile(dir.getAbsolutePath(),filename);
                        csv.writeData(dir.getAbsolutePath(),filename);




                        //Stopped after Testing


                        /*File[] trainedFiles = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Trained").listFiles();
                        for(File f: trainedFiles)
                        {
                            Log.v("Testing", ""+f.getAbsolutePath());
                            FastDtwTest.DTW(f.getAbsolutePath(),dir.getAbsolutePath()+ File.separator+filename,true);
                        }*/



                        /*NewGesture newGesture = new NewGesture();
                        //int gesture_no = newGesture.detectGesture(new File(dir.getAbsolutePath(),filename));
                        //int gesture_no = newGesture.gestureDetection(new File(dir.getAbsolutePath(),filename));*/
                        word = "sorry";
                        String ges1, ges2;
                        if(pose.size() >1) {

                            ges1 = pose.get(0).toString().split("=")[0];
                            ges2 = pose.get(1).toString().split("=")[0];


                            if ((ges1.equals("Fist") && ges2.equals("FingersSpread") || (ges1.equals("FingersSpread") && ges2.equals("Fist")))) {
                                word = "helloMyo";


                            }

                            if ((ges1.equals("WaveOut") && ges2.equals("WaveIn") || (ges1.equals("WaveIn") && ges2.equals("WaveOut")))) {
                                word = "howYouDoin";

                            }
                        }

                        aiRequest.setQuery(word);
                        new AsyncTask<AIRequest, Void, AIResponse>() {
                            @Override
                            protected AIResponse doInBackground(AIRequest... requests) {
                                final AIRequest request = requests[0];
                                try {
                                    final AIResponse response = aiDataService.request(aiRequest);
                                    return response;
                                } catch (AIServiceException e) {
                                }
                                return null;
                            }
                            @Override
                            protected void onPostExecute(AIResponse aiResponse) {
                                if (aiResponse != null) {
                                    // process aiResponse here
                                    Log.v("Result!!!",aiResponse.getResult().getFulfillment().getSpeech());
                                    answer = aiResponse.getResult().getFulfillment().getSpeech();
                                    TTS(answer);



                                }
                            }
                        }.execute(aiRequest);
                        //Call DTW
                    }

                }

            }

        });

        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        else
        {
            Toast.makeText(this, "Hub initialized", Toast.LENGTH_SHORT).show();

        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
        Log.v("Myo", "Listener added");
        setLockPolicy();

        final AIConfiguration config = new AIConfiguration(ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(this,config);
        aiRequest = new AIRequest();



        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    Log.i("TextToSpeech","Initializaed");
                    t1.setLanguage(Locale.UK);
                    t1.setSpeechRate((float)0.7);
                    //Log.i("TTS",t1.getVoices().toString());
                    //Log.i("TTS",Boolean.toString(flag));
                }
            }
        });

        //TTS("Hey, how are you?");


    }

    private class ApiAi extends AsyncTask<AIRequest, Void, AIResponse>
    {
        @Override
        protected AIResponse doInBackground(AIRequest... requests) {
            final AIRequest request = requests[0];
            try {
                final AIResponse response = aiDataService.request(aiRequest);
                return response;
            } catch (AIServiceException e) {
            }
            return null;
        }
        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            if (aiResponse != null) {
                // process aiResponse here
                Log.v("Response", aiResponse.toString());
            }
        }
    }
    private void setLockPolicy() {
        Hub.getInstance().setLockingPolicy(Hub.LockingPolicy.NONE);
    }
    protected void onResume()
    {
        super.onResume();


        if(switchMode.isChecked())
        {
            //Test Mode

            switchMode.setText("Test");
            //isTrain = false;

            gestureName.setVisibility(View.INVISIBLE);
            gestureWord.setVisibility(View.INVISIBLE);
            File root = android.os.Environment.getExternalStorageDirectory();
            dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Test");

        }
        else
        {
            //Train Mode
            switchMode.setText("Train");
            //isTrain = true;

            gestureName.setVisibility(View.VISIBLE);
            gestureWord.setVisibility(View.VISIBLE);
            File root = android.os.Environment.getExternalStorageDirectory();
            dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Training");
        }
        if(isConnected== false) {
            onScanActionSelected();
        }


    }
    public String getFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String currentDateandTime = sdf.format(new Date());
        //android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        String filename = currentDateandTime + ".csv";
        return filename;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private class WriteFile extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            Log.v("SensorService", "Data logged");
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Log.v("SensorService", "Size of write buffer: " + writeBuffer.size());
            writeToSDFile();
            writeBuffer.clear();

            return null;

        }
    }

    private void writeToSDFile(String d){


        File file = new File(dir + File.separator + filename);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write(d + "\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeToSDFile(){

        Log.v("SensorService", "Size of write buffer: " + writeBuffer.size());
        Log.v("SensorService", "Writing to file" + dir +"/"+ filename);


        File file = new File(dir + File.separator + filename);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            for(int i=0;i<writeBuffer.size(); i++) {
                osw.write(writeBuffer.get(i) + "\n");
            }
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File lastFileModified(String dir) {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }
    private void truncateSDFile(String dir, String filename) {

        String filepath = dir + File.separator + filename;
        try {
            PrintWriter pw = new PrintWriter(filepath);
            pw.close();
            Log.v("Monitor", "Sensor file Truncated!");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }
    public void triggerGestureRecognition(){
        if(prevIntertialStatus==false && queue.isInertial==true){
            counter=1;
            //startGestureRecognition();
        }else if(prevIntertialStatus==true && queue.isInertial==true){
            counter++;
        }else if(prevIntertialStatus==true && queue.isInertial==false){
            Log.v("HomeScreen","Callng Gesture");
            //recognizeGesture();
        }
        prevIntertialStatus=queue.isInertial;
    }
    public void recognizeGesture(){

        //Stop Recording
        isRecording = false;
        start.setText("Start");
        Log.v("Logging Data", "Stopped Recording");


        if(!switchMode.isChecked())
        {
            //Stopped after Training
            Log.v("Data Logging", "Stopped after training");
            File root = android.os.Environment.getExternalStorageDirectory();
            dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Training");
            File lastModified = lastFileModified(dir.getAbsolutePath());


            // Take average
            CSVNormalizer csv = new CSVNormalizer();
            try {
                csv.readCSV(lastModified);
            } catch (IOException e) {
                e.printStackTrace();
            }
            csv.normalize();
            String filename = lastModified.getName();
            Log.v("Last Modified", filename);
            File newDir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Trained");
            csv.writeData(newDir.getAbsolutePath(),filename);

            Log.v("Training", "Trained");

        }
        else
        {

            Log.v("Data Logging", "Stopped after testing");
            File root = android.os.Environment.getExternalStorageDirectory();
            dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Test");

            // Take average
            File lastModified = lastFileModified(dir.getAbsolutePath());

            CSVNormalizer csv = new CSVNormalizer();
            try {
                csv.readCSV(lastModified);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //csv.normalize();

            String pose = csv.findMode();
            Log.v("Mode Pose",pose);
            String filename = lastModified.getName();
            dir = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Test");
            truncateSDFile(dir.getAbsolutePath(),filename);
            csv.writeData(dir.getAbsolutePath(),filename);




            //Stopped after Testing


                        /*File[] trainedFiles = new File (root.getAbsolutePath() + File.separator + "MYO" + File.separator + "Trained").listFiles();
                        for(File f: trainedFiles)
                        {
                            Log.v("Testing", ""+f.getAbsolutePath());
                            FastDtwTest.DTW(f.getAbsolutePath(),dir.getAbsolutePath()+ File.separator+filename,true);
                        }*/

            /*NewGesture newGesture = new NewGesture();
            int gesture_no = newGesture.gestureDetection(new File(dir.getAbsolutePath(),filename));*/

            switch(pose)
            {
                case "Fist":
                    word = "time";
                    break;
                case "FingersSpread":
                    word= "weather in bangalore in celsius";
                    break;
                case "Right_DoubleTap":
                    word = "youSpeak";
                    break;
                case "WaveOut":
                    word = "letsGoOut";
                    break;
                case "WaveIn":
                    word = "letsGoIn";
                    break;
            }


            aiRequest.setQuery(word);
            new AsyncTask<AIRequest, Void, AIResponse>() {
                @Override
                protected AIResponse doInBackground(AIRequest... requests) {
                    final AIRequest request = requests[0];
                    try {
                        final AIResponse response = aiDataService.request(aiRequest);
                        return response;
                    } catch (AIServiceException e) {
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(AIResponse aiResponse) {
                    if (aiResponse != null) {
                        // process aiResponse here
                        Log.v("Result!!!",aiResponse.getResult().getFulfillment().getSpeech());
                        answer = aiResponse.getResult().getFulfillment().getSpeech();
                        TTS(answer);



                    }
                }
            }.execute(aiRequest);


            //Call DTW
        }


    }
    public void startGestureRecognition(){

        if(!switchMode.isChecked())
        {
            //Start Recording in Training Mode

            String gesture = gestureName.getText().toString();
            String word = gestureWord.getText().toString();
            filename = "train" + "_" + gesture + word + "_" + getFileName();

        }
        else
        {
            //Start Recording in Test Mode
            filename = "test_" + getFileName();

        }
        isRecording = true;
        start.setText("Stop");
        Log.v("Data Logging", "Started Recording");

    }


    private Runnable audioRunnable = new Runnable() {
        @Override
        public void run() {
            //while (flag)
            {
                String toSpeak = msg;

                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                /*try {
                    String toSpeak = msg;
                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    HashMap<String,String> myHashRender = new HashMap();
                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,msg);
                    File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    //File newFile = new File(root.getAbsolutePath() + File.separator + "torqueDumps" + File.separator  + System.currentTimeMillis() + "-audio" + ".mp3");
                    int abc = t1.synthesizeToFile(msg,myHashRender,getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()+System.currentTimeMillis()+".mp3");
                    //int abc = t1.synthesizeToFile(msg,myHashRender,newFile.toString());
                    Log.i("TTS",getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
                    Thread.sleep(20000);
                    flag = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }
    };
    public void TTS(String arg)
    {
        msg = arg;
        Handler myHandler = new Handler();
        myHandler.postDelayed(audioRunnable,200);

    }

    private class AutomaticStop extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            Log.v("SensorService", "Data logged");
            start.performClick();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Log.v("SensorService", "Size of write buffer: " + writeBuffer.size());
            try {
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;

        }
    }


}

