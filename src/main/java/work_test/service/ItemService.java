package work_test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work_test.model.Item;
import work_test.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

}
