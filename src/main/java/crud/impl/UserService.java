package crud.impl;

import crud.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import crud.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service()
@Transactional
public class UserService {


    @Autowired
    private UserRepository repository;

    //Ленивая загрузка Vaadin8
    public List<User> findAll(int offset, int limit, Map<String, Boolean> sortOrders) {
        int page = offset / limit;
        List<Sort.Order> orders = sortOrders.entrySet().stream()
                .map(e -> new Sort.Order(e.getValue() ? Sort.Direction.ASC : Sort.Direction.DESC, e.getKey()))
                .collect(Collectors.toList());

        PageRequest pageRequest = new PageRequest(page, limit, orders.isEmpty() ? null : new Sort(orders));
        List<User> users = repository.findAll(pageRequest).getContent();
        return users.subList(offset % limit, users.size());
    }

    public Integer count() {
        return Math.toIntExact(repository.count());
    }

    //методы поиска и сохранения
    public List<User> findByName(String name) {
        return repository.findByName(name);
    }

    public List<User> findByAdmin(boolean isAdmin) {
        return repository.findByAdmin(isAdmin);
    }

    public List<User> findByAge(int age) {
        return repository.findByAge(age);
    }

    public void saveOrUpdate(User user) {
        repository.save(user);
    }
}
