package com.ph.core.story.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.springframework.util.StreamUtils;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@WebFilter("/*")
public class XSSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Sanitize request/response content
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            // Wrap the request to sanitize the JSON payload
            XSSRequestWrapper wrappedRequest = new XSSRequestWrapper((HttpServletRequest) request);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    public static class XSSRequestWrapper extends HttpServletRequestWrapper {

        private String sanitizedBody;

        public XSSRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            // Read the original JSON body
            String originalBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            // Sanitize the JSON body
            this.sanitizedBody = sanitizeJson(originalBody);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sanitizedBody.getBytes(StandardCharsets.UTF_8))));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sanitizedBody.getBytes(StandardCharsets.UTF_8));
            return new CustomServletInputStream(byteArrayInputStream);
        }

        private String sanitizeJson(String json) {
            // Parse and sanitize the JSON
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                //map.replaceAll((key, value) -> value instanceof String ? XSSSanitizer.sanitize((String) value) : value);
                XSSSanitizer.sanitizeMap(map);
                return objectMapper.writeValueAsString(map);
            } catch (Exception e) {
                return json; // Return the original JSON if parsing fails
            }
        }

        private static class XSSSanitizer {

            public static void sanitizeMap(Map<String, Object> map) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();

                    // If the value is a String, sanitize it
                    if (value instanceof String) {
                        entry.setValue(XSSSanitizer.sanitize((String) value));
                    }
                    // If the value is a Map, recursively sanitize it
                    else if (value instanceof Map) {
                        sanitizeMap((Map<String, Object>) value);
                    }
                    // If the value is a List, recursively sanitize each element
                    else if (value instanceof List) {
                        sanitizeList((List<Object>) value);
                    }
                }
            }

            private static String sanitize(String input) {
                if (input == null) {
                    return null;
                }
                // Use Jsoup to clean the input
                Cleaner cleaner = new Cleaner(Safelist.none());
                return cleaner.clean(Jsoup.parse(input)).text();
            }

            private static void sanitizeList(List<Object> list) {
                for (int i = 0; i < list.size(); i++) {
                    Object value = list.get(i);

                    // If the element is a String, sanitize it
                    if (value instanceof String) {
                        list.set(i, XSSSanitizer.sanitize((String) value));
                    }
                    // If the element is a Map, recursively sanitize it
                    else if (value instanceof Map) {
                        sanitizeMap((Map<String, Object>) value);
                    }
                    // If the element is a List, recursively sanitize each element
                    else if (value instanceof List) {
                        sanitizeList((List<Object>) value);
                    }
                }
            }
        }

        private static class CustomServletInputStream extends ServletInputStream {

            private final ByteArrayInputStream byteArrayInputStream;

            public CustomServletInputStream(ByteArrayInputStream byteArrayInputStream) {
                this.byteArrayInputStream = byteArrayInputStream;
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // No-op: Can be left unimplemented for basic use cases
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        }
    }
}
