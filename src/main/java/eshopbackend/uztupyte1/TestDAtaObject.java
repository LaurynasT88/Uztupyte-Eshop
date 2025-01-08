package eshopbackend.uztupyte1;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestDAtaObject {

    @Id
    private long id;
    @Column
    private String username;
}
