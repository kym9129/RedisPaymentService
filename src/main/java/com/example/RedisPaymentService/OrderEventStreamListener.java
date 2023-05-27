package com.example.RedisPaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.util.HashMap;
import java.util.Map;

// OrderService에서 발생한 이벤트를 받아서 처리
public class OrderEventStreamListener implements StreamListener<String , MapRecord<String, String, String>> {

    int paymentProcessId = 0;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        // 엔트리의 필드와 벨류를 맵으로 받음
        Map<String, String> map = message.getValue();

        String userId = (String) map.get("userId");
        String productId = (String) map.get("productId");
        String price = (String) map.get("price");

        // 결제 관련 로직 처리
        // ...

        String paymentIdStr = Integer.toString(paymentProcessId++);

        // 결제 완료 이벤트 발행
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("userId", userId);
        fieldMap.put("productId", productId);
        fieldMap.put("price", price);
        fieldMap.put("paymentProcessId", paymentIdStr);

        redisTemplate.opsForStream().add("payment-events", fieldMap);

        System.out.println("[Order consumed] Created payment: " + paymentIdStr);
        // XRANGE payment-events - + 생성한 모든 payment-events 엔트리 조회
    }
}
