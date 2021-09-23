package org.group77.mejl.model;
import java.io.IOException;
import java.util.*;

public class ApplicationManager {

    AccountHandler accountHandler;
    EmailServiceProviderFactory espFactory;

    public ApplicationManager() {}

    /**
     * @author Elin Hagman
     *
     * Creates an Account and tries to store it.
     * Only stores the account if it connects to the server and it is not already stored.
     *
     * @param emailAddress the Account's emailAddress
     * @param password the Account's password
     *
     * @return boolean, true if account was successfully stored,
     * false if account could not be stored.
    * */


    public boolean addAccount(String emailAddress, String password) throws Exception {

        Account account = accountHandler.createAccount(emailAddress,password);
        EmailServiceProviderStrategy espStrategy = espFactory.getEmailServiceProvider(account);

        if (espStrategy.testConnection(account)) {

            return accountHandler.storeAccount(account);

        } else {
            return false;
        }

    }


    public boolean setActiveAccount(String emailAddress) {
        return false;
    }

    public List<String> getEmailAddresses() {
        return null;
    }


    /**
     * @author David Zamanian
     *
     * calls getEmails with foldername in accountHandler
     *
     * @param folderName the name of the desired email folder
     * @return
     * @throws OSNotFoundException If the operating system is not found
     * @throws IOException If there are any problems when locating the file
     * @throws ClassNotFoundException Of the classes required is not on the classpath?
     */

    public List<Email> getEmails(String folderName) throws OSNotFoundException, IOException, ClassNotFoundException {
        accountHandler.getEmails(folderName);
        return null;
    }

    public boolean sendEmail(List<String> recipients, String subject, String content) {
        return false;
    }

    /**
     * @author Elin Hagman
     *
     * Refreshes the currently active account´s folders from server and stores them.
     * If the refreshed fodlers cannot be stored, an exception will
     * be thrown instead of returning the folders.
     *
     * @return List of folders that has been updated from server
     * @throws Exception if the folders updated from server cannot be stored
     */
    public List<Folder> refreshFromServer() throws Exception {

        Account account = accountHandler.getActiveAccount();
        EmailServiceProviderStrategy espStrategy = espFactory.getEmailServiceProvider(account);

        List<Folder> folders = espStrategy.refreshFromServer(account);
        if (accountHandler.storeFolders(folders)) {
            return folders;
        } else {
            throw new Exception("Could not store folders");
        }

    }


}
