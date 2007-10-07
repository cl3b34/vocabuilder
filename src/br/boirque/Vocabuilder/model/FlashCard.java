package br.boirque.vocabuilder.model;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;

public class FlashCard extends MIDlet implements CommandListener {
    private Command exitCommand;
    private TextBox tbox;

    public FlashCard() {
        exitCommand = new Command("Exit", Command.EXIT, 1);
        tbox = new TextBox("Hello world MIDlet", "Hello World!", 25, 0);
        tbox.addCommand(exitCommand);
        tbox.setCommandListener(this);
    }

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

