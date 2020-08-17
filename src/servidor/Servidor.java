package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import exceptions.RangePortsException;

public class Servidor extends Thread {

    private int solicitarPuertoAUsuario() {

        final Scanner s = new Scanner(System.in);

        while (true) {

            consola.println("\nSeleccione un puerto de escucha (" + PortsManager.MIN_PORT + " - "
                    + PortsManager.MAX_PORT + ").");

            consola.println("<<< Press ENTER to Default >>>");

            try {

                // TODO final String in = s.nextLine();
                final String in = teclado.readLine();

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

            } catch (final NumberFormatException e) {
                System.err.println("\nERROR: El valor introducido no es valido !!!\n");
                e.printStackTrace();
            } catch (final RangePortsException e) {
                System.err.println("\nERROR: Valor fuera del rango permitido. !!!\n");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("\nERROR de E/S\n");
                e.printStackTrace();
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
                consola.println("Servidor en " + puerto + " dice: " + "Servidor a la escucha");

                // instanciar socket para recepciones
                Socket socket;

                // Guardar el socket del cliente cuando inicie comunicación
                socket = serverSocket.accept();

                // mostrar información de socket cliente
                consola.println("Servidor en " + puerto + " dice: Conectado con: " + socket);

                // conectar salida
                // TODO final OutputStream salida = socket.getOutputStream();
                final OutputStreamWriter salida = new OutputStreamWriter(socket.getOutputStream());

                // conectar entrada
                // TODO final InputStream entrada = socket.getInputStream();
                final InputStreamReader entrada = new InputStreamReader(socket.getInputStream());
                
                // Para informar del puerto a usar en la conexión
                // TODO final PrintStream ps = new PrintStream(salida, true);
                // TODO ps.println(++puertoConnect);
                salida.write(String.valueOf(++puertoConnect));

                // mostrar puerto de nueva conexión
                consola.println("Servidor en " + puerto + " dice: Puerto para nueva conexión: " + puertoConnect);
                consola.println("Servidor en " + puerto + " dice: Esperando confirmación");

                // esperar confirmación
                final int nBytes = entrada.read(buffer);

                // procesar lectura
                final char[] chars = new char[nBytes];
                for (int i = 0; i < chars.length; i++)
                    chars[i] = (char) buffer[i];

                // comprobar que el eco del puerto es correcto
                if (Integer.valueOf(String.valueOf(chars)) != puertoConnect) {
                    break;
                } else {
                    consola.println(
                            "Servidor en " + puerto + " dice: Negociación de puerto satisfactoria: " + puertoConnect);
                    // crear un hilo para comunicar por el nuevo puerto. con el numero de puerto como nombre
                    final ThreadServer t = new ThreadServer(puertoConnect, String.valueOf(chars));
                    t.start();
                }

            }
            
            serverSocket.close();
            throw new ConnectException("Servidor en " + puerto + "Puerto negociado incorrecto");

        } catch (final ConnectException e) {

            System.err.println(e.toString());
            e.printStackTrace();

        } catch (final IOException e) {

            System.err.println(e);
            e.printStackTrace();

        }
    }

    @Override
    public String toString() {

        return "Servidor [buffer=" + Arrays.toString(buffer) + ", consola=" + consola + ", pm=" + pm + ", puerto="
                + puerto + ", serverSocket=" + serverSocket + "]";
                
    }

    // PC:192.168.0.160
    // Portátil:192.168.0.169
    // Movil:192.168.0.3
    private final static int DEFAULT_PORT = 1024;
    private int puerto;
    private final char[] buffer = new char[256];
    private ServerSocket serverSocket;

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
    PrintWriter consola = new PrintWriter(System.out, true);

    PortsManager pm;

    public Servidor(final String string) {
        super(string);
    }

}