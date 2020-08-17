package servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ThreadServer extends Thread {
    private final int puerto;
    private Socket tcp;
    private final ServerSocket serverSocket;
    private OutputStream salida;
    private InputStream entrada;
    public static int ConexionesActivas = 0;

    public ThreadServer(final int puerto) throws IOException {
        super();
        this.puerto = puerto;
        // iniciar servidor
        serverSocket = new ServerSocket(puerto);
        ConexionesActivas++;
    }

    public ThreadServer(final int puerto, final String nombreThread) throws IOException {
        super(nombreThread);
        this.puerto = puerto;
        // iniciar servidor
        serverSocket = new ServerSocket(puerto);
        ConexionesActivas++;
    }

    @Override
    public void run() {
        try {
            System.out.println("Servidor en " + puerto + " dice: Servicio a la espera");
            // instanciar socket para recepciones
            tcp = serverSocket.accept();
            // mostrar información de socket cliente
            System.out.println("Servidor en " + puerto + " dice: Conectado con: " + tcp);
            // conectar salida
            salida = tcp.getOutputStream();
            // conectar entrada
            entrada = tcp.getInputStream();
            // inicializar recursos para datos
            final byte[] buffer = new byte[256];
            final byte cr = (byte) '\n'; // '\n'=10
            int longitud;
            while (true) {
                // leer una linea del socket
                longitud = entrada.read(buffer);
                /*
                 * Comprueba si cliente termina la conexión. comprueba los dos primeros bytes
                 * para compatibilizar con sistemas solo CR (retorno de carro) y con sistemas
                 * CRLF (retorno de carro)+(salto de línea)
                 */
                if (buffer[0] == cr || buffer[1] == cr) {
                    tcp.close();
                    break;
                }
                // mostrar respuesta
                System.out.print("Cliente en " + puerto + " dice: ");
                System.out.write(buffer, 0, longitud);
                // devolver eco
                salida.write(buffer, 0, longitud);
            }
            salida.close();
        } catch (final SocketException e) {
            ConexionesActivas--;
            System.err.println("Servidor en " + puerto + " dice: Conexión perdida !!!");
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            // TODO meter puerto en la cola de disponibles
            System.err.println("Servidor en " + puerto + " dice: finally TODO meter puerto en la cola de disponibles");
        }
        System.out.println("Servidor en " + puerto + " dice: Conexión finalizada por el cliente.");
    }

    @Override
    protected void finalize() throws Throwable {
        ConexionesActivas--;
        System.out.println("Servidor en " + puerto + " dice: Conexión finalizada");
    }
}