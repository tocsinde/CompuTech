package kickstart.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.salespointframework.useraccount.UserAccount;

/**
 * Created by Stephan on 03.11.2015.
 */
public class Customer {

    private @Id @GeneratedValue long id;

    private String address;
    private String firstname;
    private String lastname;

    private String password;

    private String mail;
    private String phone;


}
