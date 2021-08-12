package com.example.wavemaker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wavemaker.databinding.AudioLayoutBinding;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    MainActivity mainActivity;
    public static final String RECORD = "Record";
    public static final String RECORDING = "Recording";
    public static final String STOP = "Stop";
    public static final String PLAY = "Play";
    public static final int RED = Color.RED;
    public static final int GREEN = Color.GREEN;
    public static final int FREQUENCY = 0;
    public static final int AMP = 1;
    int viewNumber = MainActivity.maxPosition+1;
    int holdnumber = 0;
    public static ArrayList<Boolean> playing = new ArrayList<>();
    public static ArrayList<SeekBar> freqencySeekBars = new ArrayList<>();
    ArrayList<Float> currentFrequencys = new ArrayList<>();
    ArrayList<Long> currentTimes = new ArrayList<>();
    ArrayList<RecordData> recordData = new ArrayList<>();
    ArrayList<Boolean> recording = new ArrayList<>();
    ArrayList<Integer> progresses = new ArrayList<>();

    public RecyclerViewAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        setUpLists();
    }

    public void setUpLists() {
        for (int i=holdnumber; i<viewNumber; i++){
            MainHandler.MESSAGE_QUEUE.add(i, new MessageDataList());
            currentFrequencys.add(i, Frequencies.getFrequency(1));
            currentTimes.add(i, System.currentTimeMillis());
            progresses.add(i, 0);
            recordData.add(i, new RecordData());
            freqencySeekBars.add(i, null);
            recording.add(i, false);
            playing.add(i, false);
        }
        holdnumber = viewNumber;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AudioLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.audio_layout,
                parent,
                false);
        return new RecyclerViewAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {
        final TextView recordButton = holder.binding.recyclerviewRecordButton;
        if (position < viewNumber) {
            setSeekBarChangeListener(holder, position, FREQUENCY);
            setSeekBarChangeListener(holder, position, AMP);
            setWaveFormButton(holder, position);
            recordButton.setOnClickListener(recordButtonListener(recordButton, position));

        } else {
            holder.binding.recyclerviewSeekbar.setVisibility(View.GONE);
            holder.binding.recyclerviewSeekbarAmp.setVisibility(View.GONE);
            holder.binding.recyclerviewRecordButton.setVisibility(View.GONE);
            final TextView textView = holder.binding.recyclerviewTextview;
            textView.setText("Add");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.addEngineIndex();
                    viewNumber++;
                    setUpLists();
                    setSeekBarChangeListener(holder, position, FREQUENCY);
                    setSeekBarChangeListener(holder, position, AMP);
                    recordButton.setVisibility(View.VISIBLE);
                    recordButton.setOnClickListener(recordButtonListener(recordButton, position));
                    textView.setOnClickListener(getWaveFormClick(textView, position));
                    textView.setText(R.string.square_wave);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return viewNumber + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AudioLayoutBinding binding;
        public ViewHolder(AudioLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static void handleMsg(MessageData data){
        SeekBar seekBar = freqencySeekBars.get(data.getPosition());
        seekBar.setProgress(data.getProgress());
    }

    public void playRecoring(final RecordData data, final int position) {
        Frequencies.playTones(mainActivity, data, position, 0, 0, new Runnable() {
            @Override
            public void run() {
                playRecoring(data, position);
            }
        });
    }

    public void setSeekBarChangeListener(RecyclerViewAdapter.ViewHolder holder, int position, int callback){
        SeekBar seekBar = null;
        if (callback == FREQUENCY) {
            seekBar = holder.binding.recyclerviewSeekbar;
            freqencySeekBars.set(position, seekBar);
        } else if (callback == AMP) {
            seekBar = holder.binding.recyclerviewSeekbarAmp;
        }
        seekBar.setVisibility(View.VISIBLE);
        seekBar.setOnSeekBarChangeListener(getSeekBarChangeListener(position, callback));
    }

    public void setWaveFormButton(RecyclerViewAdapter.ViewHolder holder, int position){
        TextView textView = holder.binding.recyclerviewTextview;
        textView.setVisibility(View.VISIBLE);
        textView.setOnClickListener(getWaveFormClick(textView, position));
    }

    public void setFrequency(final int position, final int progress){
        Thread thread = new Thread(){
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                if (recording.get(position)) {
                    RecordData rData = recordData.get(position);
                    rData.list.add(new RData(currentFrequencys.get(position), currentTime - currentTimes.get(position), progresses.get(position)));
                    recordData.add(position, rData);
                }
                progresses.set(position, progress);
                currentFrequencys.set(position, Frequencies.getFrequency(progress));
                currentTimes.set(position, currentTime);
                mainActivity.setFrequency(currentFrequencys.get(position), position);
            }
        };
        thread.start();
    }

    public void setAmp(final int position, final int progress){
        Thread thread = new Thread() {
            @Override
            public void run() {
                float amp = (float) progress;
                amp = amp/100f;
                mainActivity.setAmpIndex(position, amp);
                mainActivity.restartEngineIndex(currentFrequencys.get(position), position);
                if (progress <= 1) {
                    mainActivity.setToneOn(false, position);
                } else {
                    mainActivity.setToneOn(true, position);
                }
            }
        };
        thread.start();
    }

    public View.OnClickListener recordButtonListener(final TextView recordButton, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordButton.getText().equals("Record")) {
                    currentTimes.set(position, System.currentTimeMillis());
                    recording.set(position, true);
                    setRecordButtonText(recordButton, RECORDING, RED);
                } else if (recordButton.getText().equals("Recording")){
                    recording.set(position, false);
                    setRecordButtonText(recordButton, PLAY, GREEN);
                    // Disable buttons until done playing
                    // After playing button will go back to red and "record"
                } else if (recordButton.getText().equals("Play")){
                    try {
                        RecordData data = recordData.get(position);
                        playing.set(position, true);
                        playRecoring(data, position);
                        setRecordButtonText(recordButton, STOP, RED);
                    } catch (Exception e) {
                        e.printStackTrace();
                        setRecordButtonText(recordButton, RECORD, RED);
                    }
                } else if (recordButton.getText().equals("Stop")){
                    playing.set(position, false);
                    recordData.add(position, new RecordData());
                    setRecordButtonText(recordButton, RECORD, RED);
                }
            }
        };
    }

    public void setRecordButtonText(TextView textView, String text, int color){
        textView.setText(text);
        textView.setBackgroundColor(color);
    }

    public SeekBar.OnSeekBarChangeListener getSeekBarChangeListener(final int position, final int callback) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (callback == FREQUENCY) {
                        setFrequency(position, progress);
                    } else if (callback == AMP) {
                        setAmp(position, progress);
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    public View.OnClickListener getWaveFormClick(final TextView textView, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sineOn = mainActivity.getSine(position);
                boolean isOnOrOff = !sineOn;
                mainActivity.setSineOn(isOnOrOff, position);
                mainActivity.restartEngineIndex(currentFrequencys.get(position), position);
                if (isOnOrOff) {
                    textView.setText(R.string.sine_wave);
                } else {
                    textView.setText(R.string.square_wave);
                }
            }
        };
    }

}
