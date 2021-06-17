package androiddeveloper.amrrabbie.buseettask.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androiddeveloper.amrrabbie.buseettask.R;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.setFinishOnTouchOutside(false);
    }
}