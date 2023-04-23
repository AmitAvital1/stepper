package menu.methods.api;

import menu.user.input.UserInputContext;

public abstract class AbstractMenuMethod implements MenuMethod{
    protected String userString;

    protected AbstractMenuMethod(String userString){this.userString = userString;}

    public abstract void invoke(UserInputContext context);
    public void print(){
        System.out.println(userString);
    };
}
