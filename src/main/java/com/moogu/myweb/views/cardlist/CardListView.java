package com.moogu.myweb.views.cardlist;

import com.moogu.myweb.model.PersonCard;
import com.moogu.myweb.views.main.MainView;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;


@Route(value = "card-list", layout = MainView.class)
@PageTitle("Card List")
@CssImport(value = "styles/views/cardlist/card-list-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class CardListView extends Div implements AfterNavigationObserver {

    public static final String SPACING = "spacing-s";
    public static final String VAADIN = "vaadin";
    public static final String POST = "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document without relying on meaningful content (also called greeking).";
    Grid<PersonCard> grid = new Grid<>();

    public CardListView() {
        setId("card-list-view");
        addClassName("card-list-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(this::createCard);
        add(grid);
    }

    private HorizontalLayout createCard(PersonCard personCard) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add(SPACING);

        Image image = new Image();
        image.setSrc(personCard.getImage());
        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add(SPACING);

        Span name = new Span(personCard.getName());
        name.addClassName("name");
        Span date = new Span(personCard.getDate());
        date.addClassName("date");
        header.add(name, date);

        Span post = new Span(personCard.getPost());
        post.addClassName("post");

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("actions");
        actions.setSpacing(false);
        actions.getThemeList().add(SPACING);

        IronIcon likeIcon = new IronIcon(VAADIN, "heart");
        Span likes = new Span(personCard.getLikes());
        likes.addClassName("likes");
        IronIcon commentIcon = new IronIcon(VAADIN, "comment");
        Span comments = new Span(personCard.getComments());
        comments.addClassName("comments");
        IronIcon shareIcon = new IronIcon(VAADIN, "connect");
        Span shares = new Span(personCard.getShares());
        shares.addClassName("shares");

        actions.add(likeIcon, likes, commentIcon, comments, shareIcon, shares);

        description.add(header, post, actions);
        card.add(image, description);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Set some data when this view is displayed.
        List<PersonCard> personCards = Arrays.asList( //
                createPerson("https://randomuser.me/api/portraits/men/42.jpg", "John Smith", "May 8", POST, "1K", "500", "20"),
                createPerson("https://randomuser.me/api/portraits/women/42.jpg", "Abagail Libbie", "May 3", POST, "1K", "500", "20"),
                createPerson("https://randomuser.me/api/portraits/men/24.jpg", "Alberto Raya", "May 3", POST, "1K", "500", "20")
        );

        grid.setItems(personCards);
    }

    private static PersonCard createPerson(String image, String name, String date, String post, String likes,
                                           String comments, String shares) {
        PersonCard p = new PersonCard();
        p.setImage(image);
        p.setName(name);
        p.setDate(date);
        p.setPost(post);
        p.setLikes(likes);
        p.setComments(comments);
        p.setShares(shares);

        return p;
    }

}
