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

    int counter =0;
    public static int myAmount = 2000;
    String Startbid = new String("m1");
    JFrame client1stFrame = new JFrame();
    JPanel panel1stFrame = new JPanel();

    JProgressBar slideBar = new JProgressBar();
    JLabel bidThingInfo = new JLabel();
    JLabel myInfo = new JLabel();
    JLabel bidInfo = new JLabel();
    JButton buttonClaimbid = new JButton("Claim Bid");
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
    public void sendMsgOne(String msgToSend)
    {
        try {
            bufferedWriter.write(msgToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendMessage() {
        try {
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
                        msgFromGroupChat = (String)bufferedReader.readLine();
                        if(msgFromGroupChat.equals(Startbid))
                        {
                            clientPage();
                        }
                        if((msgFromGroupChat.charAt(0)=='n')&&(msgFromGroupChat.charAt(1)=='p'))
                        {
                            bidThingInfo.setText(msgFromGroupChat.substring(2));
                        }
                        if((msgFromGroupChat.charAt(0)=='b')&&(msgFromGroupChat.charAt(1)=='i'))
                        {
                            bidInfo.setText(msgFromGroupChat.substring(2));
                        }
                        if((msgFromGroupChat.charAt(0)=='c')&&(msgFromGroupChat.charAt(1)=='b'))
                        {
                            counter=0;
                            slider();
                        }
                        if((msgFromGroupChat.charAt(0)=='d')&&(msgFromGroupChat.charAt(1)=='b'))
                        {
                            sendMsgOne("done bid for this player");
                        }

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

    public void clientPage()
    {
       panel1stFrame.add(myInfo);
       panel1stFrame.add(bidThingInfo);
       panel1stFrame.add(bidInfo);
       panel1stFrame.add(buttonClaimbid);
       panel1stFrame.add(slideBar);

        client1stFrame.add(panel1stFrame);
        client1stFrame.setTitle("IIT Auction");
        client1stFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client1stFrame.setResizable(true);
        client1stFrame.setSize(500, 400);
        client1stFrame.getContentPane().setBackground(black);
        //client1stFrame.pack();
        client1stFrame.setVisible(true);

    }
    public void startClient(String username1) throws IOException {

    }
    // Run the program.
    public static void main(String[] args) throws IOException {

        // Get a username for the user and a socket connection.
        //Scanner scanner = new Scanner(System.in);
        //System.out.print("Enter your username for the group chat: ");
        //String username = scanner.nextLine();
        String username = JOptionPane.showInputDialog(null,"Enter the name of your team","Your team name here");
        // Create a socket to connect to the server.
        Socket socket = new Socket("localhost", 1234);

        // Pass the socket and give the client a username.
        Client client = new Client(socket, username);
        JOptionPane.showMessageDialog(null,"Please wait, the host will start auction shortly!","Information",JOptionPane.PLAIN_MESSAGE);

        client.sendMsgOne(username);

        // Infinite loop to read and send messages.
        client.listenForMessage();
        client.buttonClaimbid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMsgOne("cb");
                myAmount = myAmount -100;
                client.myInfo.setText(String.valueOf(client.myAmount));
                //client.sendMsgOne("cb");
            }
        });
        //client.getResponse();
        //client.sendMessage();
    }

    void slider()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (counter<=150)
                {
                    slideBar.setValue(counter);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    counter = counter+1;
                }
                sendMsgOne("db"+String.valueOf(myAmount));
            }
        }).start();

    }
    void getResponse()
    {
        while (socket.isConnected())
        {
            buttonClaimbid.addActionListener(this);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        /*if(e.getSource()==buttonClaimbid)
        {
            sendMsgOne("cb");
        }*/
    }
}
