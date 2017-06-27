package crud.repository;

import crud.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by pozar on 23.06.2017.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByName(String name);

    List<User> findByAge(int age);

    List<User> findByAdmin(boolean isAdmin);
}
