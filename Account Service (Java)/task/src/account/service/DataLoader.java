package account.service;

import account.entity.Role;
import account.repository.GroupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {
    private EntityManager entityManager;
    private final GroupRepository groupRepository;

    @Autowired
    public DataLoader(EntityManagerFactory entityManagerFactory, GroupRepository groupRepository) {
        this.entityManager = entityManagerFactory.createEntityManager();
        this.groupRepository = groupRepository;
    }

    @Override
    public void run(String... args) {
        createRoles();
    }

    private void createRoles() {
        entityManager.getTransaction().begin();

        List<String> rolesNames = groupRepository.findAll().stream()
                .map(x-> x.getRoleName())
                .collect(Collectors.toList());

        if(!rolesNames.contains("ROLE_ADMINISTRATOR")){
            Role gr1 = new Role();
            gr1.setRoleName("ROLE_ADMINISTRATOR");
            entityManager.persist(gr1);
        }

        if(!rolesNames.contains("ROLE_USER")){
            Role gr2 = new Role();
            gr2.setRoleName("ROLE_USER");
            entityManager.persist(gr2);
        }

        if(!rolesNames.contains("ROLE_ACCOUNTANT")){
            Role gr3 = new Role();
            gr3.setRoleName("ROLE_ACCOUNTANT");
            entityManager.persist(gr3);
        }

        if(!rolesNames.contains("ROLE_AUDITOR")){
            Role gr4 = new Role();
            gr4.setRoleName("ROLE_AUDITOR");
            entityManager.persist(gr4);
        }

        entityManager.getTransaction().commit();
        entityManager.clear();
    }
}
