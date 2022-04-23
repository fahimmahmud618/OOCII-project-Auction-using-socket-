import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.InvalidPropertiesFormatException;
import java.util.Scanner;

import static java.awt.Color.*;

public class Client {

    static int present_bid =0;
    static boolean press_bid_button = false;
    static int counter =0;
    public static int myAmount = 2000;
    String Startbid = new String("m1");
    JFrame client1stFrame = new JFrame();

    JProgressBar slideBar = new JProgressBar();
    JLabel bidThingInfo = new JLabel();
    JLabel myInfo = new JLabel();
    JLabel bidInfo = new JLabel();
    JButton buttonClaimbid = new JButton("Claim Bid");
    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();
    JPanel p3 = new JPanel();
    JPanel p4 = new JPanel();
    JPanel p5 = new JPanel();

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

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = (String)bufferedReader.readLine();
                        if(msgFromGroupChat.equals(Startbid))
                        {
                            clientPage();
                            counter = 0;
                            slider();
                        }
                        if((msgFromGroupChat.charAt(0)=='n')&&(msgFromGroupChat.charAt(1)=='p'))
                        {
                            String tempmsg = msgFromGroupChat;
                            bidThingInfo.setText("Player Info: "+msgFromGroupChat.substring(2));
                            present_bid = Integer.parseInt(tempmsg.replaceAll("[^0-9]",""));
                            press_bid_button = false;
                            counter=0;
                            //present_bid = 0;
                        }

                        if((msgFromGroupChat.charAt(0)=='b')&&(msgFromGroupChat.charAt(1)=='i'))
                        {
                            bidInfo.setText("Current Bid for the player: "+msgFromGroupChat.substring(2));
                            present_bid = Integer.parseInt(msgFromGroupChat.substring(2));
                            counter = 0;
                        }
                        if((msgFromGroupChat.charAt(0)=='c')&&(msgFromGroupChat.charAt(1)=='b'))
                        {
                            press_bid_button= false;
                            counter=0;
                            //slider();
                        }
                        /*if((msgFromGroupChat.charAt(0)=='d')&&(msgFromGroupChat.charAt(1)=='b'))
                        {
                            press_bid_button = false;
                            //sendMsgOne("done bid for this player");
                            counter=0;
                        }*/

                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        // Close everything gracefully.
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
        myInfo.setFont(new Font("Roboto",Font.PLAIN,18));
        bidInfo.setFont(new Font("Roboto",Font.PLAIN,18));
        bidThingInfo.setFont(new Font("Roboto",Font.PLAIN,18));
        bidInfo.setForeground(white);
        //myInfo.setSize(200,100);
        myInfo.setBounds(0,300,400,100);
        bidThingInfo.setBounds(0,0,400,100);
        bidInfo.setBounds(0,150,400,100);

        buttonClaimbid.setBounds(450,75,100,250);

        slideBar.setBounds(0,300,600,50);
        slideBar.setSize(600,50);
        slideBar.setStringPainted(true);

        p1.setBackground(red);
        p1.setBounds(0,0,600,50);
        p1.add(bidThingInfo);

        p2.setBackground(blue);
        p2.setBounds(0,75,300,50);
        p2.add(bidInfo);

        p3.setBackground(lightGray);
        p3.setBounds(300,75,300,50);
        p3.add(myInfo);
        myInfo.setText(username+" :: Remaining Amount : "+String.valueOf(myAmount));

        p4.setBackground(red);
        p4.setBounds(0,200,600,50);
        p4.add(buttonClaimbid);

        p5.setBackground(blue);
        p5.setBounds(0,300,600,50);
        p5.add(slideBar);

        client1stFrame.add(p1);
        client1stFrame.add(p2);
        client1stFrame.add(p3);
        client1stFrame.add(p4);
        client1stFrame.add(p5);

        client1stFrame.setTitle("IIT Auction");
        client1stFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client1stFrame.setLayout(null);
        client1stFrame.setSize(600, 400);
        client1stFrame.setVisible(true);

    }

    public static void main(String[] args) throws IOException {

        String username = JOptionPane.showInputDialog(null,"Enter the name of your team","Your team name here");
        String password = JOptionPane.showInputDialog(null,"Enter the password");
        if(!password.equals("iit123"))
        {
            throw new InvalidPropertiesFormatException("Passswoed is not correct");
        }
        else {
            Socket socket = new Socket("localhost", 1234);

            Client client = new Client(socket, username);
            JOptionPane.showMessageDialog(null, "Please wait, the host will start auction shortly!", "Information", JOptionPane.PLAIN_MESSAGE);

            client.sendMsgOne(username);

            client.listenForMessage();
            client.buttonClaimbid.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    client.sendMsgOne("cb" + username + " Placed bid at " + String.valueOf(present_bid));
                    press_bid_button = true;
                    System.out.println("Pressed bid and now " + press_bid_button);
                    counter = 0;
                }
            });
        }
    }

    void slider()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //counter = 0;
                    while ((counter <= 150)&&(counter>=0)) {
                        slideBar.setValue(counter);
                        try {

                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        counter = counter + 1;
                    }
                    if(counter==151)
                    {
                        System.out.println(username+":  counter in 151 and button "+press_bid_button);
                        if(press_bid_button)
                        {
                            sendMsgOne("db"+username+" "+String.valueOf(present_bid));
                            myAmount = myAmount -present_bid;
                            myInfo.setText(username+" :: Remaining Amount : "+String.valueOf(myAmount));
                            counter=0;
                        }

                    }

                }
            }
        }).start();

    }

}

