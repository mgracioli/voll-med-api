package med.voll.api.medico;

public record DadosListagemMedico(long id, String nome, String email, String crm, Especialidade especialidade) {
    public DadosListagemMedico(Medico medico){
        this(medico.getId(), medico.getNome(), medico.getEmail(), medico.getCrm(), medico.getEspecialidade()); //o this chama o construtor ali de cima (no public record ...) - sempre q tiver algum outro construtor no Record ele tem q chamar o construtor principal com o this
    }
}
