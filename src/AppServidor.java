import servidor.ServidorEcoTcp;

public class AppServidor {
    public static void main(String[] args) throws Exception {
        ServidorEcoTcp s = new ServidorEcoTcp("ServidorEco");
        s.start();
    }
}
