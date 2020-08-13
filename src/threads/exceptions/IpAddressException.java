package tcp_ip.tcp.threads.exceptions;

public class IpAddressException extends Exception {
    private static final long serialVersionUID = 7128738470705390964L;

    /**
     * Constructs a {@code NoSuchElementException} with {@code "dirección IP no valida"}
     * as its error message string.
     */
    public IpAddressException() {
        super("dirección IP no valida");
    }

    /**
     * Constructs a {@code NoSuchElementException}, saving a reference
     * to the error message string {@code s} for later retrieval by the
     * {@code getMessage} method.
     *
     * @param   s   the detail message.
     */
    public IpAddressException(String s) {
        super(s);
    }
}