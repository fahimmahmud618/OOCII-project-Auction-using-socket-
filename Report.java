import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Report implements ActionListener {
    ArrayList<ThingOfBid> thingOfBids = new ArrayList<>();
    JFrame reportpage = new JFrame();
    JRadioButton price_wise = new JRadioButton();
    JRadioButton who_sold_wise = new JRadioButton();
    JLabel label = new JLabel();

    public Report(ArrayList<ThingOfBid> thingOfBids)
    {
        this.thingOfBids = thingOfBids;
        label.setText("How do you want to see the reports? ");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(price_wise);
        buttonGroup.add(who_sold_wise);

        reportpage.add(label);
        reportpage.add(price_wise);
        reportpage.add(who_sold_wise);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==price_wise)
        {
            Collections.sort(thingOfBids);
            for(ThingOfBid thing : thingOfBids)
                System.out.println(thing);
        }
        if(e.getSource()==who_sold_wise)
        {
            Collections.sort(thingOfBids, new Comparator<ThingOfBid>() {
                @Override
                public int compare(ThingOfBid o1, ThingOfBid o2) {
                    return o1.WhoSold.compareTo(o2.WhoSold);
                }
            });
            for(ThingOfBid thing : thingOfBids)
                System.out.println(thing);
        }
    }
}
