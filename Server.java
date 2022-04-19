import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static java.awt.Color.black;

public class Server implements ActionListener {

    int team_count =0;
    ArrayList<Socket> sockets = new ArrayList<>();
    boolean start_bid = false;
    String label1Txt = "";

    private final ServerSocket serverSocket;

    JFrame server1stframe = new JFrame();
    JPanel panel = new JPanel();
    JLabel label1 = new JLabel();
    JButton button = new JButton();

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        button.setText("Start Bid");
        panel.add(label1);
        panel.add(button);
        server1stframe.add(panel);

        server1stframe.add(panel);
        server1stframe.setTitle("IIT Auction");
        server1stframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server1stframe.setResizable(true);
        server1stframe.setSize(500, 400);
        server1stframe.getContentPane().setBackground(black);
        server1stframe.setVisible(true);

        try {
            button.addActionListener(this);
            // Listen for connections (clients to connect) on port 1234.
            while (!serverSocket.isClosed()) {
                // Will be closed in the Client Handler.
                Socket socket = serverSocket.accept();

                team_count++;
                label1Txt = String.valueOf(team_count)+" Team Leader joined the Auction!";
                label1.setText(label1Txt);
                //System.out.println("A new client has connected!");
                sockets.add(socket);
                //ClientHandler clientHandler = new ClientHandler(socket);
               // Thread thread = new Thread(clientHandler);
                // The start method begins the execution of a thread.
                // When you call start() the run method is called.
                // The operating system schedules the threads.
               // thread.start();
                if(start_bid)
                    break;
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void Start_bid()
    {
        for(Socket allsocket: sockets)
        {
            ClientHandler clientHandler = new ClientHandler(allsocket);
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    // Close the server socket gracefully.
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Run the program.
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==button)
        {
            Start_bid();
        }
    }
}
