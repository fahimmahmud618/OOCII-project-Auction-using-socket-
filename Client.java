import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.awt.Color.black;

// A client sends messages to the server, the server spawns a thread to communicate with the client.
// Each communication with a client is added to an array list so any message sent gets sent to every other client
// by looping through it.

public class Client implements ActionListener {
    public Client()
    {

    }

    JFrame client1stFrame = new JFrame();
    JPanel panel1stFrame = new JPanel();
    JLabel label11stFrame = new JLabel();
    JButton button1stFrame = new JButton();
    JTextField textField1stFrame = new JTextField();

    // A client has a socket to connect to the server and a reader and writer to receive and send messages respectively.
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    boolean got_username = false;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            // Gracefully close everything.
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Sending a message isn't blocking and can be done without spawning a thread, unlike waiting for a message.
    public void sendMessage() {
        try {
            // Initially send the username of the client.
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            // Create a scanner for user input.
            Scanner scanner = new Scanner(System.in);
            // While there is still a connection with the server, continue to scan the terminal and then send the message.
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            // Gracefully close everything.
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Listening for a message is blocking so need a separate thread for that.
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                // While there is still a connection with the server, continue to listen for messages on a separate thread.
                while (socket.isConnected()) {
                    try {
                        // Get the messages sent from other users and print it to the console.
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        // Close everything gracefully.
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    // Helper method to close everything so you don't have to repeat yourself.
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        // Note you only need to close the outer wrapper as the underlying streams are closed when you close the wrapper.
        // Note you want to close the outermost wrapper so that everything gets flushed.
        // Note that closing a socket will also close the socket's InputStream and OutputStream.
        // Closing the input stream closes the socket. You need to use shutdownInput() on socket to just close the input stream.
        // Closing the socket will also close the socket's input stream and output stream.
        // Close the socket after closing the streams.
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientLoginPage()
    {
        textField1stFrame.setPreferredSize(new Dimension(250,40));
        panel1stFrame.add(label11stFrame);
        label11stFrame.setText("Enter the name of your team");
        panel1stFrame.add(textField1stFrame);

        button1stFrame.setText("Submit");
        panel1stFrame.add(button1stFrame);
        button1stFrame.addActionListener(this);

        client1stFrame.add(panel1stFrame);
        client1stFrame.setTitle("IIT Auction");
        client1stFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client1stFrame.setResizable(true);
        client1stFrame.setSize(500, 400);
        client1stFrame.getContentPane().setBackground(black);
        client1stFrame.pack();
        client1stFrame.setVisible(true);

        System.out.println("Username: "+username);

    }
    public void startClient(String username1) throws IOException {
        Socket socket = new Socket("localhost", 1234);

        // Pass the socket and give the client a username.
        Client client = new Client(socket, username1);
        // Infinite loop to read and send messages.
        client.listenForMessage();
        client.sendMessage();
    }
    // Run the program.
    public static void main(String[] args) throws IOException {

        Client firdt = new Client();
        firdt.clientLoginPage();


        // Get a username for the user and a socket connection.
        //Scanner scanner = new Scanner(System.in);
        //System.out.print("Enter your username for the group chat: ");
        //String username = scanner.nextLine();
        // Create a socket to connect to the server.

        //System.out.println(username1);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==button1stFrame)
        {
            username = textField1stFrame.getText();
            try {
                new Client().startClient(username);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
