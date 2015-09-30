/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileHandleUtil;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author leon
 */
public class FXMLHandle
{
    public static <T> T loadFXML(T controller,String source, Stage stage) throws IOException
    {
        //load the FXML and get access to its controller
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = controller.getClass().getResource(source);
        fxmlLoader.setLocation(url);
        Parent root = (Parent) fxmlLoader.load(url.openStream());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        return fxmlLoader.getController();
    }
}