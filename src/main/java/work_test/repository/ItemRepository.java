package work_test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import work_test.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}