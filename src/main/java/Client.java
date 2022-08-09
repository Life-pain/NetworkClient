import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private Socket socket;
    private FileWriter writerToLog;
    private final String logPath = "./src/main/resources/file.log";
    private final String settingsPath = "./src/main/resources/settings.txt";
    private String userName;
    private SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public Client() {
        try {
            writerToLog = new FileWriter(logPath, true);
            writeToLog("Подключение к чату");
            socket = new Socket("127.0.0.1", getPort());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            scanner = new Scanner(System.in);

            new Thread(null, new ReaderMsg(), "Reader").start();
            userName = scanner.nextLine();
            out.println(userName);
            writeToLog("Установлено имя: " + userName);
            new Thread(null, new SendMsg(), "Sender").start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void close() {
        try {
            in.close();
            out.close();
            scanner.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort(){
        int result = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(settingsPath));)
        {
            result = Integer.parseInt(reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private class ReaderMsg implements Runnable {
        private FileWriter writerToLogForReader;
        @Override
        public void run() {
            String msg;
            try {
                writerToLogForReader = new FileWriter(logPath, true);
                while (true) {
                    msg = in.readLine();
                    if (msg.equals("exit")){
                        close();
                        break;
                    }
                    System.out.println(msg);
                    if (msg.equals("Введите имя:")) continue;
                    writerToLogForReader.write(formatter.format(new Date(System.currentTimeMillis()))
                            + " " + userName + ": " + msg + "\n");
                    writerToLogForReader.flush();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendMsg implements Runnable {
        @Override
        public void run() {
            System.out.println("Вы в чате! Введите сообщение, для завершения введите \"exit\": ");
            String line;
            while (true) {
                line = scanner.nextLine();

                out.println(line);
                if (line.equals("exit")){
                    writeToLog("Вы вышли из чата\n");
                    break;
                }
                writeToLog("Вы: " + line);
            }
        }
    }

    private void writeToLog(String line){
        try {
            writerToLog.write( formatter.format(new Date(System.currentTimeMillis())) + " " + line + "\n");
            writerToLog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}