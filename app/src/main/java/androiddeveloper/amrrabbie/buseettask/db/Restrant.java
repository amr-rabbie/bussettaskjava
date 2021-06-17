package androiddeveloper.amrrabbie.buseettask.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "restrant_table")
public class Restrant {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "restrant_name")
    private String name;
    private String type;
    private String img;
    private String late;
    private String Long;
    private String address;
    @Ignore
    private String rating;

    public Restrant() {
    }

    public Restrant(String name, String type, String img, String late, String aLong, String address, String rating) {
        this.name = name;
        this.type = type;
        this.img = img;
        this.late = late;
        Long = aLong;
        this.address = address;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLate() {
        return late;
    }

    public void setLate(String late) {
        this.late = late;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
