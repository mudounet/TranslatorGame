package com.mudounet.translatoreditor.sync;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by guillaume on 04/01/2018.
 */

public class Manager {

    private static final org.slf4j.Logger Logger = LoggerFactory
            .getLogger(Manager.class);

    private CredentialsProvider credentials;
    private Git git;

    public Manager(File directory, CredentialsProvider credentials) {
    }

    public boolean pull() {
        Logger.debug("Retrieving data to remote");
        return false;
    }

    public boolean push() {
        Logger.debug("Sending data to remote");
        return false;
    }

    public boolean cancel() {
        Logger.debug("Reverting back changes");
        return false;
    }

    public boolean commit() {
        Logger.debug("Storing data into repository");
        return false;
    }

    public boolean isDirty() {
        Logger.debug("Checking is repository is dirty");
        return false;
    }

    public File getFiles() {
        Logger.debug("Returning all files");
        return null;
    }


}
