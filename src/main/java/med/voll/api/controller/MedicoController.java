package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("medicos")
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroMedico medico){
      repository.save(new Medico(medico));
    }

    @GetMapping
    public Page<DadosListagemMedico> listar(@PageableDefault(size=10, sort={"nome"}) Pageable paginacao){ //@pageableDefault serva para efinir valores default para os parâmetros de paginação (não é obrigatorio passar esses parametros), caso nenhum parâmetro seja passado na url, esses defaults serão utilizados
        return repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
    }

    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizacaoMedico dadosMedico){
        var medico = repository.getReferenceById(dadosMedico.id());
        medico.atualizarInformacoes(dadosMedico);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable long id){
        var medico = repository.getReferenceById(id);
        medico.excluir();
    }
}
