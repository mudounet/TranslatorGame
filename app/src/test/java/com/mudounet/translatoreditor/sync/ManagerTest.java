package com.mudounet.translatoreditor.sync;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by guillaume on 04/01/2018.
 */
public class ManagerTest {

    private static final org.slf4j.Logger Logger = LoggerFactory
            .getLogger(ManagerTest.class);

    String remoteURI;
    CredentialsProvider credentials;
    PersonIdent ident = new PersonIdent("Beautiful NAME", "test@toto.com");

    @Rule
    public TemporaryFolder repositoryFolder= new TemporaryFolder();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "gitremote.properties";
            input =ManagerTest.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                Logger.error("Sorry, unable to find " + filename);
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out
            Logger.debug(prop.getProperty("remoteURI"));
            Logger.debug(prop.getProperty("username"));

            remoteURI = prop.getProperty("remoteURI");
            credentials = new UsernamePasswordCredentialsProvider(prop.getProperty("username"), prop.getProperty("password"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void initAlreadySetRepositoryWithoutRemote() throws Exception {
        Logger.debug("");

        Git git = Git.init().setDirectory(repositoryFolder.getRoot()).call();

        File file = _setTextInFile("testfile", "test");
        git.add().addFilepattern(file.getPath()).call();
        git.commit().setAuthor(ident).setMessage("Test commit").call();

        ObjectId sha1 = git.getRepository().resolve(Constants.HEAD);

        git.close();

        Manager manager = new Manager(repositoryFolder.getRoot(), ident);

        assertEquals(sha1, manager.getSha1());
    }

    @Test
    public void initAlreadySetRepository() throws Exception {
        Git git = Git.init().setDirectory(repositoryFolder.getRoot()).call();

        File file = _setTextInFile("testfile", "test");
        git.add().addFilepattern(file.getPath()).call();
        git.commit().setAuthor(ident).setMessage("Test commit").call();

        ObjectId sha1 = git.getRepository().resolve(Constants.HEAD);

        git.close();

        Manager manager = new Manager(repositoryFolder.getRoot(), ident, remoteURI, credentials);

        assertEquals(sha1, manager.getSha1());

        manager.pull();
    }

    @Test
    public void pull() throws Exception {
        Manager manager = new Manager(repositoryFolder.getRoot(), ident, remoteURI, credentials);

        manager.pull();
    }

    @Test
    public void pullWithoutRemote() throws Exception {
        Manager manager = new Manager(repositoryFolder.getRoot(), ident);

        expectedEx.expect(SyncException.class);
        expectedEx.expectMessage("Remote is not configured");

        manager.pull();
    }

    @Test
    public void push() throws Exception {
        Manager manager = new Manager(repositoryFolder.getRoot(), ident, remoteURI, credentials);

        manager.push();
    }

    @Test
    public void pushWithoutRemote() throws Exception {
        Manager manager = new Manager(repositoryFolder.getRoot(), ident);

        expectedEx.expect(SyncException.class);
        expectedEx.expectMessage("Remote is not configured");

        manager.push();
    }

    @Test
    public void cancel() throws Exception {
        Manager manager = new Manager(repositoryFolder.getRoot(), ident);

        manager.cancel();
    }

    @Test
    public void commit() throws Exception {
        Logger.debug("Test");
        Manager manager = new Manager(repositoryFolder.getRoot(), ident);

        Assert.assertFalse(manager.isDirty());
        Assert.assertFalse(_isFileExists("testfile1.txt"));

        File file1 = _setTextInFile("testfile1.txt", "");

        Assert.assertTrue(_isFileExists("testfile1.txt"));
        Assert.assertTrue(manager.isDirty());

        manager.commit("test commit");

        Assert.assertFalse(manager.isDirty());

        _setTextInFile("testfile1.txt", "new text");

        Assert.assertTrue(manager.isDirty());

        manager.commit("test commit");

        Assert.assertFalse(manager.isDirty());
    }

    @Test
    public void getFiles() throws Exception {
        Manager manager = new Manager(repositoryFolder.getRoot(), ident);

        assertEquals(0, manager.getFiles().length);

        _setTextInFile("testfile1.txt", "new text");

        assertEquals(1, manager.getFiles().length);
    }

    private boolean _isFileExists(String filename) {
        File file = new File(repositoryFolder.getRoot(), filename);
        if (file.exists() && !file.isDirectory()) return true;
        return false;
    }

    private File _setTextInFile(String filename, String text) throws IOException {
        File file = new File(repositoryFolder.getRoot(), filename);

        if (text == null) {
            file.delete();
            return null;
        }

        if (! file.exists() && !file.isDirectory()) file = repositoryFolder.newFile(filename);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(text);

        //Close writer
        writer.close();
        return file;
    }
}