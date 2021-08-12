package com.example.wavemaker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class MainHandler {
    // this list is build up in the adapter
    public static ArrayList<MessageDataList> MESSAGE_QUEUE = new ArrayList<>();
    ConstraintLayout layout;
    static ImageView timerView;

    public MainHandler(ConstraintLayout layout) {
        this.layout = layout;
        timerView = layout.findViewById(R.id.box);
        // Listen for messages from socket thread
        MESSAGE_HANDLER();
    }

    private void MESSAGE_HANDLER() {
        // Recursive MainThread Looper
        timer(10, new Runnable() {
            @Override
            public void run() {
                // Exec first in queue, then remove from heap
                int i = 0;
                for (MessageDataList dataList : MESSAGE_QUEUE){
                    try {
                        MessageData data = dataList.list.get(0);
                        handleMsg(data);
                        dataList.list.remove(0);
                        i++;
                    } catch (Exception e) {}
                }
                MESSAGE_HANDLER();
            }
        });
    }

    public void handleMsg(MessageData data) {
        RecyclerViewAdapter.handleMsg(data);
    }

    public static void sendMessage(int progress, int position){
        MessageDataList dataList = MESSAGE_QUEUE.get(position);
        dataList.list.add(new MessageData(progress, position));
    }

    public static void timer(int time, final Runnable runnable){
        // Hacky timer OFF UI thread,
        // Works solid and easily
        // Where as sending android.os.message to ui thread can get lost
        // Arbitrarily change alpha to fully opaque
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(timerView, "alpha", 1);
        objectAnimator.setDuration(time);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                runnable.run();
            }
        });
    }
}
