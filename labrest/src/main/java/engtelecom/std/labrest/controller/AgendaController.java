package engtelecom.std.labrest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import engtelecom.std.labrest.entities.Pessoa;
import engtelecom.std.labrest.exceptions.PessoaNaoEncontradaException;

@RestController
@RequestMapping("/pessoas")
public class AgendaController {
    private final List<Pessoa> agenda = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong();

    @GetMapping
    public List<Pessoa> obterTodasPessoas() {
        return this.agenda;
    }

    @GetMapping("/{id}")
    public Pessoa obterPessoa(@PathVariable long id) {
        for (Pessoa p : this.agenda) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new PessoaNaoEncontradaException(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pessoa adicionarPessoa(@RequestBody Pessoa p) {
        Pessoa n = new Pessoa(contador.incrementAndGet(), p.getNome(), p.getEmail());
        this.agenda.add(n);
        return n;
    }

    @PutMapping("/{id}")
    public Pessoa atualizarPessoa(@RequestBody Pessoa pessoa, @PathVariable long id) {
        for (Pessoa p : this.agenda) {
            if (p.getId() == id) {
                p.setNome(pessoa.getNome());
                p.setEmail(pessoa.getEmail());
                return p;
            }
        }
        throw new PessoaNaoEncontradaException(id);
    }

    @DeleteMapping("/{id}")
    public void excluirPessoa(@PathVariable long id) {
        if (!this.agenda.removeIf(p -> p.getId() == id)) {
            throw new PessoaNaoEncontradaException(id);
        }
    }

    @ControllerAdvice
    class PessoaNaoEncontrada {
        @ResponseBody
        @ExceptionHandler(PessoaNaoEncontradaException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String pessoaNaoEncontrada(PessoaNaoEncontradaException p) {
            return p.getMessage();
        }
    }
}