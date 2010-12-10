
package br.boirque.vocabuilder.view;

import javax.microedition.lcdui.*;
import java.io.*;
import java.io.InputStream;
//import java.io.OutputStream;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.io.file.FileConnection;

/**
 *
 * This class has been taken from http://j2mesamples.blogspot.com and adopted for vocabuilder
 *
 * @author administrator
 */
public class FileSelector extends List implements CommandListener, FileSystemListener {

    Vocabuilder fileSelection;
    private Display display;
    // define the file separator
    private final static String FILE_SEPARATOR = (System.getProperty("file.separator") != null) ? System.getProperty("file.separator") : "/";

    private Command cmdOpen = new Command("Open", Command.OK, 1);
    private Command cmdClose = new Command("Close", Command.SCREEN, 2);
    private Command cmdBack = new Command("Back", Command.BACK, 1);

    private Vector rootsList = new Vector();
    private FileConnection currentRoot = null;
    private FileConnection fconn = null;
    private Image imgFolder = null;
    private Image imgText = null;
    private static final int CHUNK_SIZE = 1024;

    FileSelector(Vocabuilder fileSelection) {
        super("File Browser", List.IMPLICIT);
        this.fileSelection = fileSelection;
        
        while ( this.size() > 0) this.delete(0);

        addCommand(cmdOpen);
        addCommand(cmdClose);
        addCommand(cmdBack);
        setSelectCommand(cmdOpen);
        setCommandListener(this);
        FileSystemRegistry.addFileSystemListener(FileSelector.this);
        execute();
    }

    public void execute() {
        String initDir = System.getProperty("fileconn.dir");
        
        try {
            imgFolder = Image.createImage("/Folder.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            imgText = Image.createImage("/Text.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        loadRoots();
        if (initDir != null) {
            try {
                currentRoot = (FileConnection) Connector.open(initDir, Connector.READ);
                displayCurrentRoot();
            } catch (Exception e) {
                displayAllRoots();
            }
        } else {
            displayAllRoots();
        }
    }

    private void loadRoots() {
        if (!rootsList.isEmpty()) {
            rootsList.removeAllElements();
        }
        try {
            Enumeration roots = FileSystemRegistry.listRoots();
            while (roots.hasMoreElements()) {
                rootsList.addElement(FILE_SEPARATOR + (String) roots.nextElement());
            }
        } catch (Throwable e) {
        }
    }

    private void displayCurrentRoot() {
        try {
            setTitle(currentRoot.getURL());

            while ( this.size() > 0) this.delete(0);

            //append(upper_dir, null);
            

            Enumeration listOfDirs = currentRoot.list("*", false);
            while (listOfDirs.hasMoreElements()) {
                String currentDir = (String) listOfDirs.nextElement();
                if (currentDir.endsWith(FILE_SEPARATOR)) {
                    append(currentDir, imgFolder);
                } else if(currentDir.endsWith(".txt"))  {
                    append(currentDir, imgText);
                }
            }
            if (this.size() > 0) this.setSelectedIndex(0, true);

            /*Enumeration listOfFiles = currentRoot.list("*.png",false);
            while(listOfFiles.hasMoreElements()) {
            String currentFile=(String) listOfFiles.nextElement();
            if(currentFile.endsWith(FILE_SEPARATOR)) {
            append(currentFile,null);
            }
            else {
            append(currentFile,null);
            }
            }*/
        } catch (IOException e) {
        } catch (SecurityException e) {
        }
    }

    private void displayAllRoots() {
        setTitle("[Roots]");

        while ( this.size() > 0) this.delete(0);

        Enumeration roots = rootsList.elements();
        while (roots.hasMoreElements()) {
            String root = (String) roots.nextElement();
            append(root, imgFolder);
        }
        currentRoot = null;
    }

    private void openSelected() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedFile = getString(selectedIndex);
            if (selectedFile.endsWith(FILE_SEPARATOR)) {
                try {
                    if (currentRoot == null) {
                        currentRoot = (FileConnection) Connector.open("file://" + selectedFile, Connector.READ);
                    } else {
                        currentRoot.setFileConnection(selectedFile);
                    }
                    displayCurrentRoot();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                } catch (SecurityException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String url = currentRoot.getURL() + selectedFile;
                fileSelection.displaySetOfCards(url);

            }
        }
    }

    public void stop() {


        if (currentRoot != null) {
            try {
                currentRoot.close();
            } catch (IOException e) {
            }
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdOpen) {
            openSelected();
        }
        if (c == cmdClose) {
            fileSelection.displayLoadSetMenu();
        }
        if (c == cmdBack) {
            if (rootsList.contains(currentRoot.getPath() + currentRoot.getName())) {
                    displayAllRoots();
                } else {
                    try {
                        currentRoot = (FileConnection) Connector.open("file://" + currentRoot.getPath(), Connector.READ);
                        displayCurrentRoot();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
        }
    }

    public void rootChanged(int state, String rootNmae) {
    }

}
