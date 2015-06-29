package wappi.iRoadCommunityPolicing.DatabaseModals;

/**
 * Created by Ilakoze on 5/3/2015.
 */
public class HealthTip extends Modal {

    private int health_tip_id;
    private String title_en;
    private String title_sw;
    private String description_sw;
    private String description_en;
    private int min_age;
    private int max_age;
    private String gender;
    private String poster;

    public int getHealth_tip_id() {
        return health_tip_id;
    }

    public void setHealth_tip_id(int health_tip_id) {
        this.health_tip_id = health_tip_id;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getTitle_sw() {
        return title_sw;
    }

    public void setTitle_sw(String title_sw) {
        this.title_sw = title_sw;
    }

    public String getDescription_sw() {
        return description_sw;
    }

    public void setDescription_sw(String description_sw) {
        this.description_sw = description_sw;
    }

    public String getDescription_en() {
        return description_en;
    }

    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }

    public int getMin_age() {
        return min_age;
    }

    public void setMin_age(int min_age) {
        this.min_age = min_age;
    }

    public int getMax_age() {
        return max_age;
    }

    public void setMax_age(int max_age) {
        this.max_age = max_age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
