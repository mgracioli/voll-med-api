package med.voll.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurarions {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/login").permitAll() //se a requsição for de login, não valida se está autenticado
                .anyRequest().authenticated()   //qualquer outra requisição, verifica se está autenticado
                .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)  //informa para o Spring que o filtro q eu criei la em SecurityFilter tem que ser chamado antes desse filtro aqui do Spring
                .build(); //desabilita a segurança contra csrf (como vamos usar tokens, o token já previne contra ataques csrf) e desabilita o processo de autenticação padrão que o spring security habilita (o formulario de login e senha que ele cria), STATELESS pq essa é uma API rest, STATEFUL seria se fosse uma aplicação Web que precisa guardar a sessão do usuário q está acessando para não precisar usar tokens
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
