package com.agent.agentj;

import com.agent.agentj.tool.impl.CalculatorTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "anthropic.api.key=test-key-placeholder"
})
class AgentJApplicationTests {

    @Autowired
    private CalculatorTool calculatorTool;

    @Test
    void contextLoads() {
        // Verifies the Spring context starts successfully
    }

    @Test
    void calculatorEvaluatesMultiplication() {
        String result = calculatorTool.execute(Map.of("expression", "347 * 19"));
        assertEquals("6593", result);
    }

    @Test
    void calculatorEvaluatesParentheses() {
        String result = calculatorTool.execute(Map.of("expression", "(100 + 50) * 3"));
        assertEquals("450", result);
    }

    @Test
    void calculatorEvaluatesDivision() {
        String result = calculatorTool.execute(Map.of("expression", "10 / 4"));
        assertEquals("2.5", result);
    }
}
