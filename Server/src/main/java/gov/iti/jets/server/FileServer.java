package gov.iti.jets.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

    public static void Start() {
        System.out.println(FileServer.class.getResource("/screens"));
        try (ServerSocket s = new ServerSocket(3000)) {
            int i = 1;
            while (true) {
                Socket incoming = s.accept();
                // ThreadedFile.arr.add(incoming);
                // s.accept();
                System.out.println("Spawning " + i);
                Runnable r = new ThreadedFile(incoming);
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.start();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
        // finally {
        //     for (Socket s : ThreadedEchoHandler.arr) {
        //         try {
        //             s.close();
        //         } catch (IOException e) {
        //             // TODO Auto-generated catch block
        //             e.printStackTrace();
        //         }
        //     }
        // }
    }

}
