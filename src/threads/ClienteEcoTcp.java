package tcp_ip.tcp.threads;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

import tcp_ip.tcp.threads.exceptions.IpAddressException;
import tcp_ip.tcp.threads.exceptions.RangePortsException;

public class ClienteEcoTcp extends Thread {
    // PC:192.168.0.160
    // Portatil:192.168.0.169
    // Movil:192.168.0.3
    private final static int DEFAULT_PORT = 1024;
    private final static String DEFAULT_ADDRESS = "192.168.0.160";
    private String direccion;
    private int puerto;

    private PortsManager pm;

    // constructores
    public ClienteEcoTcp(int puerto, String nombreThread) {
        super(nombreThread);
        this.puerto = puerto;
        this.direccion = DEFAULT_ADDRESS;
    }

    public ClienteEcoTcp(int puerto) {
        super();
        this.puerto = puerto;
        this.direccion = DEFAULT_ADDRESS;
    }

    public ClienteEcoTcp(String direccion, String nombreThread) {
        super(nombreThread);
        this.direccion = direccion;
        this.puerto = DEFAULT_PORT;
    }

    public ClienteEcoTcp(String direccion) {
        super();
        this.direccion = direccion;
        this.puerto = DEFAULT_PORT;
    }

    public ClienteEcoTcp(String direccion, int puerto, String nombreThread) {
        super(nombreThread);
        this.direccion = direccion;
        this.puerto = puerto;
    }

    public ClienteEcoTcp(String direccion, int puerto) {
        super();
        this.direccion = direccion;
        this.puerto = puerto;
    }

    private static int solicitarPuertoAUsuario() {
        Scanner s = new Scanner(System.in);
        String in = null;
        while (in != "") {
            System.out.print("Seleccione el puerto de escucha del servidor.");
            System.out.println("\t<<< Press ENTER to Default >>>");
            in = s.nextLine();
            try {
                // comprobar entrada
                if (0 <= Integer.parseInt(in) && Integer.parseInt(in) < Math.pow(2, 16)) {
                int p = Integer.parseInt(in);
                s.close();
                return p;
                } else throw new RangePortsException();
            } catch (NumberFormatException e) {
                System.out.println("\nERROR: El valor introducido no es valido !!!\n");
            } catch (RangePortsException e) {
                e.printStackTrace();
            }
        }
        return DEFAULT_PORT;
    }

    private static String solicitarDireccionAUsuario() {
        Scanner s = new Scanner(System.in);
        String in = null;
        while (in != "") {
            try {
                System.out.println("\nIntroduzca la dirección IP del servidor.");
                System.out.println("<<< Press ENTER to Default >>>");
                in = s.nextLine();
                // comprobar entrada
                StringTokenizer st = new StringTokenizer(in, ".");
                try {
                    for (int i = 0; i < 4; i++)
                        if (Integer.parseInt(st.nextToken()) <= 255)
                            ;
                        else
                            throw new IpAddressException();
                } catch (NoSuchElementException e) {
                    throw new IpAddressException();
                } catch (NumberFormatException e) {
                    throw new IpAddressException();
                }
            } catch (IpAddressException e) {
                System.err.println(e);
            }

            // if (!in.equals("")) {
            // int p = Integer.parseInt(in);
            // s.close();
            // return in;
            // }
        }
        return DEFAULT_ADDRESS;

    }

    @Override
    public void run() {
        System.out.print("Cliente en " + puerto + " dice: Conectado con: ");
        Socket tcp, tcp2;
        try {
            tcp = new Socket(direccion, puerto);
            System.out.println(tcp.toString());
            // conectar salida
            OutputStream salida = tcp.getOutputStream();
            // conectamos entrada
            InputStream entrada = tcp.getInputStream();
            // buffer de transmisión
            byte[] bufferOut = new byte[256];
            // buffer de recepción
            byte[] bufferIn = new byte[256];
            // para longitudes de los paquetes de entrada y salida
            int longOut = 0, longIn = 0;

            // esperar puerto de conexión
            longIn = entrada.read(bufferIn);
            // mostrar puerto recibido
            System.out.print("Servidor en " + puerto + " dice: ");
            System.out.write(bufferIn, 0, longIn);
            System.out.println();
            // devolver eco
            salida.write(bufferIn, 0, longIn);

            // procesar lectura
            char[] chars = new char[longIn];
            for (int i = 0; i < chars.length; i++)
                chars[i] = (char) bufferIn[i];
            puerto = Integer.valueOf(String.valueOf(chars));
            // actualizar socket para establecer la comunicación
            tcp2 = new Socket(direccion, puerto);
            salida = tcp2.getOutputStream();
            entrada = tcp2.getInputStream();

            while (longOut >= 0) {
                // leer una linea.
                // Se carga el buffer en el array y se almacena la longitud devuelta
                longOut = System.in.read(bufferOut);
                // enviarla por el socket
                salida.write(bufferOut, 0, longOut);
                /*
                 * Comprueba si cliente termina la conexión. comprueba los dos primeros bytes
                 * para compatibilizar con sistemas solo CR (retorno de carro) y con sistemas
                 * CRLF (retorno de carro)+(salto de línea)
                 */
                final byte cr = (byte) '\n'; // '\n'=10
                if (bufferOut[0] == cr || bufferOut[1] == cr)
                    // TODO if (longOut < 3)
                    break;
                // leer la respuesta
                // Se carga el buffer en el array y se almacena la longitud devuelta
                longIn = entrada.read(bufferIn);
                // mostrar respuesta
                System.out.print("Servidor en " + puerto + " dice: ");
                System.out.write(bufferIn, 0, longIn);
            }
            tcp.close();
            tcp2.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        int puerto = solicitarPuertoAUsuario();
        String direccion = solicitarDireccionAUsuario();

        ClienteEcoTcp c = new ClienteEcoTcp(puerto);

    }
}
