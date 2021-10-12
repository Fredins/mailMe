package org.group77.mailMe.model;

import org.group77.mailMe.model.data.Account;
import org.group77.mailMe.model.data.Email;
import org.group77.mailMe.model.data.Folder;
import org.group77.mailMe.services.emailServiceProvider.EmailServiceProvider;
import org.group77.mailMe.services.emailServiceProvider.EmailServiceProviderFactory;
import org.group77.mailMe.services.storage.Storage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Control {

    // services
    private final Storage storage;

    // model
    private Model model;

    public Control(Storage storage) {
        this.storage = storage;

        // retrieve data from storage and give to model
        Map<Account, List<Folder>> accountsData = new HashMap<>();
        for (Account account : storage.retrieveAccounts()) {
            List<Folder> folders = storage.retrieveFolders(account);
            accountsData.put(account,folders);

        }
        this.model = new Model(accountsData);

    }

    public void refresh() throws Exception {
        // 1) check that there is an active account and folders is not empty in model
        // 2a) if true:
        //      a) get ESP for model's activeAccount
        //      b) send activeAccount to refreshFromServer, will receive List<Emails> INBOX
        //      c) add the new emails to model's inbox folder (TODO: refreshFolder in model)
        //      d) add the new emails to storage
        //
        // 2b) if not true:
        //      a) throw exception that there is no active account

        if (model.getActiveAccount() != null && model.getActiveFolders() != null) {

            EmailServiceProvider esp = EmailServiceProviderFactory.getEmailServiceProvider(model.getActiveAccount().get());
            // receive new emails from server
            List<Email> newEmails = esp.refreshFromServer(model.getActiveAccount().get());

            // add new emails to inbox in model
            try {
                model.updateInbox(newEmails);
                storage.store(model.getActiveAccount().get(),model.getActiveFolders().get()); // replaces old inbox with new emails
            } catch (Exception e){
                throw new Exception("No folder named inbox");
            }

        } else {
            throw new Exception("No active account");
        }


    }

    public void addAccount(String emailAddress, String password) throws Exception {

        // 1) try to create account with addAccount method in model or use AccountFactory directly? (TODO: addAccount in model)
        // 2a) if successful:
        //      a) get ESP for added account
        //      b) esp.testConnection(account)
        //          if connection successful:
        //              i) store account (throws an exception?)
        //              ii) add account in model's accounts (TODO: method that adds account to accounts attribute)
        //          if connection not successful:
        //              i) throw authentication exception
        // 2b) if not successful:
        //      a) throw domain not supported exception

        Account tempAccount = AccountFactory.createAccount(emailAddress,password.toCharArray());

        if (tempAccount != null) {
            storage.store(tempAccount); // throws authentication exception
            model.addAccount(tempAccount); // will set tempAccount to activeAccount

        } else {
            throw new Exception("Domain is not supported");
        }

    }

    public void send(List<String> recipients, String subject, String content, List<File> attachments) throws Exception {
        // 1) check that there is an active account in model
        // 2a) if true:
        //      a) get active account ESP
        //      b) esp.sendEmail(recipients,subject,content,attachment)
        // 2b) if not true:
        //      a) throw exception: no active account
        if (model.getActiveAccount() != null) {

            EmailServiceProvider esp = EmailServiceProviderFactory.getEmailServiceProvider(model.getActiveAccount().get());
            esp.sendEmail(model.getActiveAccount().get(),recipients,subject,content,attachments);

        } else {
            throw new Exception("No active account");
        }
    }

    public void DeleteEmail() throws Exception {

        List<Folder> newFolders = storage.retrieveFolders(model.getActiveAccount().get());
        //Move the currently open email to the trash
        newFolders.get(4).emails().add(model.getReadingEmail().get()); //TODO Fix better index if we want to add more folders in the future (from 4 to compare name to "Trash" somehow..)
        //Remove currently open email from the activeFolder
        newFolders.get(newFolders.indexOf(model.getActiveFolder().get())).emails().remove(model.getReadingEmail().get());
        storage.store(model.getActiveAccount().get(), newFolders);
        model.getActiveFolders().replaceAll(newFolders);
        refresh();
    }

    /**
     * Used when deleting emails from the trash. Will remove it from all inboxes and will not be able to recover it
     *
     * @throws Exception
     * @author David Zamanian
     */

    public void PermDeleteEmail() throws Exception {
        List<Folder> newFolders = storage.retrieveFolders(model.getActiveAccount().get());
        newFolders.get(newFolders.indexOf(model.getActiveFolder().get())).emails().remove(model.getReadingEmail().get());
        storage.store(model.getActiveAccount().get(), newFolders);
        model.getActiveFolders().replaceAll(newFolders);
        refresh();
    }

    /**
     * Moves tha email to the desired folder and deletes it from the activeFolder. Choose where to move in the comboBox in the readingView.
     *
     * @param folder The folder that was selected in the "Move" comboBox in readingView
     * @throws Exception
     * @author David Zamanian
     */

    public void MoveEmail(Folder folder) throws Exception {
        List<Folder> newFolders = storage.retrieveFolders(model.getActiveAccount().get());
        //Move the currently open email to the chosen folder in the comboBox
        newFolders.get(newFolders.indexOf(folder)).emails().add(model.getReadingEmail().get());
        //Delete the currently open email from the activeFolder
        newFolders.get(newFolders.indexOf(model.getActiveFolder().get())).emails().remove(model.getReadingEmail().get());
        storage.store(model.getActiveAccount().get(), newFolders);
        model.getActiveFolders().replaceAll(newFolders);
        refresh();
    }


    /*
    public void deleteEmail(Email emailToBeDeleted) {
        // use method in model like: model.deleteEmail(emailToBeDeleted)
        // model returns new folder structure, store new copy in storage
    }
    */



    public Subject<Account> getActiveAccount() {
        return model.getActiveAccount();
    }

    public SubjectList<Account> getAccounts() {
        return model.getAccounts();
    }

    public void setActiveAccount(Account account) {
        model.setActiveAccount(account);
    }

    public SubjectList<Folder> getActiveFolders() {
        return model.getActiveFolders();
    }

    // view subjects

    public Subject<Folder> getActiveFolder() {
        return model.getActiveFolder();
    }

    public Subject<Email> getReadingEmail() {
        return model.getReadingEmail();
    }

    public SubjectList<Email> getVisibleEmails() {
        return model.getVisibleEmails();
    }

    public void setActiveFolder(Folder activeFolder) {
        model.setActiveFolder(activeFolder);
    }

    public void setReadingEmail(Email readingEmail) {
        model.setReadingEmail(readingEmail);
    }

    public void setVisibleEmails(List<Email> visibleEmails) {
        model.setVisibleEmails(visibleEmails);
    }

}
