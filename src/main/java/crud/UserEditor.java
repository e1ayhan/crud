package crud;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import crud.entities.User;
import crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by e1ayhan on 24.06.2017.
 */
@SpringComponent
@UIScope
public class UserEditor extends VerticalLayout {
    private final UserRepository repository;
    private User editUser;


    TextField name = new TextField("name");
    TextField age = new TextField("age");


    CheckBox isAdmin = new CheckBox("isAdmin");

    Button save = new Button("Save", FontAwesome.SAVE);
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", FontAwesome.TRASH_O);
    CssLayout actions = new CssLayout(save, cancel, delete);

    Binder<User> binder = new Binder<>(User.class);

    @Autowired
    public UserEditor(UserRepository repository) {
        this.repository = repository;

        addComponents(name, age, isAdmin, actions);


//        binder.bindInstanceFields(this);
        binder.forField(name).withValidator(s -> s.length() <= 25,
                "Имя слишком большое!").bind(User::getName, User::setName);
        binder.forField(age).withConverter(Integer::valueOf, String::valueOf,
                "Введите число!").bind(User::getAge, User::setAge);
        binder.forField(isAdmin).bind(User::isAdmin, User::setAdmin);
        isAdmin.getValue();

        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        save.addClickListener(clickEvent -> repository.save(editUser));
        delete.addClickListener(clickEvent -> repository.delete(editUser));
        cancel.addClickListener(clickEvent -> editUser(editUser));
        setVisible(false);

    }


    public final void editUser(User user) {
        if (user == null) {
            setVisible(false);
            return;
        }

        final boolean persisted = user.getId() != null;

        if (persisted) {
            editUser = repository.findOne(user.getId());
        } else {
            editUser = user;
        }
        cancel.setVisible(persisted);

        binder.setBean(editUser);
        setVisible(true);

        save.focus();
        name.selectAll();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler h) {
        save.addClickListener(clickEvent -> h.onChange());
        delete.addClickListener(clickEvent -> h.onChange());
    }

}
