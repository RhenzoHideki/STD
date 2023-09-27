package eng.telecom.sj.ifsc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Servidor {
    public static void main(String[] args) throws Exception {
        Contador c = new Contador(); // criando objeto
        ContadorDistribuido stub = (ContadorDistribuido) UnicastRemoteObject.exportObject(c, 0);
        // Criando registry
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        Registry registro = LocateRegistry.createRegistry(12345);
        // Registrando objeto com o nome MeuContador
        registro.bind("MeuContador", stub);
        System.out.println("Servidor pronto!");
    }
}