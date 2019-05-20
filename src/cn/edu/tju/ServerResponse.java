package cn.edu.tju;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerResponse extends Thread {
    private static final short BUFFER_SIZE = 1024;
    private Socket client;
    private JTextArea jTextArea;

    /**
     * constructor method here
     */

    @Contract(pure = true)
    ServerResponse(Socket client, int count, JTextArea jTextArea) {
        this.client = client;
        this.jTextArea = jTextArea;
    }

    /*
     * run function of the Class
     * Dealing with the FileStream
     * 1. FileInputStream -> buffer -> DataOutputStream
     * 2. buffer here is: byte[] buffer = new byte[1024]
     * 3. A read function with FileInputStream,
     *    put the file into buffer in Bytes Form
     * 4. A write function with DateOutputStream
     */
    public void run() {
        String CRLF = "\r\n";
        FileInputStream FIS = null;
        BufferedReader BR = null;
        DataOutputStream DOS = null;

        try {
            // use client socket to get the InputStream
            BR = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // settle the output Stream
            DOS = new DataOutputStream(client.getOutputStream());
            // read the first row of http request
            String headRequest = BR.readLine();
            // debug, print the headline of request
            System.out.println();

            File file = returnFileByGet(headRequest);
            StringTokenizer stk = new StringTokenizer(headRequest);
            if (headRequest.contains("HEAD")) {
                DOS.writeBytes("HTTP/1.0 200 OK" + CRLF);
                DOS.writeBytes("MIME_Version 1.0");
                DOS.writeBytes(CRLF);
                jTextArea.insert(" REQUEST: " + headRequest + "\n", 0);
                jTextArea.insert(" USER: " + client.getInetAddress().toString().substring(1), 0);
                jTextArea.insert(" Port: " + client.getPort() + '\n', 0);
            }
            /*
             * Send HEAD of HTTP first
             * Use a blank line to devide head and body
             * The content of HEAD
             * 1. HTTP 1.1 200 OK
             * 2. MIME_Version: 1.0
             * 3. content_length
             * 4. content_type
             */
            if (file != null) {
                // HEAD
                try {
                    DOS.writeBytes("HTTP/1.0 200 OK" + CRLF);
                    DOS.writeBytes("MIME_Version 1.0");
                    if (file.getName().endsWith("png")) {
                        DOS.writeBytes("Content-Type: image/png" + CRLF);
                    }
                    if (file.getName().endsWith("jpg")) {
                        DOS.writeBytes("Content-Type: image/jpg" + CRLF);
                    }
                    DOS.writeBytes(CRLF);
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            }
            // BODY

            try {
                FIS = new FileInputStream(file);
            } catch (FileNotFoundException fnE) {
                fnE.printStackTrace();
                System.out.println("Cannot Find File");
            }

            if (file.exists()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int haveRead;
                try {
                    assert FIS != null;
                    while ((haveRead = FIS.read(buffer)) != -1) {
                        //if read some bytes, put it into output stream
                        DOS.write(buffer, 0, haveRead);
                    }
                } catch (Exception npE) {
                    npE.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (BR != null) {
                    BR.close();
                }
                if (FIS != null) {
                    FIS.close();
                }
                if (DOS != null) {
                    DOS.close();
                }
            } catch (IOException ioE2) {
                ioE2.printStackTrace();
            }

        }
    }

    @Nullable
    private File returnFileByGet(String headRequest) {
        File file;
        StringTokenizer headTk = null;
        if (headRequest != null) {
            headTk = new StringTokenizer(headRequest);
        }

        // the form of GET request is:
        // GET path/file name HTTP/1.1
        // execute the following codes when the request is "GET"
        assert headTk != null;
        if (headTk.nextToken().equals("GET")) {
            String reqFileName = headTk.nextToken();
            if (reqFileName.equals("/")) {
                reqFileName = "/home.html";
            }

            //get the request file, must return something
            file = new File(reqFileName.substring(1));
            // show the form of GET request
            // report Count of Request
//          jTextArea.insert(" COUNT: " + String.valueOf(count) + '\n', 0);
            // show the message of REQUEST info
            jTextArea.insert(" REQUEST: " + headRequest + "\n", 0);
            jTextArea.insert(" USER: " + client.getInetAddress().toString().substring(1), 0);
            jTextArea.insert(" Port: " + client.getPort() + '\n', 0);

            if (file.exists()) { // return files if existing
                jTextArea.insert(" \n FOUND" + reqFileName.substring(1) + ", Jumping to the PAGE\n", 0);
            } else { // return "error.html" if not existing
                jTextArea.insert(" \n NOT FOUND" + reqFileName.substring(1) + ", JUMPING to the ERROR PAGE\n", 0);
                file = new File("error.html");
            }
            return file;
        } else if (headTk.nextToken().equals("HEAD")) {
            String reqFileName = headTk.nextToken();
            if (reqFileName.equals("/")) {
                reqFileName = "/home.html";
            }
            jTextArea.insert(" REQUEST: " + headRequest + "\n", 0);
            jTextArea.insert(" USER: " + client.getInetAddress().toString().substring(1), 0);
            jTextArea.insert(" Port: " + client.getPort() + '\n', 0);
            return null;
        }
        return null;
    }
}