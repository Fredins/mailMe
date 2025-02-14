package org.group77.mailMe.model;

/**
 * @author Martin Fredin.
 * Class for creating concrete account instances.
 */
public class AccountFactory {

    /**
     * Creates a concrete account instance.
     * Sets ServerProvider depending on the suffix of the email address.
     * * "@gmail.com" gets associated with ServerProvider.GMAIL.
     * * "@hotmail.com", "@outlook.com" with ServerProvider.MICROSOFT.
     *
     * @param emailAddress - email address of the created account
     * @param password     - password of the created account
     * @return An account instance with the given attributes.
     * @author Martin Fredin
     * @author Hampus Jernkrook
     * @author Elin Hagman
     */
    public static Account createAccount(String emailAddress, char[] password) throws EmailDomainNotSupportedException {
        if (emailAddress.contains("@gmail.com")) {
            return new Account(emailAddress, password, ServerProvider.GMAIL);
        } else if (emailAddress.contains("@hotmail.com")
                || emailAddress.contains("@outlook.com")) {
            return new Account(emailAddress, password, ServerProvider.MICROSOFT);
        } else {
            throw new EmailDomainNotSupportedException();
        }
    }
}
