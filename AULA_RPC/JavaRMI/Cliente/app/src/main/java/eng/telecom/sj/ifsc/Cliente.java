package eng.telecom.sj.ifsc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {
    public static void main(String[] args) throws Exception {
        // Obtendo referência para o registro (tem que conhecer IP e porta)
        Registry registro = LocateRegistry.getRegistry("127.0.01", 12345);
        // Obtendo referência para o objeto instanciado pelo Servidor
        ContadorDistribuido stub = (ContadorDistribuido) registro.lookup("MeuContador");
        // invocando métodos do objeto remoto
        System.out.println("valor: " + stub.obtemValor());
        stub.incrementa();
        System.out.println("valor: " + stub.obtemValor());
    }
}