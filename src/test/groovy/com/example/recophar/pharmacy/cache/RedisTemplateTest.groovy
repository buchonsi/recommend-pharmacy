package com.example.recophar.pharmacy.cache

import com.example.recophar.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class RedisTemplateTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private RedisTemplate redisTemplate

    def "RedisTemplate String operations"() {
        given:
        def valueOperations = redisTemplate.opsForValue()
        def key = "stringKey"
        def value = "hello"

        when:
        valueOperations.set(key, value)

        then:
        valueOperations.get(key) == value
    }

    def "RedisTemplate set operations"() {
        given:
        def setOperations = redisTemplate.opsForSet()
        def key = "setKey"

        when:
        setOperations.add(key, "h", "e", "l", "l", "o")

        then:
        setOperations.size(key) == 4L
    }

    def "RedisTemplate hash operations"() {
        given:
        def hashOperations = redisTemplate.opsForHash()
        def key = "hashKey"

        when:
        hashOperations.put(key, "subKey", "value")

        then:
        hashOperations.get(key, "subKey") == "value"

        def entries = hashOperations.entries(key)
        entries.keySet().contains("subKey")
        entries.values().contains("value")

        hashOperations.size(key) == entries.size()
    }
}
