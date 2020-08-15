package servidor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import exceptions.RangePortsException;

// PC:192.168.0.160
// Portatil:192.168.0.169
// Movil:192.168.0.3
public class ServidorEcoTcp extends Thread {
    int puerto;
    final byte[] b = new byte[256];
    ServerSocket serverSocket;

    PortsManager pm;

    public ServidorEcoTcp(String string) {
        super(string);
    }

    private int solicitarPuertoAUsuario() {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("\nSeleccione un puerto de escucha (" + PortsManager.MIN_PORT + " - "
                    + PortsManager.MAX_PORT + ").");
            System.out.println("<<< Press ENTER to Default >>>");
            String in = s.nextLine();
            try {
                if (!in.equals("")) {
                    if (PortsManager.MIN_PORT < Integer.parseInt(in) && Integer.parseInt(in) < PortsManager.MAX_PORT) {
                        pm = new PortsManager(Integer.parseInt(in));
                        break;
                    } else {
                        throw new RangePortsException();
                    }
                } else {
                    pm = new PortsManager(PortsManager.DEFAULT_LISTEN_PORT);
                    break;
                }
            } catch (NumberFormatException e) {
                System.err.println("\nERROR: El valor introducido no es valido !!!\n");
            } catch (RangePortsException e) {
                System.err.println("\nERROR: Valor fuera del rango permitido. !!!\n");
            }
        }
        s.close();
        return pm.getListenPort();
    }

    @Override
    public void run() {
        puerto = solicitarPuertoAUsuario();
        int puertoConnect = puerto;

        try {

            // instanciar socket servidor
            serverSocket = new ServerSocket(puerto);
            while (true) {
                // informar de escucha
                System.out.println("Servidor en " + puerto + " dice: " + "Servidor a la escucha");
                // instanciar socket para recepciones
                Socket socket;
                socket = serverSocket.accept();
                // mostrar información de socket cliente
                System.out.println("Servidor en " + puerto + " dice: Conectado con: " + socket);
                // conectar salida
                final OutputStream salida = socket.getOutputStream();
                // conectar entrada
                final InputStream entrada = socket.getInputStream();
                // Para informar del puerto a usar en la conexión
                final PrintStream ps = new PrintStream(salida, true);
                ps.println(++puertoConnect);
                // mostrar puerto de nueva conexión
                System.out.println("Servidor en " + puerto + " dice: Puerto para nueva conexión: " + puertoConnect);
                System.out.println("Servidor en " + puerto + " dice: Esperando confirmación");
                // esperar confirmación
                final int nBytes = entrada.read(b);
                // procesar lectura
                final char[] chars = new char[nBytes];
                for (int i = 0; i < chars.length; i++)
                    chars[i] = (char) b[i];
                // comprobar que el eco del puerto es correcto
                if (Integer.valueOf(String.valueOf(chars)) != puertoConnect) {
                    break;
                } else {
                    System.out.println(
                            "Servidor en " + puerto + " dice: Negociación de puerto satisfactoria: " + puertoConnect);
                    // crear un hilo para comunicar por el nuevo puerto. con el numero de puerto
                    // como nombre
                    final ThreadServerEcoTcp t = new ThreadServerEcoTcp(puertoConnect, String.valueOf(chars));
                    t.start();
                }
            }
            serverSocket.close();
            throw new ConnectException("Servidor en " + puerto + "Puerto negociado incorrecto");
        } catch (ConnectException e) {
            System.err.println(e.toString());
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(final String[] args) throws IOException {
        ServidorEcoTcp serv = new ServidorEcoTcp("ServidorEco");
        serv.start();
    }
}