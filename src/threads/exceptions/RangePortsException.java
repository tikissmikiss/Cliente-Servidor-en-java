package tcp_ip.tcp.threads.exceptions;

public class RangePortsException extends Exception {
    private static final long serialVersionUID = -532468093245046863L;
    /**
     * Constructs a {@code NoSuchElementException} with {@code "dirección IP no valida"}
     * as its error message string.
     */
    public RangePortsException() {
        super("puerto fuera de rango");
    }

    /**
     * Constructs a {@code NoSuchElementException}, saving a reference
     * to the error message string {@code s} for later retrieval by the
     * {@code getMessage} method.
     *
     * @param   s   the detail message.
     */
    public RangePortsException(String s) {
        super(s);
    }
}