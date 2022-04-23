public class ThingOfBid implements Comparable<ThingOfBid>{
    String Name = "NO name";
    String Description = "No info";
    int minimum_price =100;
    int sold_price = 0;
    String WhoSold;

    public ThingOfBid(String Name, String Description, int minimum_price)
    {
        this.Name = Name;
        this.Description = Description;
        this.minimum_price = minimum_price;
    }
    @Override
    public String toString() {
        return "ThingOfBid{" +
                "Name='" + Name + '\'' +
                ", Description='" + Description + '\'' +
                ", minimum_price=" + minimum_price +
                ", sold_price=" + sold_price +
                ", WhoSold='" + WhoSold + '\'' +
                '}';
    }

    @Override
    public int compareTo(ThingOfBid that) {
        return this.sold_price-that.sold_price;
    }
}
