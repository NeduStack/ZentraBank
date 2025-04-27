package fizy.web.app.repository;
import fizy.web.app.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    boolean existsByCardNumber(double cardNumber);

    Optional<Card> findByOwnerUid(String uid);
    // Additional query methods can be defined here if needed
}
