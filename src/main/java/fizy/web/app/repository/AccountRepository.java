package fizy.web.app.repository;
import fizy.web.app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
    // Additional query methods can be defined here if needed

}
