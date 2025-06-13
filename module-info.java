module My.Favourite.Dish {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.example.my_favourite_dish to javafx.fxml;
    exports com.example.my_favourite_dish;
}
