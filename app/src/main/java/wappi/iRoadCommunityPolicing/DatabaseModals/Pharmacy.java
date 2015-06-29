package wappi.iRoadCommunityPolicing.DatabaseModals;

/**
 * Created by Ilakoze on 5/2/2015.
 */
public class Pharmacy extends Modal{
    private int pharmacy_id;
    private double latitude;
    private double longitude;
    private String pharmacy_name;
    private String pharmacy_email;
    private String pharmacy_phone_number;
    private String pharmacy_alt_phone;

    public int getId() {
        return pharmacy_id;
    }

    public void setId(int pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
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

    public String getPharmacy_name() {
        return pharmacy_name;
    }

    public void setPharmacy_name(String pharmacy_name) {
        this.pharmacy_name = pharmacy_name;
    }

    public String getPharmacy_email() {
        return pharmacy_email;
    }

    public void setPharmacy_email(String pharmacy_email) {
        this.pharmacy_email = pharmacy_email;
    }

    public String getPharmacy_phone_number() {
        return pharmacy_phone_number;
    }

    public void setPharmacy_phone_number(String pharmacy_phone_number) {
        this.pharmacy_phone_number = pharmacy_phone_number;
    }

    public String getPharmacy_alt_phone() {
        return pharmacy_alt_phone;
    }

    public void setPharmacy_alt_phone(String pharmacy_alt_phone) {
        this.pharmacy_alt_phone = pharmacy_alt_phone;
    }
}
