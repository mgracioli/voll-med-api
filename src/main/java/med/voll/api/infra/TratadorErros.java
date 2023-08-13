package med.voll.api.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//RestControllerAdvice é uma classe específica para fazer tratamento de exceptions nas requisições
@RestControllerAdvice
public class TratadorErros {
    @ExceptionHandler(EntityNotFoundException.class) //Com essa anotação, o spring sabe que, sempre que der uma exceção do tipo NotFound, é essa classe que deve ser chamada
    public ResponseEntity trataErro404(){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) //Tratamento para exceções do tipo "argumento inválido", ao passar, por exemplo, um campo null, quando ele deveria vir preenchido na requisição
    public ResponseEntity trataErro400(MethodArgumentNotValidException exception){
        var erros = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErrosValidacao::new).toList());
    }

    private record DadosErrosValidacao(String campo, String mensagem){
        public DadosErrosValidacao(FieldError erro){
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
