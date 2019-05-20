package cn.edu.tju;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShowTime extends Label{
    @Override
    public void paint(Graphics graph){
        super.paint(graph);
    }

    public void repaint() {
        super.repaint();
    }

    public void setText(String text){
        super.setText(text);
    }

    String getTime(){
        Calendar calen = Calendar.getInstance();
        // here we only need Hour, Minute and Second
        return (new SimpleDateFormat("HH:mm:ss")).format(calen.getTime());
    }

    // a new Thread object to update Time
    ShowTime(){
        // create a new SimpleDateFormat and transform date into it
        Runnable runn = () -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException inE) {
                    inE.printStackTrace();
                }
                Calendar cale = Calendar.getInstance();
                // create a new SimpleDateFormat and transform date into it
                String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(cale.getTime());
                setText(date);
            }
        };
        Thread thr = new Thread(runn);
        thr.start();
    }

}
