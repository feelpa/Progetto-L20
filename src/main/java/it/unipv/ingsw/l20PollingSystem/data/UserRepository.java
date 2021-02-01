package it.unipv.ingsw.l20PollingSystem.data;

import it.unipv.ingsw.l20PollingSystem.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByUserId(int userId);
    User findByUsername(String username);
    User findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
