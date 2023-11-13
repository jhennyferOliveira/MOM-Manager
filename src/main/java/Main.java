
import network.ConnectionHandler;
import view.BrokerMainView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {

        ConnectionHandler.connectToActiveMQ();
        SwingUtilities.invokeLater(() -> {
            try {
                var view = new BrokerMainView();
                view.setUpFrame();
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}