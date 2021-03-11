package ru.zimina.myrestassuredproject.newsapi.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TeslaArticlesClass {

    @JsonProperty("status")
    String status;

    @JsonProperty("totalResults")
    Integer totalResults;

    @JsonProperty("articles")
    List<Articles> articles;


    @JsonDeserialize(contentAs=Articles.class)
    class Articles {
        @JsonProperty("source")
        List<Source> source;

        @JsonProperty("author")
        String author;

        @JsonProperty("title")
        String title;

        @JsonProperty("description")
        String description;

        @JsonProperty("url")
        String url;

        @JsonProperty("urlToImage")
        String urlToImage;

        @JsonProperty("publishedAt")
        String publishedAt;

        @JsonProperty("content")
        String content;

        @JsonDeserialize(contentAs=Source.class)
        class Source{
            @JsonProperty ("id")
            String id;

            @JsonProperty("name")
            String name;

        }
    }



}
