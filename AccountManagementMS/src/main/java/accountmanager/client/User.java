package accountmanager.client;

public class User {
    public String cprNo;
    public String firstName;
    public String lastName;
    public String ID;

    public User()
    {
        
    }

    public User(String cprNo, String firstName, String lastName, String ID) {
        this.cprNo = cprNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ID = ID;
    }

    public User(String cprNo, String ID) {
        this.cprNo = cprNo;
        this.ID = ID;
    }

    public User(String ID)
    {
        this.ID = ID;
    }

}
