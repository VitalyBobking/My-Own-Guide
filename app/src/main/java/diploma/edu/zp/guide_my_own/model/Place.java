package diploma.edu.zp.guide_my_own.model;

import java.io.Serializable;

/**
 * Created by Val on 2/17/2017.
 */

public class Place implements Serializable {
    private int id;
    private String title;
    private String description;
    private String url_pic;
    private double latitude;
    private double longitude;
    private String placeName;
    private int color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl_pic() {
        return url_pic;
    }

    public void setUrl_pic(String url_pic) {
        this.url_pic = url_pic;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
