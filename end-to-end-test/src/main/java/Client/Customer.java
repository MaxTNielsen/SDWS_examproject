package Client;

public class Customer extends User implements Client {
    public Customer(String ID)
    {
        super(ID);
    }

    public Customer(String cprNo, String ID) {
        super(cprNo, ID);
    }

    public Customer(String cprNo, String firstName, String lastName, String ID) {
        super(cprNo, firstName, lastName, ID);
    }
}
