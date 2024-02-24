package account.repository;

import account.entity.Role;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
public interface GroupRepository extends CrudRepository<Role, Integer> {

    Role save(Role entity);

    List<Role> findAllByRoleName(String roleName);

    List<Role> findAll();
}
