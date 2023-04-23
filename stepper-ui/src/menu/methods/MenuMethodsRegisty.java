package menu.methods;


import menu.methods.api.MenuMethod;
import menu.methods.impl.back.GoBack;
import menu.methods.impl.loadxml.LoadDataFromXml;
import menu.user.input.UserInputContext;

public enum MenuMethodsRegisty implements MenuMethod {
   // LOAD_XML(new LoadDataFromXml()),
    EXIT(new GoBack())
    ;


    MenuMethodsRegisty(MenuMethod menuMethod) {
        this.menuMethod = menuMethod;
    }
    private final MenuMethod menuMethod;

    @Override
    public void invoke(UserInputContext context) {
        menuMethod.invoke(context);
    }

    @Override
    public void print() {
        menuMethod.print();
    }
}
