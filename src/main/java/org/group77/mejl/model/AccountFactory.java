package org.group77.mejl.model;

public class AccountFactory {

    public Account createAccount(String emailAddress, String password){
        Account a;
        if (emailAddress.contains("@gmail.com")){
           a = new Account() 
        }

        return null;
    }
    
    private Account createGmailAccount(String emailAddress, String password) {
        return null;
    }
    
    private Account createMicrosoftAccount(String emailAddress, String password) {
        return null;
    }

}
