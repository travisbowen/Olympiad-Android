package upscaleapps.olympiad;

public class User {
    String email;
    String name;
    String location;
    String gender;
    String age;
    String motivation;
    String skill;
    String reason;
    String rateing;
    String image;
    String time;
    Double longitude;
    Double latitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public User() {
    }
    public String getAge() {
        return age;
    }
    public String getName() {
        return name;
    }
    public String getLocation() {
        return location;
    }
    public String getEmail() {
        return email;
    }
    public String getGender() {
        return gender;
    }
    public String getTime() {
        return time;
    }

    public String getMotivation(){
        return motivation;
    }
    public String getReason(){
        return reason;
    }
    public String getSkill(){
        return skill;
    }
    public String getRateing(){
        return rateing;
    }
    public String getImage(){
        return image;
    }
}
