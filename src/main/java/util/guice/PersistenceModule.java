package util.guice;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

@SuppressWarnings("checkstyle:MissingJavadocType")
public class PersistenceModule extends AbstractModule {

    private String jpaUnit;

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public PersistenceModule(String jpaUnit) {
        this.jpaUnit = jpaUnit;
    }

    @Override
    protected void configure() {
        install(new JpaPersistModule(jpaUnit));
        bind(JpaInitializer.class).asEagerSingleton();
    }

}
