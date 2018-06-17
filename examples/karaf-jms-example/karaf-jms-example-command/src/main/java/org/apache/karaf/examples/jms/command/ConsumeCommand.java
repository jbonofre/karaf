package org.apache.karaf.examples.jms.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

@Service
@Command(scope = "example", name = "consume", description = "Consume a message from a JMS queue")
public class ConsumeCommand implements Action {

    @Argument(index = 0, name = "queue", description = "Name of the queue", required = true, multiValued = false)
    String queue;

    @Reference
    ConnectionFactory connectionFactory;

    @Override
    public Object execute() throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(queue);
        MessageConsumer consumer = session.createConsumer(destination);
        TextMessage message = (TextMessage) consumer.receive(60000);
        if (message == null) {
            throw new IllegalStateException("No message received");
        } else {
            System.out.println(message.getText());
        }
        return null;
    }

}
