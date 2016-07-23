package myosample.batzeesappstudio.com.myo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

public class MainActivity extends Activity {

    private String TAG = "MyO Test";
    Context context;
    private DeviceListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        requestLocationAccessPermission(context);
        Log.v("MainActivity", "Location approved.");

        createHub();
        scanFordevice();
        setLockPolicy();
        ontSendData();

    }
    public void requestLocationAccessPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Location Provider is not enable please do so
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.d("MapActivity:", "Permission Status: " + (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) + "");
            Log.d("MapActivity:", "Location Provider is not enable please do so ");
            return;
        }
    }

    private void createHub() {
        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e(TAG, "Could not initialize the Hub.");
            finish();
            return;
        }
    }

    private void scanFordevice() {
        Intent intent = new Intent(context, ScanActivity.class);
        context.startActivity(intent);
    }

    private void setLockPolicy() {
        Hub.getInstance().setLockingPolicy(Hub.LockingPolicy.NONE);
    }

    private void createAndAddListner() {

        mListener = new AbstractDeviceListener() {
            @Override
            public void onConnect(Myo myo, long timestamp) {
                Log.v("MainActivity", "Myo Connected");
                Toast.makeText(context, "Myo Connected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnect(Myo myo, long timestamp) {
                Toast.makeText(context, "Myo Disconnected!", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "Myo Disconnected");
            }

            @Override
            public void onPose(Myo myo, long timestamp, Pose pose) {
                switch (pose) {
                    case REST:
                        Toast.makeText(context, "REST", Toast.LENGTH_SHORT).show();
                        break;
                    case FIST:
                        Toast.makeText(context, "FIST", Toast.LENGTH_SHORT).show();
                        break;
                    case WAVE_IN:
                        Toast.makeText(context, "WAVE_IN", Toast.LENGTH_SHORT).show();
                        break;
                    case WAVE_OUT:
                        Toast.makeText(context, "WAVE_OUT", Toast.LENGTH_SHORT).show();
                        break;
                    case FINGERS_SPREAD:
                        Toast.makeText(context, "FINGERS_SPREAD", Toast.LENGTH_SHORT).show();
                        break;
                    case DOUBLE_TAP:
                        Toast.makeText(context, "DOUBLE_TAP", Toast.LENGTH_SHORT).show();
                        break;
                    case UNKNOWN:
                        Toast.makeText(context, "UNKNOWN", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        Hub.getInstance().addListener(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createAndAddListner();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Hub.getInstance().removeListener(mListener);
    }

    private void ontSendData() {
        if (Hub.getInstance().isSendingUsageData()) {
            Hub.getInstance().setSendUsageData(false);
        }
    }
}
