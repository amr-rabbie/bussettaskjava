package androiddeveloper.amrrabbie.buseettask.model.directions;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StartLocation implements Serializable {

	@SerializedName("lng")
	private double lng;

	@SerializedName("lat")
	private double lat;

	public void setLng(double lng){
		this.lng = lng;
	}

	public double getLng(){
		return lng;
	}

	public void setLat(double lat){
		this.lat = lat;
	}

	public double getLat(){
		return lat;
	}

	@Override
 	public String toString(){
		return 
			"StartLocation{" + 
			"lng = '" + lng + '\'' + 
			",lat = '" + lat + '\'' + 
			"}";
		}
}