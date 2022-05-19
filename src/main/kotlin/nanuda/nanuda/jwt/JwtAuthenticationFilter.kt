package nanuda.nanuda.security.jwt


import nanuda.nanuda.common.BaseException
import nanuda.nanuda.common.BaseResponseCode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider): OncePerRequestFilter() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        // 헤더에서 JWT 를 받아옵니다.
        val aToken: String? = jwtTokenProvider.resolveToken((request as HttpServletRequest))
        // 유효한 토큰인지 확인합니다.
        if ((aToken != null) && jwtTokenProvider.validateToken(aToken)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            val authentication = jwtTokenProvider.getAuthentication(aToken)
            // SecurityContext 에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().authentication = authentication
        }
        //로그아웃 확인
        if(jwtTokenProvider.existsAccessToken(aToken!!)){
            throw BaseException(BaseResponseCode.EXIST_LOGOUT)
        }
        chain.doFilter(request, response)
    }

}