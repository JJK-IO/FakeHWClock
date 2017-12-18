package io.jjk.fakehwclock;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import io.jjk.fakehwclock.shell.RootCmdRunner;

public class FakeHWClockService extends Service {
    private String TAG = "FakeHWClockService";
    private int secondsToUpdate = 60;
    Handler mHandler;

    public FakeHWClockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* Set an runnable to save the current clock time to fake hardware clock time only if the
            software clock is newer than the fake hardware clock time. */
        mHandler = new Handler();
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                Calendar currentTime = Calendar.getInstance();
                File sdCard = Environment.getExternalStorageDirectory();
                File file = new File(sdCard, "hwclock");
                if (!fakeHWClockNewer(file)) {
                    try {
                        FileWriter f = new FileWriter(file);
                        Log.v(TAG, "Saving time: " + currentTime.getTimeInMillis());
                        f.write("" + currentTime.getTimeInMillis());
                        f.flush();
                        f.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.postDelayed(this, secondsToUpdate * 1000);
            }
        };
        mHandler.post(mRunnable);

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File file = new File(sdCard, "hwclock");

            /* Only update software clock if the fake hardware clock has a newer time.
                IE. Hasn't/can't pull date from ntp service. */
            if (fakeHWClockNewer(file)) {
                // Get fake hardware clock as Calendar object
                String unixTime = getStringFromFile(file.getPath()).trim();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.valueOf(unixTime));

                // Get the fake hardware clock in required format.
                String year = String.valueOf(cal.get(Calendar.YEAR));
                String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
                String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                String minute = String.valueOf(cal.get(Calendar.MINUTE));
                String second = String.valueOf(cal.get(Calendar.SECOND));
                String dateString = year + month + day + "." + hour + minute + second;

                // Set the software clock to fake hardware clock time.
                String command = "date -s " + dateString;
                Log.e(TAG, command);
                String result = RootCmdRunner.execute(command);
                Log.e("RootCmdRunner", result);

                String actionString = "io.jjk.fakehwclock.CLOCK_UPDATED";
                sendBroadcast(new Intent(actionString));
            } else {
                Log.e(TAG, "System time currently more update to date, no need to use FakeHWClock.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e(TAG, "There was an issue parsing the unix time from hwclock file.");
        }
    }

    public static boolean fakeHWClockNewer(File f) {
        try {
            String unixTime = getStringFromFile(f.getPath()).trim();
            Calendar currentTime = Calendar.getInstance();
            Calendar hwClock = Calendar.getInstance();
            hwClock.setTimeInMillis(Long.valueOf(unixTime));

            return hwClock.getTimeInMillis() > currentTime.getTimeInMillis();
        } catch (IOException e) {
            return false;
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
