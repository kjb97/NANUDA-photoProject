package nanuda.nanuda.redis.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService(@Qualifier(value = "memberRedisTemplate") val redisTemplate: RedisTemplate<String, String>) {

    // 키-벨류 설정
    fun setValues(token: String, email: String) {
        val values: ValueOperations<String, String> = redisTemplate.opsForValue()
        values.set(token,email,Duration.ofMinutes(3))
    }
    // 키-벨류 설정
    fun getValues(token: String): String? {
        val values: ValueOperations<String, String> = redisTemplate.opsForValue()
        return values.get(token)
    }
    // 키-벨류 설정
    fun delValues(token: String) {
        redisTemplate.delete(token)
    }
}