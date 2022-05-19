package nanuda.nanuda.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import nanuda.nanuda.redis.service.RedisService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(private val userDetailsService: UserDetailsService, private val redisService: RedisService) {
    // JWT를 생성하고 검증하는 컴포넌트
    @Value("\${app.security.key}")
    private var secretKey = ""

    // 토큰 유효시간 30분
    private val atValidTime = 1 * 10 * 1000L
    private val rtValidTime = 1 * 60 * 1000L

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    // AccessToken(AT) JWT 토큰 생성
    fun createAccessToken(userPk: String): String {
        return this.createToken(userPk,atValidTime)
    }
    // RefreshToken(RT) JWT 토큰 생성
    fun createRefreshToken(userPk: String): String {
        return this.createToken(userPk,rtValidTime)
    }

    // JWT 토큰 생성
    fun createToken(userPk: String, vTime: Long): String {
        val claims: Claims = Jwts.claims().setSubject(userPk) // JWT payload 에 저장되는 정보단위
        claims["userPk"] = userPk // 정보는 key / value 쌍으로 저장된다.
        val now = Date()
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(claims) // 정보 저장
            .setIssuedAt(now) // 토큰 발행 시간 정보
            .setExpiration(Date(now.time + vTime)) // set Expire Time
            .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과
            // signature 에 들어갈 secret값 세팅
            .compact()
    }

    // JWT 토큰에서 인증 정보 조회
    fun getAuthentication(token: String): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(getUserPk(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    // 토큰에서 회원 정보 추출
    fun getUserPk(token: String): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }

    // Request의 Header에서 AccessToken(AT) 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    fun resolveToken(request: HttpServletRequest): String? {
        return request.getHeader("accessToken")
    }
    // Request의 Header에서 RefreshToken(RT) 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    fun resolveRefreshToken(request: HttpServletRequest): String? {
        return request.getHeader("refreshToken")
    }

    // 토큰의 유효성 + 만료일자 확인
    fun validateToken(jwtToken: String): Boolean {
        return try {

            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun existsRefreshToken(refreshtoken: String): Boolean {
        return redisService.getValues(refreshtoken) != null
    }
    fun existsAccessToken(accessToken: String): Boolean{
        return redisService.getValues(accessToken) != null
    }

}