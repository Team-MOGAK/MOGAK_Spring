package com.mogak.spring.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;


    public String getValues(String key){
        //opsForValue : Strings를 쉽게 Serialize / Deserialize 해주는 Interface
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void setValues(String key, String value, long time){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        Duration expiredDuration = Duration.ofMillis(time);
        values.set(key,value,expiredDuration); // 만료시간 지나면 자동 삭제
    }

    public void deleteValues(String key){
        redisTemplate.delete(key);
    }




}