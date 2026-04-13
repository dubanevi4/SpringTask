package com.ehu.javacafe;

import com.ehu.javacafe.repository.BeverageRepository;
import com.ehu.javacafe.repository.impl.JSONBeverageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class SpringRepositoryWiringTest {

    @Test
    void primaryBeverageRepositoryIsJsonRepository() {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MainAnnotation.class)) {
            BeverageRepository repository = ctx.getBean(BeverageRepository.class);
            assertNotNull(repository);
            assertInstanceOf(JSONBeverageRepository.class, repository);
        }
    }
}
