package com.carsales.carsales_anaytics.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AIQueryServiceImpl implements AIQueryService {

    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;

    public AIQueryServiceImpl(ChatClient.Builder builder, JdbcTemplate jdbcTemplate) {
        // Build the client with a default system instruction for better stability
        this.chatClient = builder.build();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String process(String question) {

        String sql = generateSQL(question);
        if (sql.equalsIgnoreCase("INVALID")) {
            return "Only table-related questions allowed";
        }
        if (!isSafe(sql)) {
            return "X Unsafe query";
        }
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

            if (result.isEmpty()) {
                return "⚠️ No data found";
            }

            return toNaturalLanguage(question,result);

        } catch (Exception e) {
            return "❌ Query failed";
        }
    }

       // return null;
        private String toNaturalLanguage(String question, List<Map<String, Object>> result) {

            String prompt = """
                Convert database result into a human readable answer.
                
                User Question:
                """ + question + """
                
                DB Result:
                """ + result.toString() + """
                
                Rules:
                - Answer clearly (don't write too much)
                - Do not show JSON
                - Do not explain SQL
                """;

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();
        }



    //Validation
    private boolean isSafe(String sql) {
        String lower = sql.toLowerCase();
        return lower.startsWith("select")
                && !lower.contains("drop")
                && !lower.contains("delete")
                && !lower.contains("update")
                && !lower.contains("insert");
    }


    private String generateSQL(String question) {
        String systemInstruction = """
        You are a SQL generator for the 'car_sales' table.
        Columns: id, brand, car_number, city, color, contact_number, customer_name, date_of_purchase, email, engine, fuel_type, mileage, model, payment_mode, price, state, time_of_purchase, warranty_period, year.
        
        Rules:
        1. Only generate valid SQL SELECT queries.
        2. If the user asks for counts (e.g., 'count of all', 'how many'), use 'SELECT count(*) FROM car_sales'.
        3. If the question is NOT related to cars or the database, respond with exactly: INVALID.
        4. Do not provide any conversational text, labels, or formatting. Return ONLY the raw SQL or the word INVALID.
        """;

        return chatClient.prompt()
                .system(systemInstruction)
                .user(question)
                .call()
                .content()
                .trim();
    }
}
