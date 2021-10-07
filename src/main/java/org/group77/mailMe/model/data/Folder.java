package org.group77.mailMe.model.data;

import java.io.*;
import java.util.*;

/**
 * Class representing named folders containing emails.
 *
 * @author Alexey Ryabov, Martin
 */
public record Folder(
        String name,
        List<Email> emails
) implements Serializable {
}
