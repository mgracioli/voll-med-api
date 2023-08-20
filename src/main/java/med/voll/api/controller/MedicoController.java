package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.domain.medico.DadosListagemMedico;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.medico.*;
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
@SecurityRequirement(name="bearer-key")
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dadosMedico, UriComponentsBuilder uriBuilder){ //O Spring exige, no método cadastrar, que seja passado no cabeçalho da response uma uri Location, que é a uri do registro que acabou de ser adicionado no banco. UriComponentsBuilder é uma classe do Spring que cria essa uri, esse argumento não precisa ser passado no método GET, o Spring gera automaticamente (Ex.: em modo desenvolvimento, a uri é http://localhost:8080 e ele muda automaticamente quando está em produção)
      var medico = new Medico(dadosMedico);
      repository.save(medico);

      var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri(); //complementa a URI gerada anteriormente com o para gerar a uri completa do registro que acabou de ser adicionado no banco, essa uri é o endereço q eu posso acessar para ter os dados detalhados desse registro que acabou de ser adicionado (é um dos pilares para se construir uma API restful - HATEOAS)
      return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size=10, sort={"nome"}) Pageable paginacao){ //@pageableDefault serva para efinir valores default para os parâmetros de paginação (não é obrigatorio passar esses parametros), caso nenhum parâmetro seja passado na url, esses defaults serão utilizados
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity detalharPorId(@PathVariable long id){
        var medico = repository.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
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
