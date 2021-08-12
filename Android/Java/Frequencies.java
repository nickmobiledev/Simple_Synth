package com.example.wavemaker;

import java.util.ArrayList;

public class Frequencies {

    public static ArrayList<String> frequencies = new ArrayList<>();
    MainActivity mainActivity;

    public Frequencies(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static float getFrequency(int position) {
        String frequency = frequencies.get(position);
        return Float.parseFloat(frequency);
    }

    public static void playTone(float tone, int position, MainActivity mainActivity){
        mainActivity.setFrequency(tone, position);
    }

    public static void playTones(final MainActivity mainActivity, final RecordData recordData, final int position, final int index, final int pauseTime, final Runnable runnable){
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (index < recordData.list.size()) {
                    // play the tone
                    RData rdata = recordData.list.get(index);
                    MainHandler.sendMessage(rdata.progress, position);
                    playTone(rdata.frequency, position, mainActivity);
                    try {
                        Thread.sleep(Math.abs(rdata.time));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // recursive call to play the next tone
                    if (RecyclerViewAdapter.playing.get(position)) {
                        playTones(mainActivity, recordData, position, index + 1, pauseTime, runnable);
                    }
                } else {
                    mainActivity.setToneOn(false, position);
                    try {
                        Thread.sleep(pauseTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mainActivity.setToneOn(true, position);
                    runnable.run();
                }
            }
        };
        thread.start();
    }


    static {
        frequencies.add( "1");
        frequencies.add( "16.35");
        frequencies.add( "17.32");
        frequencies.add( "18.35");
        frequencies.add( "19.45");
        frequencies.add("20.60");
        frequencies.add("21.83");
        frequencies.add( "23.12");
        frequencies.add("24.50");
        frequencies.add( "25.96");
        frequencies.add("27.50");
        frequencies.add( "29.14");
        frequencies.add("30.87");
        frequencies.add("32.70");
        frequencies.add( "34.65");
        frequencies.add("36.71");
        frequencies.add( "38.89");
        frequencies.add("41.20");
        frequencies.add("43.65");
        frequencies.add( "46.25");
        frequencies.add("49.00");
        frequencies.add( "51.91");
        frequencies.add("55.00");
        frequencies.add( "58.27");
        frequencies.add("61.74");
        frequencies.add("65.41");
        frequencies.add( "69.30");
        frequencies.add("73.42");
        frequencies.add( "77.78");
        frequencies.add("82.41");
        frequencies.add("87.31");
        frequencies.add( "92.50");
        frequencies.add("98.00");
        frequencies.add( "103.83");
        frequencies.add("110.00");
        frequencies.add( "116.54");
        frequencies.add("123.47");
        frequencies.add("130.81");
        frequencies.add( "138.59");
        frequencies.add("146.83");
        frequencies.add( "155.56");
        frequencies.add("164.81");
        frequencies.add("174.61");
        frequencies.add( "185.00");
        frequencies.add("196.00");
        frequencies.add( "207.65");
        frequencies.add("220.00");
        frequencies.add( "233.08");
        frequencies.add("246.94");
        frequencies.add("261.63");
        frequencies.add( "277.18");
        frequencies.add("293.66");
        frequencies.add( "311.13");
        frequencies.add("329.63");
        frequencies.add("349.23");
        frequencies.add( "369.99");
        frequencies.add("392.00");
        frequencies.add( "415.30");
        frequencies.add("440.00");
        frequencies.add( "466.16");
        frequencies.add("493.88");
        frequencies.add("523.25");
        frequencies.add( "554.37");
        frequencies.add("587.33");
        frequencies.add( "622.25");
        frequencies.add("659.25");
        frequencies.add("698.46");
        frequencies.add( "739.99");
        frequencies.add("783.99");
        frequencies.add( "830.61");
        frequencies.add("880.00");
        frequencies.add( "932.33");
        frequencies.add("987.77");
        frequencies.add("1046.50");
        frequencies.add( "1108.73");
        frequencies.add("1174.66");
        frequencies.add( "1244.51");
        frequencies.add("1318.51");
        frequencies.add("1396.91");
        frequencies.add( "1479.98");
        frequencies.add("1567.98");
        frequencies.add( "1661.22");
        frequencies.add("1760.00");
        frequencies.add( "1864.66");
        frequencies.add("1975.53");
        frequencies.add("2093.00");
        frequencies.add( "2217.46");
        frequencies.add("2349.32");
        frequencies.add( "2489.02");
        frequencies.add("2637.02");
        frequencies.add("2793.83");
        frequencies.add( "2959.96");
        frequencies.add("3135.96");
        frequencies.add( "3322.44");
        frequencies.add("3520.00");
        frequencies.add( "3729.31");
        frequencies.add("3951.07");
        frequencies.add("4186.01");
        frequencies.add( "4434.92");
        frequencies.add("4698.63");
        frequencies.add( "4978.03");
        frequencies.add("5274.04");
        frequencies.add("5587.65");
        frequencies.add( "5919.91");
        frequencies.add("6271.93");
        frequencies.add( "6644.88");
        frequencies.add("7040.00");
        frequencies.add( "7458.62");
        frequencies.add("7902.13");
    }

}
