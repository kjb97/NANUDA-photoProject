package nanuda.nanuda.config

import com.fasterxml.jackson.databind.ObjectMapper
import nanuda.nanuda.security.jwt.JwtAuthenticationFilter
import nanuda.nanuda.security.jwt.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
class SecurityConfig(private val jwtTokenProvider: JwtTokenProvider, private val objectMapper:ObjectMapper ): WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin("*")
        configuration.addAllowedMethod(HttpMethod.GET)
        configuration.addAllowedMethod(HttpMethod.POST)
        configuration.addAllowedHeader("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }


    override fun configure(http: HttpSecurity) {

        http.headers()
            .and()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .httpBasic().disable() // rest api??? ??????, ?????? ?????? ??????
            .csrf().disable() // csrf ?????? ?????? disable ??????
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ?????? ?????? ??????????????? ?????? ?????? ??????
            .and()
            .authorizeRequests() // ????????? ?????? ???????????? ??????
            .antMatchers("/api/**").authenticated()
            .antMatchers("/register/**", "/login/**","/custom/logout/**").permitAll() // ?????????, ??????????????? ????????? ?????? ??????
            .and()
            // .exceptionHandling().authenticationEntryPoint(customEntryPoint)
           // .and()
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            // .addFilterBefore(JwtExceptionFilter(), JwtAuthenticationFilter::class.java)
    }




}