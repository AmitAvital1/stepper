package menu.methods.api;

import menu.user.input.UserInputContext;

public interface MenuMethod {

    void invoke(UserInputContext context);
    void print();
}
