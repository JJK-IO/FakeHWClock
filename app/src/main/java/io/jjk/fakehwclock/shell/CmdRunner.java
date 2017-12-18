package io.jjk.fakehwclock.shell;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CmdRunner {

    public static String execute(String cmd) {
        StringBuilder result = new StringBuilder();
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd);
            Log.d("CmdRunner", "Waiting...");
            p.waitFor();
            Log.d("SSHTest", "Done");

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
