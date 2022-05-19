package nanuda.nanuda.common

import com.fasterxml.jackson.databind.ObjectMapper
import lombok.RequiredArgsConstructor
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@RequiredArgsConstructor
class CustomEntryPoint: AuthenticationEntryPoint {
    private val objectMapper: ObjectMapper? = null
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        response.characterEncoding = "utf-8"
        println("CustomEntryPoint : 잘못된 토큰으로 페이지 요청")


        response.status = HttpServletResponse.SC_FORBIDDEN

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized")

        /*val map: MutableMap<String, String> = HashMap()
        map["errortype"] = "Forbidden"
        map["code"] = "403"
        map["message"] = "잘못된 토큰으로 접근하였습니다. 다시 로그인 해주세요"

        response.writer.write(objectMapper!!.writeValueAsString(map))*/


    }

}
