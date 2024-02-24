package account.repository;

import account.entity.Event;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface EventRepository extends CrudRepository<Event, Integer> {

    public Event save(Event entity);
    public List<Event> findAllByOrderById();
}