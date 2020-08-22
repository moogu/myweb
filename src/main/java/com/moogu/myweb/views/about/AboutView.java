package com.moogu.myweb.views.about;

import com.moogu.myweb.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "about", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("About")
@CssImport("styles/views/about/about-view.css")
public class AboutView extends HorizontalLayout {

    private final TextField name;

    public AboutView() {
        setId("about-view");
        name = new TextField("Your name");
        Button sayHello = new Button("Say hello");
        add(name, sayHello);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);
        sayHello.addClickListener(e -> Notification.show("Hello " + name.getValue()));
    }
}
