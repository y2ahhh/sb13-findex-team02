package com.findex.team02.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

    // EntityManager는 데이터베이스와 통신하는 핵심 객체
    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory queryFactory() {
        // QueryDSL은 이 객체를 통해 타입 안전한 SQL(JPQL) 쿼리를 작성함
        return new JPAQueryFactory(em);
    }

}