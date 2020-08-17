import java.io.IOException;
import java.net.UnknownHostException;

import cliente.Cliente;

public class AppCliente {
    public static void main(String[] args) throws UnknownHostException, IOException {

        Cliente c = new Cliente();
        c.start();
    }
}
