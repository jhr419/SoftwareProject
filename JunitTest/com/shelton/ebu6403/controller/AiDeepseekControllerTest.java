package com.shelton.ebu6403.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AiDeepseekController.
 * This class contains test cases to verify the functionality of the AI-based deepseek API integration.
 */
class AiDeepseekControllerTest {

    /**
     * Tests the sendRequest method with a real API call.
     * This test verifies that:
     * 1. The API response is not null
     * 2. The response contains the expected answer (Paris) for a simple question
     *
     * @throws Exception if there is an error during the API request
     */
    @Test
    void testSendRequest_withRealAPI() throws Exception {
        AiDeepseekController controller = new AiDeepseekController();
        String response = controller.testSendRequest("What's the capital of France?");

        assertNotNull(response);                       // Response should not be null
        assertTrue(response.toLowerCase().contains("paris")); // Should contain "Paris" (case insensitive)
    }
}
