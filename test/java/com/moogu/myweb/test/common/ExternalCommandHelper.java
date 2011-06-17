package com.moogu.myweb.test.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExternalCommandHelper {

    public static int run(File dir, String command, String... failureStrings) {
        int err = -1;
        command = "cmd /c cd " + dir + " && " + command;
        try {
            Process proc = Runtime.getRuntime().exec(command);
            StreamPumper inputPumper = new StreamPumper(proc.getInputStream(), failureStrings);
            StreamPumper errorPumper = new StreamPumper(proc.getErrorStream(), failureStrings);
            inputPumper.start();
            errorPumper.start();
            proc.getOutputStream();
            proc.waitFor();
            inputPumper.join();
            errorPumper.join();
            proc.destroy();
            err = proc.exitValue();
            if (err != 0 || inputPumper.hasFailure() || errorPumper.hasFailure()) {
                throw new RuntimeException("Error while executing external command");
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error exec: " + command, ioe);
        } catch (InterruptedException ex) {
        }
        return err;
    }

    private static class StreamPumper extends Thread {

        private BufferedReader din;

        private boolean endOfStream;

        private int sleeptime;

        private String[] failureStrings;

        private boolean hasFailure;

        public void pumpStream() throws IOException {
            if (!endOfStream) {
                String line = din.readLine();
                if (line != null) {
                    System.out.println(line);
                    for (String failureString : failureStrings) {
                        if (line.contains(failureString)) {
                            hasFailure = true;
                        }
                    }
                } else {
                    endOfStream = true;
                }
            }
        }

        public boolean hasFailure() {
            return hasFailure;
        }

        @Override
		public void run() {
            try {
                try {
                    while (!endOfStream) {
                        pumpStream();
                        Thread.sleep(sleeptime);
                    }
                } catch (InterruptedException ie) {
                }
                din.close();
            } catch (IOException ioe) {
            }
        }

        public StreamPumper(InputStream is, String[] failureStrings) {
            this.endOfStream = false;
            this.sleeptime = 5;
            this.din = new BufferedReader(new InputStreamReader(is));
            this.failureStrings = failureStrings;
        }
    }

}
