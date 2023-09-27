package eng.telecom.sj.ifsc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ContadorDistribuido extends Remote {
    public void incrementa() throws RemoteException;

    public int obtemValor() throws RemoteException;
}
