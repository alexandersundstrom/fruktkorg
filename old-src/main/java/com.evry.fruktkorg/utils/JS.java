package com.evry.fruktkorg.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class JS {
    private static ObjectMapper mapper = new ObjectMapper();
//    static {
//        mapper.registerModule(new Jdk8Module());
//    }

    private JS() {
        //NO create, only static...
    }

//    public static ResponseEntity<JsonNode> message(HttpStatus status, String message, Object... args) {
//        return ResponseEntity.status(status).body(JS.message(String.format(message, args)));
//    }

//    public static ResponseEntity<JsonNode> message(HttpStatus status, Optional<?> message) {
//        return message.isPresent() ? ResponseEntity.status(status).body(JS.parse(message.get()))
//                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(JS.parse("Not Present"));
//    }

    public static ResponseEntity<JsonNode> message(HttpStatus status, Object o) {
        if(o instanceof String) {
            o = message((String) o);
        }
        return ResponseEntity.status(status).body(JS.parse(o));
    }

    public static JsonNode message(String message) {
        return parse(new JsonMessage(message));
    }

    private static JsonNode parse(Object o) {
        return mapper.valueToTree(o);
    }

    public static class JsonMessage {
        private String message;

        public JsonMessage() {
        }

        private JsonMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
