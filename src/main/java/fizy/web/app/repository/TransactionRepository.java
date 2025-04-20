package fizy.web.app.repository;
import fizy.web.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    // Additional query methods can be defined here if needed
}
