package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.paciente.*;
import med.voll.api.domain.paciente.DadosCadastroPaciente;
import med.voll.api.domain.paciente.DadosListagemPaciente;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("pacientes")
public class PacienteController {
    @Autowired
    private PacienteRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroPaciente dadosPaciente, UriComponentsBuilder uriBuilder){ //O Spring exige, no método cadastrar, que seja passado no cabeçalho da response uma uri Location, que é a uri do registro que acabou de ser adicionado no banco. UriComponentsBuilder é uma classe do Spring que cria essa uri, esse argumento não precisa ser passado no método GET, o Spring gera automaticamente (Ex.: em modo desenvolvimento, a uri é http://localhost:8080 e ele muda automaticamente quando está em produção)
      var paciente = new Paciente(dadosPaciente);
      repository.save(paciente);

      var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri(); //complementa a URI gerada anteriormente com o para gerar a uri completa do registro que acabou de ser adicionado no banco, essa uri é o endereço q eu posso acessar para ter os dados detalhados desse registro que acabou de ser adicionado (é um dos pilares para se construir uma API restful - HATEOAS)
      return ResponseEntity.created(uri).body(new DadosDetalhamentoPaciente(paciente));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPaciente>> listar(@PageableDefault(size=10, sort={"nome"}) Pageable paginacao){ //@pageableDefault serva para efinir valores default para os parâmetros de paginação (não é obrigatorio passar esses parametros), caso nenhum parâmetro seja passado na url, esses defaults serão utilizados
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemPaciente::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity detalharPorId(@PathVariable long id){
        var paciente = repository.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoPaciente dadosPaciente){
        var paciente = repository.getReferenceById(dadosPaciente.id());
        paciente.atualizarInformacoes(dadosPaciente);

        return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable long id){
        var paciente = repository.getReferenceById(id);
        paciente.excluir();

        return ResponseEntity.noContent().build();
    }
}
