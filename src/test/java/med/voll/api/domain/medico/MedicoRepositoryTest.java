package med.voll.api.domain.medico;

import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.domain.paciente.DadosCadastroPaciente;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest    //anotação para quando eu quero testar um repository ou um método que faz conexão com o banco, o spring ja carrega o manager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    //Por padrão, o Spring usa um banco em memória para fazer os testes, aqui estou dizendo para ele não isar esse banco e, sim, o banco real da aplicação (o MySQL)
@ActiveProfiles("test") //Informa para o Spring que eu quero usar o application-test.properties, esse profile sobrescreve o caminho do banco de dados pq aqui eu quero usar o banco de teste e não o que a aplicação está usando, todas as outras propriedade do application.properties são herdadas pelo application-test.properties
class MedicoRepositoryTest {
    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Deve retornar null quando o único médico cadastrado não está disponível na data")
    void escolherMedicoAleatorioPorEspecialidadeLivreNaDataCenario1() {
        var proximaSegundaAs10 = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(10,0); //pega sempre a próxima segunda feira a partir da data atual
       var medico = cadastrarMedico("Antonio", "an@an.com", "123456", Especialidade.CARDIOLOGIA);
       var paciente = cadastrarPaciente("Jessica", "j@j.com", "36436436402");

       cadastrarConsulta(medico, paciente, proximaSegundaAs10);

        var medicoLivre = medicoRepository.escolherMedicoAleatorioPorEspecialidadeLivreNaData(Especialidade.CARDIOLOGIA, proximaSegundaAs10);
        assertThat(medicoLivre).isNull();
    }

    @Test
    @DisplayName("Deve retornar um medico quando ele estiver disponível na data")
    void escolherMedicoAleatorioPorEspecialidadeLivreNaDataCenario2() {
        var proximaSegundaAs10 = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(10,0); //pega sempre a próxima segunda feira a partir da data atual
        var medico = cadastrarMedico("Antonio", "an@an.com", "123456", Especialidade.CARDIOLOGIA);
        var medicoLivre = medicoRepository.escolherMedicoAleatorioPorEspecialidadeLivreNaData(Especialidade.CARDIOLOGIA, proximaSegundaAs10);

        assertThat(medicoLivre).isEqualTo(medico);
    }

    private void cadastrarConsulta(Medico medico, Paciente paciente, LocalDateTime data) {
        em.persist(new Consulta(null, medico, paciente, data));
    }

    private Medico cadastrarMedico(String nome, String email, String crm, Especialidade especialidade) {
        var medico = new Medico(dadosMedico(nome, email, crm, especialidade));
        em.persist(medico);
        return medico;
    }

    private Paciente cadastrarPaciente(String nome, String email, String cpf) {
        var paciente = new Paciente(dadosPaciente(nome, email, cpf));
        em.persist(paciente);
        return paciente;
    }

    private DadosCadastroMedico dadosMedico(String nome, String email, String crm, Especialidade especialidade) {
        return new DadosCadastroMedico(
                nome,
                email,
                "61999999999",
                crm,
                especialidade,
                dadosEndereco()
        );
    }

    private DadosCadastroPaciente dadosPaciente(String nome, String email, String cpf) {
        return new DadosCadastroPaciente(
                nome,
                email,
                "61999999999",
                dadosEndereco()
        );
    }

    private DadosEndereco dadosEndereco() {
        return new DadosEndereco(
                "rua xpto",
                "bairro",
                "00000000",
                "Brasilia",
                "DF",
                null,
                null
        );
    }
}