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
            .httpBasic().disable() // rest api만 고려, 기본 설정 해제
            .csrf().disable() // csrf 보안 토큰 disable 처리
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 사용 안함
            .and()
            .authorizeRequests() // 요청에 대한 사용권한 체크
            .antMatchers("/api/**").authenticated()
            .antMatchers("/register/**", "/login/**","/custom/logout/**").permitAll() // 로그인, 회원가입은 누구나 접근 가능
            .and()
            // .exceptionHandling().authenticationEntryPoint(customEntryPoint)
           // .and()
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            // .addFilterBefore(JwtExceptionFilter(), JwtAuthenticationFilter::class.java)
    }




}