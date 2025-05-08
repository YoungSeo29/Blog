package com.example.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import io.github.cdimascio.dotenv.Dotenv;

@EnableJpaAuditing
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {

        // .env 파일 로딩
        Dotenv dotenv = Dotenv.load();

        // 환경변수로 등록
        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(BlogApplication.class, args);
    }

}
