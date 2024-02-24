package account.repository;

import account.entity.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Integer>{
    Optional<AppUser> findAppUserByEmail(String email);

    @Query(nativeQuery = true,
            value = "SELECT * FROM APP_USER ORDER BY id")
    List<AppUser> findAll();

    @Query(nativeQuery = true,
            value = "SELECT * FROM APP_USER WHERE EMAIL IN (?1)")
    List<AppUser> findAllByEmail(List<String> emails);

    @Override
    long count();

    @Override
    void delete(AppUser entity);
}