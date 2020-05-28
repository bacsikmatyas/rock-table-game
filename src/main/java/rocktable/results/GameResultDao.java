package rocktable.results;

import com.google.inject.persist.Transactional;
import util.jpa.GenericJpaDao;

import java.util.List;

/**
 * DAO class for the {@link GameResult} entity.
 */
public class GameResultDao extends GenericJpaDao<GameResult> {

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public GameResultDao() {
        super(GameResult.class);
    }

    /**
     * Returns the list of {@code n} best results with respect to the time
     * spent for finishing the game.
     *
     * @param n the maximum number of results to be returned
     * @return the list of {@code n} best results with respect to the time
     * spent for finishing the game
     */
    @Transactional
    public List<GameResult> findBest(int n) {
        return entityManager.createQuery("SELECT r FROM GameResult r ORDER BY r.duration ASC, r.created DESC", GameResult.class)
                .setMaxResults(n)
                .getResultList();
    }

}
