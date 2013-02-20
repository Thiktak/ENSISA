/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package banderole;

import java.util.ArrayList;

/**
 *
 * @author Thiktak
 */
public class Messages {

    protected ArrayList<String> messages;
    protected int index;

    public Messages() {
        this.messages = new ArrayList<>();
        this.index = 0;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String messages) {
        this.messages.add(messages);
    }

    public String next() {
        return this.messages.get((this.index++) % this.messages.size());
    }
}
