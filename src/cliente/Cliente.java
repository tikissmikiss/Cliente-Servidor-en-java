package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import exceptions.IpAddressException;
import exceptions.RangePortsException;

public class Cliente extends Thread {

    @Override
    public void run() {
        Socket tcp, tcp2;
        try {

            // instanciar socket
            tcp = new Socket(address, puerto);
            consola.print("Cliente en " + puerto + " dice: Conectado con: ");
            consola.println(tcp.toString());

            /*
             * En vez de usar directamente los Streams que proporciona el Socket como en la
             * versión anterior, utiliza un Reader y un Writer para compatibilizar sistemas
             * de codificación de caracteres enviando caracteres en vez de bytes
             */
            // conectar salida
            // TODO OutputStream salida = tcp.getOutputStream();
            OutputStreamWriter salida = new OutputStreamWriter(tcp.getOutputStream());

            // conectamos entrada
            // TODO InputStream entrada = tcp.getInputStream();
            InputStreamReader entrada = new InputStreamReader(tcp.getInputStream());

            // buffer de transmisión
            char[] bufferOut = new char[256];
            // buffer de recepción
            char[] bufferIn = new char[256];
            // para longitudes de los paquetes de entrada y salida
            int longOut = 0, longIn = 0;

            // BufferedReader socketReader = new BufferedReader(new
            // InputStreamReader(entrada));
            // PrintWriter socketWriter = new PrintWriter(salida, true);
            // BufferedWriter socketWriter = new BufferedWriter(new
            // OutputStreamWriter(salida));
            // DataInputStream socketReader = new DataInputStream(entrada);
            // DataOutputStream socketWriter = new DataOutputStream(salida);

            // esperar puerto de conexión
            consola.print("Servidor en " + puerto + " dice: Esperando resignación de puerto...");
            longIn = entrada.read(bufferIn);

            // mostrar puerto recibido
            consola.print("Servidor en " + puerto + " dice: ");
            // TODO consola.write(bufferIn, 0, longIn);
            consola.println(bufferIn);
            consola.println();

            // devolver eco
            salida.write(bufferIn, 0, longIn);

            // procesar lectura
            char[] chars = new char[longIn];
            for (int i = 0; i < chars.length; i++)
                chars[i] = (char) bufferIn[i];
            puerto = Integer.valueOf(String.valueOf(chars));

            // actualizar socket para establecer la comunicación
            tcp2 = new Socket(address, puerto);
            // TODO salida = tcp2.getOutputStream();
            // TODO entrada = tcp2.getInputStream();
            salida = new OutputStreamWriter(tcp2.getOutputStream());
            entrada = new InputStreamReader(tcp2.getInputStream());

            while (longOut >= 0) {
                // leer una linea.
                // Carga el buffer en el array y se almacena la longitud devuelta
                longOut = teclado.read(bufferOut);

                // enviarla por el socket
                salida.write(bufferOut, 0, longOut);
                /*
                 * Comprueba si cliente termina la conexión. comprueba los dos primeros bytes
                 * para compatibilizar con sistemas solo CR (retorno de carro) y con sistemas
                 * CRLF (retorno de carro)+(salto de línea)
                 */
                final byte cr = (byte) '\n'; // '\n'=10
                if (bufferOut[0] == cr || bufferOut[1] == cr)
                    break;
                // leer la respuesta
                // Se carga el buffer en el array y se almacena la longitud devuelta
                longIn = entrada.read(bufferIn);
                // mostrar respuesta
                consola.print("Servidor en " + puerto + " dice: ");
                consola.write(bufferIn, 0, longIn);
            }
            tcp.close();
            tcp2.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            if (e.getMessage().equals("Connection refused: connect")) {
                System.err.println("Imposible conectar con el servidor !!!");
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int solicitarPuertoAUsuario() throws IOException {

        String in = null;
        
        do {

            consola.println("Seleccione el puerto de escucha del servidor.");
            consola.println("<<< Press ENTER to Default (" + DEFAULT_PORT + ") >>>");

            in = teclado.readLine();

            if (in.equals(""))
                break;

            try {

                // comprobar entrada
                if (0 <= Integer.parseInt(in) && Integer.parseInt(in) < Math.pow(2, 16)) {
                    int p = Integer.parseInt(in);
                    return p;
                } else
                    throw new RangePortsException();

            } catch (NumberFormatException e) {
                System.err.println("ERROR: El valor introducido no es valido !!!");
                e.printStackTrace();
            } catch (RangePortsException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }

        } while (!in.equals(""));
        return DEFAULT_PORT;
    }

    private static String solicitarAddressAUsuario() throws IOException {
        String in = null;

        do {

            try {

                consola.println("Introduzca la dirección IP del servidor.");
                consola.println("<<< Press ENTER to Default (" + DEFAULT_ADDRESS + ") >>>");
                in = teclado.readLine();

                if (in.equals(""))
                    break;

                // comprobar entrada
                StringTokenizer st = new StringTokenizer(in, ".");

                try {

                    for (int i = 0; i < 4; i++)
                        if (Integer.parseInt(st.nextToken()) <= 255)
                            ; // Sin cuerpo. Si el bucle concluye significa que la entrada es correcta
                        else
                            throw new IpAddressException();

                    return in;

                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    throw new IpAddressException();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new IpAddressException();
                }

            } catch (IpAddressException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }

        } while (!in.equals(""));

        return DEFAULT_ADDRESS;
    }

    private String address;
    private int puerto;

    /*
     * Un InputStreamReader es un puente entre flujos de bytes y flujos de
     * caracteres: lee bytes y los decodifica en caracteres usando un juego de
     * caracteres específico. El juego de caracteres que utiliza se puede
     * especificar por nombre o se puede dar explícitamente, o se puede aceptar el
     * juego de caracteres predeterminado de la plataforma.
     * 
     * Cada invocación de uno de los métodos read () de InputStreamReader puede
     * hacer que se lean uno o más bytes del flujo de entrada de bytes subyacente.
     * Para permitir la conversión eficiente de bytes a caracteres, se pueden leer
     * más bytes del flujo subyacente de los necesarios para satisfacer la operación
     * de lectura actual.
     * 
     * Para una máxima eficiencia, considere envolver un InputStreamReader dentro de
     * un BufferedReader
     */
    private static InputStreamReader isr = new InputStreamReader(System.in); // Clase que hereda de Reader()
    /*
     * Lee texto de una secuencia de entrada de caracteres, almacenando caracteres
     * en buffer para proporcionar una lectura eficiente de caracteres, matrices y
     * líneas.
     * 
     * Se puede especificar el tamaño del buffer o se puede utilizar el tamaño
     * predeterminado. El valor predeterminado es lo suficientemente grande para la
     * mayoría de los propósitos.
     * 
     * En general, cada solicitud de lectura realizada por un lector provoca que se
     * realice una solicitud de lectura correspondiente del carácter subyacente o
     * del flujo de bytes. Por lo tanto, es aconsejable envolver un BufferedReader
     * alrededor de cualquier Reader cuyas operaciones read () puedan ser costosas,
     * como FileReaders y InputStreamReaders. Por ejemplo,
     * 
     * BufferedReader en = new BufferedReader (nuevo FileReader ("foo.in"));
     * 
     * almacenará en buffer la entrada del archivo especificado. Sin almacenamiento
     * en buffer, cada invocación de read () o readLine () podría hacer que se lean
     * bytes del archivo, se conviertan en caracteres y luego se devuelvan, lo que
     * puede ser muy ineficiente.
     * 
     * Los programas que usan DataInputStreams para la entrada de texto se pueden
     * localizar reemplazando cada DataInputStream con un BufferedReader apropiado.
     */
    private static BufferedReader teclado = new BufferedReader(isr);
    /*
     * Imprime representaciones formateadas de objetos en un flujo de salida de
     * texto. Esta clase implementa todos los métodos de impresión que se encuentran
     * en PrintStream. No contiene métodos para escribir bytes sin formato, para lo
     * cual un programa debe usar flujos de bytes no codificados.
     * 
     * A diferencia de la clase PrintStream, si el vaciado automático está
     * habilitado, se hará solo cuando se invoque uno de los métodos println, printf
     * o format, en lugar de cuando se genere un carácter de nueva línea. Estos
     * métodos utilizan la propia noción de separador de línea de la plataforma en
     * lugar del carácter de nueva línea.
     * 
     * Los métodos de esta clase nunca arrojan excepciones de E / S, aunque algunos
     * de sus constructores pueden hacerlo. El cliente puede preguntar si ha
     * ocurrido algún error invocando checkError ().
     * 
     * Esta clase siempre reemplaza las secuencias de caracteres mal formadas y no
     * asignables con la cadena de reemplazo predeterminada del juego de caracteres.
     * La clase java.nio.charset.CharsetEncoder debe usarse cuando se requiere más
     * control sobre el proceso de codificación.
     */
    private static PrintWriter consola = new PrintWriter(System.out, true);

    // constructores
    public Cliente(int puerto, String nombreThread) {
        super(nombreThread);
        this.puerto = puerto;
        this.address = DEFAULT_ADDRESS;
    }

    public Cliente(int puerto) {
        super();
        this.puerto = puerto;
        this.address = DEFAULT_ADDRESS;
    }

    public Cliente(String address, String nombreThread) {
        super(nombreThread);
        this.address = address;
        this.puerto = DEFAULT_PORT;
    }

    public Cliente(String address) {
        super();
        this.address = address;
        this.puerto = DEFAULT_PORT;
    }

    public Cliente(String address, int puerto, String nombreThread) {
        super(nombreThread);
        this.address = address;
        this.puerto = puerto;
    }

    public Cliente(String address, int puerto) {
        super();
        this.address = address;
        this.puerto = puerto;
    }

    public Cliente() {
        super();
        try {
            address = solicitarAddressAUsuario();
            consola.println("Dirección seleccionada: " + address + "\n");
            puerto = solicitarPuertoAUsuario();
            consola.println("Puerto seleccionada: " + puerto + "\n");
        } catch (IOException e) {
            System.err.println("Error de E/S");
            e.printStackTrace();
        }

    }
    
    // PC:192.168.0.160
    // Portátil:192.168.0.169
    // Movil:192.168.0.3
    private final static int DEFAULT_PORT = 1024;
    private final static String DEFAULT_ADDRESS = "192.168.0.160";

    // // TODO eliminar después de externalizar
    // public static void main(String[] args) throws UnknownHostException,
    // IOException {
    // // int puerto = solicitarPuertoAUsuario();
    // // String address = solicitarAddressAUsuario();

    // Cliente c = new Cliente();
    // c.start();
    // }
}
