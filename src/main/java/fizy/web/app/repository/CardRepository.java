package fizy.web.app.repository;
import fizy.web.app.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, String> {
    // Additional query methods can be defined here if needed
}
