package util.guice;

import com.google.inject.persist.PersistService;

import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("checkstyle:MissingJavadocType")
@Singleton
public class JpaInitializer {

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    @Inject
    public JpaInitializer (PersistService persistService) {
        persistService.start();
    }

}
