//@@author ongweekeong
package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.inbox.Inbox;
import seedu.addressbook.inbox.MessageFilePaths;
import seedu.addressbook.inbox.Msg;
import seedu.addressbook.password.Password;
import seedu.addressbook.timeanddate.TimeAndDate;

import java.io.IOException;
import java.util.TreeSet;

/** Prints out all unread notifications ordered by read status, priority, then timestamp
 * (earlier message has higher priority).
 *
 * @return messages to be printed out on the main window.
 */

public class InboxCommand extends Command {
    public static final String COMMAND_WORD = "inbox";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Displays all messages in the application starting from the unread and most urgent.\n\t"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_TOTAL_MESSAGE_NOTIFICATION = "You have %d total messages, %d unread.\n";
    public static final String MESSAGE_UNKNOWN_ERROR = "Error loading messages. Check that %s exists.\n";

    @Override
    public CommandResult execute() {

        Inbox myInbox = new Inbox(Password.getID());
        TreeSet<Msg> allMsgs;
        int myUnreadMsgs;
        int totalMsgs;
        int messageNum = 1;
        try {
            allMsgs = myInbox.loadMsgs();
            totalMsgs = allMsgs.size();
            myUnreadMsgs = myInbox.checkNumUnreadMessages();
            String fullPrintedMessage = MESSAGE_TOTAL_MESSAGE_NOTIFICATION;
            for (Msg msgToPrint : allMsgs) {
               // msgToPrint = allMsgs.pollFirst();
                fullPrintedMessage += concatenateMsg(messageNum, msgToPrint);
                messageNum++;
            }
            allMsgs.clear();
            return new CommandResult(String.format(fullPrintedMessage, totalMsgs, myUnreadMsgs));

        } catch (IOException e) {
            //e.printStackTrace();
            return new CommandResult(String.format(MESSAGE_UNKNOWN_ERROR, MessageFilePaths.getFilePathFromUserId(Password.getID())));
        }
    }

    public static String concatenateMsg(int messageNum, Msg message) throws NullPointerException{
        String concatenatedMsg = null;
        TimeAndDate dateFormatter = new TimeAndDate();
//        try{
//            concatenatedMsg = String.valueOf(messageNum) + ".\t[UNREAD] Sender: " + message.getSenderId() + " Priority: " + message.getPriority() +
//                    ", Sent: " + dateFormatter.outputDATHrs(message.getTime()) + ",\n\t\tMessage: " + message.getMsg() + ", Coordinates: " +
//                    message.getLatitude() + ", " + message.getLongitude() + ", ETA: " + message.getEta() + ".\n";
//        }
//        catch(Exception e){
        if (!message.isRead) {
            concatenatedMsg = String.valueOf(messageNum) + ". [UNREAD] Sender: " + message.getSenderId() + " Priority: " +
                    message.getPriority() + ", Sent: " + dateFormatter.outputDATHrsForMain(message.getTime()) + ",\n\tMessage: " + message.getMsg() + "\n\n";
        }
        else{
            concatenatedMsg = String.valueOf(messageNum) + ".\tSender: " + message.getSenderId() + " Priority: " +
                    message.getPriority() + ", Sent: " + dateFormatter.outputDATHrsForMain(message.getTime()) + ",\n\tMessage: " + message.getMsg() + "\n\n";
        }
//        }
        return concatenatedMsg;
    }

}

