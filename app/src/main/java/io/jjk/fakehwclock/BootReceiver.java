package io.jjk.fakehwclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class BootReceiver extends BroadcastReceiver {
    /**
     * @see BroadcastReceiver#onReceive(Context, Intent)
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Log.i("fakehwclock", "Grab hakehwclock time!");


            Log.i("fakehwclock", "Starting fakehwclock service!");
            Intent fakeHWClock = new Intent(context, FakeHWClockService.class);
            context.startService(fakeHWClock);
        }
    }

    public void getFakeHWClockTime() {
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File(sdCard, "hwclock");

//        Log.i("TEST", file.toString());
        try {
            Log.i("TEST", getStringFromFile(file.getPath()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws IOException {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
}
