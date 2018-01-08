package com.mudounet.translatoreditor.sync;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by guillaume on 04/01/2018.
 */

public class Manager {

    private static final org.slf4j.Logger Logger = LoggerFactory
            .getLogger(Manager.class);

    private Git git;
    private boolean remoteConfigured;
    private PersonIdent ident;
    private CredentialsProvider credentials;
    private String currentBranch;

    public Manager(File directory, PersonIdent ident) throws IOException, GitAPIException, URISyntaxException {
       this(directory, ident, null, null);
    }

    public Manager(File directory, PersonIdent ident, String remoteURL, CredentialsProvider credentials ) throws IOException, GitAPIException, URISyntaxException {

        this.ident = ident;
        Logger.debug("Current directory is "+ directory);

        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.addCeilingDirectory( directory );
        repositoryBuilder.findGitDir( directory );
        if( repositoryBuilder.getGitDir() != null ) {
            Logger.debug("GIT filesystem detected");
            this.git = new Git( repositoryBuilder.build() );
        }
        else {
            Logger.debug("Repository is not defined yet");
            Logger.debug("Initializing local repository");
            this.git = Git.init().setDirectory(directory).call();
        }

        if(remoteURL != null) {
            _setRemote(remoteURL, credentials);
            this.git.pull();
            Logger.debug("Initializing local repository from remote+pull");
        }
    }

    private boolean _isRemoteAvailable() {
        return !this.git.getRepository().getRemoteNames().isEmpty();
    }

    private void _setRemote(String remoteURL, CredentialsProvider credentials) throws IOException, URISyntaxException {
        if(credentials != null) this.credentials = credentials;


        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "origin", "url", new URIish(remoteURL).toString());
        config.save();
    }

    public boolean cancel() {
        Logger.debug("Reverting back changes");
        throw new UnsupportedOperationException();
    }

    public ObjectId getSha1() throws IOException {
        return git.getRepository().resolve(Constants.HEAD);
    }

    public ObjectId commit(String message) throws IOException, GitAPIException {
        Logger.debug("Storing data into repository");

        git.add().addFilepattern(".").call();
        git.commit().setAuthor(ident).setMessage(message).call();

        ObjectId objectId = getSha1();
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

    public boolean pull() throws SyncException, GitAPIException {
        Logger.debug("Retrieving data to remote");
        if(!_isRemoteAvailable()) throw new SyncException("Remote is not configured");
        return this.git.pull().setStrategy(MergeStrategy.THEIRS).call().isSuccessful();
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
