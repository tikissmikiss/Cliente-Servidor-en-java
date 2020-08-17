import servidor.Servidor;

public class AppServidor {
    public static void main(String[] args) throws Exception {
        Servidor s = new Servidor("ServidorEco");
        s.start();
    }
}
