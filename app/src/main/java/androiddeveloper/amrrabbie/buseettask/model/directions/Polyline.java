package androiddeveloper.amrrabbie.buseettask.model.directions;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Polyline implements Serializable {

	@SerializedName("points")
	private String points;

	public void setPoints(String points){
		this.points = points;
	}

	public String getPoints(){
		return points;
	}

	@Override
 	public String toString(){
		return 
			"Polyline{" + 
			"points = '" + points + '\'' + 
			"}";
		}
}