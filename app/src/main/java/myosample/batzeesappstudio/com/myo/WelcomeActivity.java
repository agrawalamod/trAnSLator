package myosample.batzeesappstudio.com.myo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dtw.FastDtwTest;
import com.thalmic.myo.scanner.ScanActivity;

import java.io.File;

public class WelcomeActivity extends AppCompatActivity {



    Button connect;
    Button use;
    Button gesture;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        connect = (Button) findViewById(R.id.connect);
        use = (Button) findViewById(R.id.use);
        gesture = (Button) findViewById(R.id.gesture);


        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                onScanActionSelected();
            }
        });
        use.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(WelcomeActivity.this, HomeScreen.class);
                i.putExtra("train", "0");
                startActivity(i);

            }

        });
        gesture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(WelcomeActivity.this, HomeScreen.class);
                i.putExtra("train", "1");
                startActivity(i);


            }

        });
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

}
