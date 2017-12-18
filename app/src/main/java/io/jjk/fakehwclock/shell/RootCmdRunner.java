package io.jjk.fakehwclock.shell;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class RootCmdRunner {

    private static String TAG = "RootCmdRunner";

    public static void execute(String[] cmds) throws IOException, InterruptedException {
        Log.d(TAG, "Running: " + "su");
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        for (String tmpCmd : cmds) {
            Log.d(TAG, "Running: " + tmpCmd);
            os.writeBytes(tmpCmd + "\n");
        }
        os.writeBytes("exit\n");
        os.flush();
        p.waitFor();
    }

    public static String execute(String cmd) throws IOException, InterruptedException {
        StringBuilder result = new StringBuilder();
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        os.writeBytes(cmd + "\n");
        os.writeBytes("exit\n");
        os.flush();
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        Log.d(TAG, "cmd result: " + result);
        return result.toString();
    }

}
