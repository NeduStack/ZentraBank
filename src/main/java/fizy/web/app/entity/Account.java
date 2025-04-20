package fizy.web.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountId;

    private BigDecimal balance;

    private String accountName;

    @Column(nullable = false, unique = true)
    private long accountNumber;

    private String currency;

    private String code;

    private String label;

    private char symbol;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String status;

    private String type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;


    //Relationships
    //Relationship to Transaction
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    //Relationship to user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}