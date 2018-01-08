package com.mudounet.translatoreditor.sync;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Created by guillaume on 04/01/2018.
 */

public class Manager {

    private static final org.slf4j.Logger Logger = LoggerFactory
            .getLogger(Manager.class);

    private Git git;
    private boolean remoteConfigured;
    private PersonIdent ident;
    private String currentBranch;

    public Manager(File directory, PersonIdent ident) throws IOException, GitAPIException {
       this(directory, ident, null, null);
    }

    public Manager(File directory, PersonIdent ident, String remoteURL, CredentialsProvider credentials ) throws IOException, GitAPIException {

        this.ident = ident;
        if (RepositoryCache.FileKey.isGitRepository(directory, FS.DETECTED)) {

            Logger.debug("GIT filesystem detected");

            Git git = Git.open(directory);

            for (Ref ref : git.getRepository().getAllRefs().values()) {
                if (ref.getObjectId() == null)
                    continue;
                Logger.debug("One GIT reference found");
                this.git = git;
                return;
            }
        }

        Logger.debug("Repository is not defined yet");
        if(remoteURL != null) {
            CloneCommand command = Git.cloneRepository().setDirectory(directory).setURI(remoteURL);
            if (credentials != null) command.setCredentialsProvider(credentials);
            this.git = command.call();
            this.remoteConfigured = true;
            Logger.debug("Initializing local repository from clone");
        }
        else {
            Logger.debug("Initializing local repository");
            this.git = Git.init().setDirectory(directory).call();
        }
    }

    private boolean _isRemoteAvailable() {
        return !this.git.getRepository().getRemoteNames().isEmpty();
    }

    public boolean cancel() {
        Logger.debug("Reverting back changes");
        throw new UnsupportedOperationException();
    }

    public ObjectId commit(String message) throws IOException, GitAPIException {
        Logger.debug("Storing data into repository");

        git.add().addFilepattern(".").call();
        git.commit().setAuthor(ident).setMessage(message).call();

        ObjectId objectId = git.getRepository().resolve(Constants.HEAD);
        Logger.debug("HEAD is now at " + objectId.getName());
        return objectId;
    }

    public boolean isDirty() throws GitAPIException, IOException {
        Logger.debug("Checking is repository is dirty");
        Status status = git.status().call();
        Logger.debug("Clean ? " + status.isClean());
        Logger.debug("Uncommitted Changes ? " + status.hasUncommittedChanges());
        return  status.hasUncommittedChanges() || !status.isClean();
    }

    public File[] getFiles() {
        Logger.debug("Returning all files");
        return this.git.getRepository().getWorkTree().listFiles(new GitFileFilter());
    }

    public boolean pull() throws SyncException {
        Logger.debug("Retrieving data to remote");
        if(!_isRemoteAvailable()) throw new SyncException("Remote is not configured");
        throw new UnsupportedOperationException();
    }

    public boolean push() throws SyncException {
        Logger.debug("Sending data to remote");
        if (!_isRemoteAvailable()) throw new SyncException("Remote is not configured");
        throw new UnsupportedOperationException();
    }

    public boolean syncNeeded() throws SyncException {
        if(!_isRemoteAvailable()) throw new SyncException("Remote is not configured");
        throw new UnsupportedOperationException();
    }

    private class GitFileFilter implements FileFilter {
        private final String[] nokFilenames = new String[] {".git", ".gitignore"};

        public boolean accept(File file) {
            for (String filename : nokFilenames) {
                if (file.getName().equals(filename))
                    return false;
            }
            return true;
        }
    }
}
