package gov.iti.jets.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServer {
    public static volatile boolean running = true;
    private static ServerSocket s;
    public static void Start() {
            ExecutorService executorService =
    Executors. newFixedThreadPool(20);
        // System.out.println(FileServer.class.getResource("/screens"));
        try {
            s = new ServerSocket(3331);
            int i = 1;
            while (running) {
                Socket incoming = s.accept();
                // ThreadedFile.arr.add(incoming);
                // s.accept();
                System.out.println("Spawning " + i);
                Runnable r = new ThreadedFile(incoming);
                executorService.submit(r);
                // Thread t = new Thread(r);
                // t.setDaemon(true);
                // t.start();
                i++;
            }
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
        } 
        // finally {
        //     for (Socket s : ThreadedEchoHandler.arr) {
        //         try {
        //             s.close();
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }
    }
    public static void Stop() {
        running = false;
        try {
            if (s != null) {
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
