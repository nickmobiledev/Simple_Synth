package com.example.wavemaker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    private native void startEngine(float frequency, int position);
    private native void stopEngine(int position);
    private native void toneOn(boolean setToneOnorOf, int position);
    private native void sineOn(boolean setWaveForm, int position);
    private native boolean getSineOn(int position);
    private native void setAmp(int position, float amp);
    private native void updateFrequency(float frequency, int position);
    private native void addEngine();
    static int maxPosition = 0;
    public static boolean RECORDING = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConstraintLayout layout = findViewById(R.id.mainactivity_layout);
        new Frequencies(this);
        startEngineIndex(0, maxPosition);
        RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment(this);
        new MainHandler(layout);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainactivity_layout, recyclerViewFragment);
        fragmentTransaction.commit();

    }

    public void setToneOn(Boolean bool, int position) {
        toneOn(bool, position);
    }

    public void setFrequency(float frequency, int position){
        updateFrequency(frequency, position);
    }

    public void setSineOn(boolean onOrOff, int position){
        sineOn(onOrOff, position);
    }

    public void startEngineIndex(float frequency, int position) {
        startEngine(frequency, position);
        toneOn(true, position);
    }

    public void restartEngineIndex(float frequency, int position) {
        stopEngineIndex(position);
        startEngineIndex(frequency, position);
    }

    public void setAmpIndex(int position, float amp){
        setAmp(position, amp);
    }

    public boolean getSine(int position){
        return getSineOn(position);
    }

    public void addEngineIndex() {
        addEngine();
        maxPosition++;
        startEngine(0, maxPosition);
        toneOn(true, maxPosition);
    }

    public void stopEngineIndex(int position) {
        toneOn(false, position);
        stopEngine(position);
    }

    @Override
    public void onDestroy() {
        //TODO: Stop all engines
        super.onDestroy();
    }
}
