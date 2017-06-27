package crud;

import com.vaadin.annotations.Theme;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import crud.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import crud.impl.UserService;
import crud.repository.UserRepository;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by pozar on 23.06.2017.
 */

@SpringUI
@Theme("valo")
public class MyUI extends UI {
    private final UserRepository repository;
    private final UserEditor editor;


    @Autowired
    private final UserService service;

    final Grid<User> grid;

    final TextField filter;

    private final Button addNewBtn;

    @Autowired
    public MyUI(UserRepository repository, UserEditor editor, UserService service) {
        this.repository = repository;
        this.editor = editor;
        this.service = service;
        this.grid = new Grid<>(User.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New user", FontAwesome.PLUS);
    }

    @Override
    protected void init(VaadinRequest request) {
        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid);
        HorizontalLayout editorLayout = new HorizontalLayout(mainLayout, editor);
        setContent(editorLayout);

        grid.setHeight(400, Unit.PIXELS);
        grid.setWidth(650, Unit.PIXELS);

        //Ленивая загрузка Vaadin8 by GitHub alejandro-du/lazy-loading-spring-demo
        grid.setDataProvider(
                (sortOrders, offset, limit) -> {
                    Map<String, Boolean> sortOrder = sortOrders.stream()
                            .collect(Collectors.toMap(
                                    QuerySortOrder::getSorted,
                                    sort -> sort.getDirection() == SortDirection.ASCENDING));

                    return service.findAll(offset, limit, sortOrder).stream();

                }, service::count
        );


        filter.setPlaceholder("Поиск по имени");


        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listUsers(e.getValue()));

        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editUser(e.getValue());
        });

        addNewBtn.addClickListener(e -> {
            editor.editUser(new User("user", 0, false));
        });

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listUsers(filter.getValue());
        });
    }

    private void listUsers(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setDataProvider(
                    (sortOrders, offset, limit) -> {
                        Map<String, Boolean> sortOrder = sortOrders.stream()
                                .collect(Collectors.toMap(
                                        QuerySortOrder::getSorted,
                                        sort -> sort.getDirection() == SortDirection.ASCENDING));

                        return service.findAll(offset, limit, sortOrder).stream();
                    },
                    service::count
            );
        } else {
            grid.setItems(service.findByName(filterText));
        }
    }
}
