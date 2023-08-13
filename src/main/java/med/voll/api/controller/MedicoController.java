package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("medicos")
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dadosMedico, UriComponentsBuilder uriBuilder){ //O Spring exige, no método cadastrar, que seja passado, no cabeçalho da response, uma uri Location, que é a uri do registro que acabou de ser adicionado no banco. UriComponentsBuilder é uma classe do Spring que cria essa uri refernte ao registro que está sendo cadastrado, esse argumento não precisa ser passado no método GET, o Spring geral automaticamente (Ex.; em modo desenvolvimento, a uri é o http://localhost:8080 e ele muda automaticamente quando está em produção)
      var medico = new Medico(dadosMedico);
      repository.save(medico);

      var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri(); //adicionando esse path eu vou ter a uri completa do registro que acabou de ser adicionado no banco
      return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size=10, sort={"nome"}) Pageable paginacao){ //@pageableDefault serva para efinir valores default para os parâmetros de paginação (não é obrigatorio passar esses parametros), caso nenhum parâmetro seja passado na url, esses defaults serão utilizados
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dadosMedico){
        var medico = repository.getReferenceById(dadosMedico.id());
        medico.atualizarInformacoes(dadosMedico);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable long id){
        var medico = repository.getReferenceById(id);
        medico.excluir();

        return ResponseEntity.noContent().build();
    }
}
