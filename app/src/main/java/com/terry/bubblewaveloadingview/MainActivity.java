package com.terry.bubblewaveloadingview;

import android.app.Activity;
import android.os.Bundle;

import com.terry.bubblewaveloadingview.view.BubbleWaveLoadingView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BubbleWaveLoadingView bubbleWaveLoadingView = findViewById(R.id.bubblewaveloadingview);
        bubbleWaveLoadingView.setProgress(80);
    }
}
