package org.group77.mejl.model;
import java.util.*;
import javax.mail.*;


public abstract class EmailServiceProviderStrategy {

    String hostIn;
    String hostOut;
    String protocolIn;
    String protocolOut;
    int portIn;
    int portOut;


    /**
     * @author Martin
     * @param account is a account
     * @return List<Folder> is a list of folders
     */
    public List<Folder> refreshFromServer(Account account) throws MessagingException{
        return parse(
                connectStore(account)
                        .getDefaultFolder()
                        .list("*")
        );
    }

    /**
     * @author Martin
     * @param account is a account
     * @return boolean if the connection was successfull
     */
    public boolean testConnection(Account account) throws MessagingException{
        try{
            connectStore(account);
        }catch(MessagingException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @author Martin
     * @param account is a account
     * @return Store is a list of folders
     */
    private Store connectStore(Account account) throws MessagingException {
        Session session = Session.getDefaultInstance((new Properties()), null);
        Store store = session.getStore(protocolIn);
        store.connect(
                hostIn,
                portIn,
                account.getEmailAddress(),
                account.getPassword()
        );
        return store;
    }

    public boolean sendEmail(Account from, List<String> recepients, String subject, String content) {
        return false;
    }

    protected abstract List<Folder> parse(javax.mail.Folder[] folders);

}
