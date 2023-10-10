package engtelecom.std;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import lombok.Data;

// Anotação @Data gera os getters/setters, toString, equals, hashCode, construtor padrão e um construtor com todos os atributos que estiverem com a anotação @NonNull
@Data
public class Pessoa {
    @NonNull
    private Integer id;
    @NonNull
    private String nome;
    @NonNull
    private String email;
    private Map<String, String> telefones = new HashMap<>();

    public String adicionarTelefone(String rotulo, String numero) {
        return this.telefones.put(rotulo, numero);
    }
}