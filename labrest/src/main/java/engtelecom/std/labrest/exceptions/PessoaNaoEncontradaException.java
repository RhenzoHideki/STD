package engtelecom.std.labrest.exceptions;

public class PessoaNaoEncontradaException extends RuntimeException {
    public PessoaNaoEncontradaException(long id) {
        super("Não foi possível encontrar pessoa com o id: " + id);
    }
}