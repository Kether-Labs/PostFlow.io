package io.ketherlabs.postflow.identity.infrastructure.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;



/**
 * Configuration Spring pour Redis.
 *
 * <p>Déclare un {@link StringRedisTemplate} utilisé par
 * {@code RedisBlacklistAdapter}. On utilise {@code StringRedisTemplate}
 * plutôt que {@code RedisTemplate<Object, Object>} car toutes nos
 * clés et valeurs sont des {@code String} — pas de sérialisation
 * Java inutile en base64 dans Redis.
 */
@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
