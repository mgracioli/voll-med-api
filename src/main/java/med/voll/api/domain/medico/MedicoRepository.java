package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Page<Medico> findAllByAtivoTrue(Pageable paginacao);    //só com o nome que foi dado para o método o Spring consegue saber qual consulta ele deve fazer no banco, não precisa implementar o método

    @Query("""
            select m from Medico m
            where m.ativo = 1 and
            m.especialidade = :especialidade and
            m.id not in (select c.medico.id from Consulta c where c.data = :data)
            order by rand()
            limit 1
            """)
    Medico escolherMedicoAleatorioPorEspecialidadeLivreNaData(Especialidade especialidade, LocalDateTime data);
}
