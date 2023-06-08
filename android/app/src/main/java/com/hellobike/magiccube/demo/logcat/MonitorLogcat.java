package com.hellobike.magiccube.demo.logcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MonitorLogcat {

    private static MonitorLogcat sLogcatRunner;

    private ShellProcessThread mLogcatThread;

    public static MonitorLogcat getInstance() {
        if (sLogcatRunner == null) {
            synchronized (MonitorLogcat.class) {
                if (sLogcatRunner == null) {
                    sLogcatRunner = new MonitorLogcat();
                }
            }
        }
        return sLogcatRunner;
    }

    public void start(LogcatOutputCallback logcatOutputCallback) {
        doStop();
        mLogcatThread = new ShellProcessThread();
        mLogcatThread.setOutputCallback(logcatOutputCallback);
        mLogcatThread.start();

    }

    public void stop() {
        doStop();
    }

    private MonitorLogcat() {
    }

    private void doStop() {
        try {
            if (mLogcatThread != null && mLogcatThread.isAlive()) {
                mLogcatThread.setOutputCallback(null);
                mLogcatThread.stopReader();
                mLogcatThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ShellProcessThread extends Thread {

        private volatile boolean readerLogging = true;
        private LogcatOutputCallback mOutputCallback;

        void setOutputCallback(LogcatOutputCallback outputCallback) {
            mOutputCallback = outputCallback;
        }

        @Override
        public void run() {
            Process exec = null;
            InputStream inputStream = null;
            BufferedReader reader = null;

            try {
//                exec = Runtime.getRuntime().exec("logcat -v threadtime");
                exec = Runtime.getRuntime().exec("logcat -s HBAndroidDSLSDK");
                inputStream = exec.getInputStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                while (readerLogging) {
                    String line = reader.readLine();
                    if (mOutputCallback != null && line != null) {
                        mOutputCallback.onReaderLine(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (exec != null) {
                    exec.destroy();
                }
            }
        }

        void stopReader() {
            readerLogging = false;
        }
    }

    public interface LogcatOutputCallback {

        void onReaderLine(String line);
    }
}
