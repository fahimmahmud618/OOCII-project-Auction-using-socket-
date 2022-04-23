import javax.swing.*;
import java.awt.*;

public class HostPageGUI {
    JFrame hostPage = new JFrame();
    JPanel p1h = new JPanel();
    JPanel p2h = new JPanel();
    JPanel p3h = new JPanel();
    JLabel playerInfo_host = new JLabel();
    JLabel Notice_host = new JLabel();
    JLabel bidInfo_host = new JLabel();

    String pi,ni,bi;

    public void HostPageGUI_show()
    {
        playerInfo_host.setText(pi);
        Notice_host.setText(ni);
        bidInfo_host.setText(bi);

        //playerInfo_host.setForeground(Color.white);
        //bidInfo_host.setForeground(Color.black);
        //Notice_host.setForeground(Color.white);

        p1h.setBackground(Color.yellow);
        p1h.setBounds(0,0,400,50);
        p1h.add(playerInfo_host);

        p2h.setBackground(Color.lightGray);
        p2h.setBounds(0,60,400,50);
        p2h.add(bidInfo_host);

        p3h.setBackground(Color.lightGray);
        p3h.setBounds(0,120,400,50);
        p3h.add(Notice_host);

        hostPage.add(p1h);
        hostPage.add(p2h);
        hostPage.add(p3h);

        hostPage.setTitle("IIT Auction :: Host");
        hostPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hostPage.setSize(400,300);
        hostPage.setVisible(true);
    }
    public void Update_player_info(String msg)
    {
        pi = msg;
    }

    public void Update_biding_Info(String msg)
    {
        bi = msg;
    }

    public void Update_notice_Info(String msg)
    {
        ni = msg;
    }
}
