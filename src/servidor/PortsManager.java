package servidor;

// TODO import java.util.ArrayList;
// import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class PortsManager implements TypePort {
    // constantes
    public static final int DEFAULT_LISTEN_PORT = 1024;
    public static final int MAX_CONNECTIONS = 32;
    public static final int MIN_PORT = 1024;
    public static final int MAX_PORT = MIN_PORT + MAX_CONNECTIONS;
    // atributos
    private final Port listenPort;
    // variables de instancia
    private Queue<Port> cola = new LinkedList<>();
    // private ArrayList<Port> puertos = new ArrayList<>();

    // constructores
    public PortsManager() {
        this.listenPort = new Port(DEFAULT_LISTEN_PORT, LISTEN_PORT);
        initialize();
    }

    public PortsManager(int listenPort) {
        this.listenPort = new Port(listenPort, LISTEN_PORT);
        initialize();
    }

    // métodos privados
    private void initialize() {
        for (int i = MIN_PORT; i <= MAX_PORT; i++) {
            if (i != listenPort.port) {
                // puertos.add(new Port(i, CONNECTION_PORT));
                // cola.add(puertos.get(i - MIN_PORT));
                cola.add(new Port(i, CONNECTION_PORT));
            } else {
                // puertos.add(new Port(i, LISTEN_PORT));
            }
        }
    }

    // clases internas
    protected class Port implements TypePort {
        boolean isListenPort;
        boolean isConnectionPort;
        int port;

        public Port(int numPort, int typePort) {
            port = numPort;
            if (typePort == LISTEN_PORT) {
                isListenPort = true;
                isConnectionPort = false;
            } else {
                isListenPort = false;
                isConnectionPort = true;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(port);
        }
    }

    public void setUnusedPort(int port) {
        cola.add(new Port(port, CONNECTION_PORT));
    }

    public int getUnusedPort() {
        return cola.poll().port;
    }

    public int getListenPort() {
        return listenPort.port;
    }

}