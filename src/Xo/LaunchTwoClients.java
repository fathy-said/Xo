package XOXO;

public class LaunchTwoClients {
    public void start() {
        // بدء الخادم
        new Thread(() -> GameServer.main(null)).start();

        try {
            Thread.sleep(1000); // الانتظار قليلاً حتى يبدأ الخادم
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // بدء اللاعب X أولاً
        new Thread(() -> {
            new GameGUI("Player X").setVisible(true);
        }).start();

        try {
            Thread.sleep(1000); // تأخير فتح نافذة اللاعب O لضمان أن X هو الأول
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ثم بدء اللاعب O
        new Thread(() -> {
            new GameGUI("Player O").setVisible(true);
        }).start();
    }
}
