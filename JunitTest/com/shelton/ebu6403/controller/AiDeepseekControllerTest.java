package com.shelton.ebu6403.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiDeepseekControllerTest {

    @Test
    void testSendRequest_withRealAPI() throws Exception {
        AiDeepseekController controller = new AiDeepseekController();
        String response = controller.testSendRequest("What's the capital of France?");

        assertNotNull(response);                       // 响应不为空
        assertTrue(response.toLowerCase().contains("paris")); // 应该包含 Paris（注意大小写）
    }
}
