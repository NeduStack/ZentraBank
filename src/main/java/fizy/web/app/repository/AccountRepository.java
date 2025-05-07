package fizy.web.app.repository;
import fizy.web.app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByAccountNumber(long accountNumber);

    boolean existsByCodeAndOwnerUid(String code, String uid);

    List<Account> findAllByOwnerUid(String uid);

    Optional<Account> findByCodeAndOwnerUid(String code, String uid);

    Optional<Account> findByAccountNumber(long recipientAccountNumber);

    // Additional query methods can be defined here if needed


}
