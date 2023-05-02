package setting.settingAbout;

import common.dataAdd.DataAddController;
import dbUtil.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable, DataAddController {

    @FXML
    private Button closeButton;
    @FXML
    private VBox setScene;

    private dbConnection dc;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
    }

    public void confirmAdd(ActionEvent Event) throws Exception{

    };

    public void createData() throws Exception{

    };

    public void closeWindow() throws Exception{
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
    };
}
