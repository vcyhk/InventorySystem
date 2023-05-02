package setting.settingChangeProPic;

import common.AlertBox;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.*;
import loginapp.LoginController;
import setting.SettingController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ChangeProPicController implements Initializable {
    Connection conn;
    @FXML
    private ImageView icon;
    @FXML
    private Button submitBtn, selectBtn;
    @FXML
    private Label filename;
    @FXML
    private VBox setScene;
    public Image im;
    public File fileImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hotKey();
    }

    public void selectFile(ActionEvent Event) throws Exception {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        String userDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userDir +"/Desktop"));
        File selectedDirectory = fileChooser.showOpenDialog(stage);
        if(selectedDirectory == null){
            //No Directory selected
        }else {
            String fileTypeName = selectedDirectory.getName();
            String[] parts = fileTypeName.split(Pattern.quote("."));
            String part2 = parts[1];
            if (part2.toLowerCase().equals("png") || part2.toLowerCase().equals("jpg") || part2.toLowerCase().equals("jpeg")
                || part2.toLowerCase().equals("jpe") || part2.toLowerCase().equals("jfif") ){
                filename.setText(fileTypeName);
                fileImage = new File(String.valueOf(selectedDirectory));
                im = new Image("file:///"+String.valueOf(selectedDirectory));
                icon.setImage(im);

            }
            else{
                AlertBox.informationBox("Wrong file format. Allowed: PNG, JPEG.");
            }
        }
    }

    public void submitPhoto(ActionEvent Event) throws Exception {
        changePhoto();
    }

    public void changePhoto() throws Exception{
        if (fileImage!=null){
            BufferedImage bImage = null;
            try {
                bImage = ImageIO.read(fileImage);
                ImageIO.write(bImage, "png", new File("src/resources/stafficon/"+ LoginController.nameS+".png"));
            } catch (IOException e) {
                System.out.println("Exception occured :" + e.getMessage());
            }
            //AlertBox.informationBox("Images were written succesfully.");
            closeWindow();
        }else{
            AlertBox.informationBox("You doesn't select the photo");
        }
    }

    public void closeWindow() throws Exception {
        SettingController promotionController = SettingController.getInstance();
        promotionController.loadNewIcon();
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }

    public void hotKey(){
        setScene.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                try {
                    if (event.getCode() == KeyCode.ENTER) {
                        changePhoto();
                    }
                    if(event.getCode() == KeyCode.ESCAPE){
                        closeWindow();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
