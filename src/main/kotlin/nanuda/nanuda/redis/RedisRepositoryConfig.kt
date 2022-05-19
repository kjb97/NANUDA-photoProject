package nanuda.nanuda.redis

import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
class RedisRepositoryConfig {
    @Value("\${spring.redis.port}")
    private val port = 0

    @Value("\${spring.redis.host}")
    private val host = ""
    // lettuce
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory? {
        return LettuceConnectionFactory(host,port)
    }

    @Bean(name = ["memberRedisTemplate"])
    fun redisTemplate(): RedisTemplate<String, String>? {
        val redisTemplate = RedisTemplate<String, String>()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = GenericJackson2JsonRedisSerializer()

        redisTemplate.setConnectionFactory(redisConnectionFactory()!!)
        return redisTemplate
    }
}