// 1. Open a socket.
// 2. Open an input stream and output stream to the socket.
// 3. Read from and write to the stream according to the server's protocol.
// 4. Close the streams.
// 5. Close the socket.

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    public static ArrayList<ThingOfBid> thingOfBidsC = new ArrayList<>();
    static ThingOfBid[] thingOfBids  = new ThingOfBid[10];
    public static int thingofbid_num = 0;
    public static ArrayList<String> biding_information = new ArrayList<>();
    static HostPageGUI hostPageGUI = new HostPageGUI();

    public ClientHandler()
    {
        File thingofbidinfo;
        hostPageGUI.HostPageGUI_show();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        //int response =
                fileChooser.showOpenDialog(null);
        //if(response==JFileChooser.APPROVE_OPTION)
        //{
            thingofbidinfo = new File(fileChooser.getSelectedFile().getAbsolutePath());
       // }
        //File thingofbidinfo = new File("C:/Users/ASUS/IdeaProjects/IIT_Auction_1.0/src/thingofbidinfo.txt");

        try {
            Scanner filescanner = new Scanner(thingofbidinfo);
            while (filescanner.hasNext())
            {
                String name = filescanner.next();
                String description = filescanner.next();
                int minimum_prx = Integer.parseInt(filescanner.next());
                thingOfBids[thingofbid_num]= new ThingOfBid(name,description,minimum_prx);

                thingofbid_num++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;


    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // When a client connects their username is sent.
            this.clientUsername = bufferedReader.readLine();
            // Add the new client handler to the array so they can receive messages from others.
            clientHandlers.add(this);
            broadcastMessageToAll("SERVER: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            // Close everything more gracefully.
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    @Override
    public void run() {
        int current_thingofbid_num =0;
        String messageFromClient;

        while (socket.isConnected()) {
            try {

                messageFromClient = bufferedReader.readLine();
                System.out.println(messageFromClient);
                if((messageFromClient.charAt(0)=='d')&&(messageFromClient.charAt(1)=='b'))
                {
                    String tempmsg = messageFromClient;
                    String soldPrice = tempmsg.replaceAll("[^0-9]","");
                    thingOfBids[current_thingofbid_num].sold_price= Integer.parseInt(soldPrice);
                    thingOfBids[current_thingofbid_num].WhoSold= clientUsername;
                    thingOfBidsC.add(thingOfBids[current_thingofbid_num]);

                    String bid_Info_msg = soldPrice+" For player "+thingOfBids[current_thingofbid_num].Name+" by "+clientUsername;
                    biding_information.add(bid_Info_msg);

                    hostPageGUI.Update_notice_Info(bid_Info_msg);
                    hostPageGUI.HostPageGUI_show();

                    String new_player_info = thingOfBids[current_thingofbid_num].Name+"  "+thingOfBids[current_thingofbid_num].Description+"  "+String.valueOf(thingOfBids[current_thingofbid_num].minimum_price);
                    broadcastMessageToAll("np"+new_player_info);

                    hostPageGUI.Update_player_info(new_player_info);
                    hostPageGUI.HostPageGUI_show();

                    broadcastMessageToAll("bi"+String.valueOf(thingOfBids[current_thingofbid_num].sold_price));
                    current_thingofbid_num++;
                    if(current_thingofbid_num==thingofbid_num)
                    {
                        System.out.println(biding_information);
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }

                }
                if((messageFromClient.charAt(0)=='c')&&(messageFromClient.charAt(1)=='b'))
                {
                    hostPageGUI.Update_biding_Info(messageFromClient.substring(2));
                    hostPageGUI.HostPageGUI_show();

                    broadcastMessageExceptOne("cb");
                    thingOfBids[current_thingofbid_num].sold_price +=100;
                    broadcastMessageToAll("bi"+String.valueOf(thingOfBids[current_thingofbid_num].sold_price));
                }
                //broadcastMessageToAll(messageFromClient);
            } catch (IOException e) {
                // Close everything gracefully.
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessageToOne(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void broadcastMessageExceptOne(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void broadcastMessageToAll(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();

            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessageToAll("SERVER: " + clientUsername + " has left the Auction!");
    }


    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        new Report(thingOfBidsC);
        System.out.println(thingOfBidsC);

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
}