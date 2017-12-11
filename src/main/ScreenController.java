package main;

import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.util.HashMap;

public class ScreenController extends StackPane {

    private HashMap<String, Node> screens = new HashMap<>();

    public void addScreen(String name, Node screen){
        screens.put(name, screen);
    }

    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            ControlledScreen myScreenControler = ((ControlledScreen) myLoader.getController());
            myScreenControler.setScreenParent(this);
            addScreen(name, loadScreen);
            System.out.println(name);
            return true;
        } catch (LoadException e){
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("Error load: "+ name);
            System.out.println(e.toString());
            return false;
        }
    }

    public boolean setScreen(final String name) {

        if (screens.get(name) != null) { //screen loaded
//            final DoubleProperty opacity = opacityProperty();

            //Is there is more than one screen
            if (!getChildren().isEmpty()) {
//                Timeline fade = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)), new KeyFrame(new Duration(500), new EventHandler() {
//                    @Override
//                    public void handle(Event event) {
//                        //remove displayed screen
//                        getChildren().remove(0);
//                        //add new screen
//                        getChildren().add(0, screens.get(name));
//                        Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0)));
//                        fadeIn.play();
//                    }
//                }, new KeyValue(opacity, 0.0)));
                //remove displayed screen
                getChildren().remove(0);
                //add new screen
                getChildren().add(0, screens.get(name));
//                fade.play();
            } else {
                //no one else been displayed, then just show
//                setOpacity(0.0);
                getChildren().add(screens.get(name));
//                Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(500), new KeyValue(opacity, 1.0)));
//                fadeIn.play();
            }
            return true;
        } else {
            System.out.println("screen hasn't been loaded!\n");
            return false;
        }
    }

    public boolean unloadScreen(String name) {
        if(screens.remove(name) == null) {
            System.out.println("Screen didn't exist");
            return false;
        } else {
            return true;
        }
    }
}
