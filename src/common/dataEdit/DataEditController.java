package common.dataEdit;

import javafx.event.ActionEvent;
public interface DataEditController {

    public void confirmEdit(ActionEvent Event) throws Exception;
    //public void inflateUI(PageData selectedForEdit);
    public void updateInfo() throws Exception;
    public void closeWindow() throws Exception;

}
