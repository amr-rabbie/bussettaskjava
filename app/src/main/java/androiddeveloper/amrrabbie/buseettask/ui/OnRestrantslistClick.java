package androiddeveloper.amrrabbie.buseettask.ui;

import androiddeveloper.amrrabbie.buseettask.db.Restrant;
import androiddeveloper.amrrabbie.buseettask.model.ResultsItem;

public interface OnRestrantslistClick {
    void onListClick(ResultsItem item);
    void onListOfflineClick(Restrant restrant);
}
