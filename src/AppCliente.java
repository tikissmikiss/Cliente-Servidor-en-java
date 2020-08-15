import java.io.IOException;
import java.net.UnknownHostException;

import cliente.ClienteEcoTcp;

public class AppCliente {
    public static void main(String[] args) throws UnknownHostException, IOException {

        ClienteEcoTcp c = new ClienteEcoTcp();
        c.start();
    }
}
