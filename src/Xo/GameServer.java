package XOXO;

import java.io.*;
import java.net.*;

public class GameServer {
    private static final int PORT = 55555;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            // اتصال اللاعب X
            Socket playerX = serverSocket.accept();
            PrintWriter outX = new PrintWriter(playerX.getOutputStream(), true);
            BufferedReader inX = new BufferedReader(new InputStreamReader(playerX.getInputStream()));
            outX.println("X");

            // اتصال اللاعب O
            Socket playerO = serverSocket.accept();
            PrintWriter outO = new PrintWriter(playerO.getOutputStream(), true);
            BufferedReader inO = new BufferedReader(new InputStreamReader(playerO.getInputStream()));
            outO.println("O");

            // أرسل من سيبدأ أولًا بعد الإعادة
            outX.println("START");
            outO.println("WAIT");

            while (true) {
                // استقبال الحركات من اللاعب X وإرسالها إلى O
                String moveFromX = inX.readLine();
                if (moveFromX == null) break;
                outO.println(moveFromX);

                // إذا كانت الرسالة RESET من X، نقوم بإرسالها لـ O
                if (moveFromX.startsWith("RESET")) {
                    outO.println(moveFromX);
                    outX.println("START");  // تحديد أن X يبدأ بعد RESET
                    outO.println("WAIT");   // تأكيد أن O في الانتظار
                    continue;
                }

                // استقبال الحركات من اللاعب O وإرسالها إلى X
                String moveFromO = inO.readLine();
                if (moveFromO == null) break;
                outX.println(moveFromO);

                // إذا كانت الرسالة RESET من O، نقوم بإرسالها لـ X
                if (moveFromO.startsWith("RESET")) {
                    outX.println(moveFromO);
                    outX.println("START");  // تحديد أن X يبدأ بعد RESET
                    outO.println("WAIT");   // تأكيد أن O في الانتظار
                    continue;
                }
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
                System.out.println("Server closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
