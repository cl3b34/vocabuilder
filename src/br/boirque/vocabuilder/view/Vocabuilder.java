package br.boirque.vocabuilder.view;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;

public class Vocabuilder extends MIDlet implements CommandListener {
    private Command exitCommand = new Command("Exit", Command.EXIT, 3);
    private Command turnCommand = new Command("Turn", Command.SCREEN, 1);
    private Command doneCommand = new Command("Done", Command.SCREEN, 1);
    private Command wrongCommand = new Command("Wrong", Command.SCREEN, 1);
    private Command restartCommand = new Command("Restart", Command.SCREEN, 1);
    private Command againCommand = new Command("Again", Command.SCREEN, 1);
    private Command reviewCommand = new Command("Review", Command.SCREEN, 1);
    private Command backCommand = new Command("Back", Command.BACK, 1);
    private TextBox tbox;
    
    public Vocabuilder() {
        tbox = new TextBox("Hello world MIDlet", "Hello World!", 150, 0);
        tbox.addCommand(exitCommand);
        tbox.setCommandListener(this);
    }

    /**
     * Takes care of initializing the app
     */
    protected void startApp() {
    
		Display.getDisplay(this).setCurrent(tbox);
    }
    
    protected void pauseApp() {}
    protected void destroyApp(boolean bool) {}

    public void commandAction(Command cmd, Displayable disp) {
        if (cmd == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        }
    }
}

