package com.type.multi_typer.dto;

public class QuoteResponse {
    private Quote quote;

    public static class Quote {
        private String id;
        private String content;
        private String author;

        public String getContent() {
            return content;
        }
    }

    public static class Author {
        private String id;
        private String name;
        private String link;
    }

    public Quote getQuote() {
        return quote;
    }
}
