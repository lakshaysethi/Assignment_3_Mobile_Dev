package com.mobileassignment3.parcel_tracking_app.model_classes;

public class Parcel {
    final int DELIVERED = 1;
    final int NOT_DELIVERED = 0;

    final int BOX = 1001;
    final int LETTER = 1002;
    final int OTHER =1003;

    int type;// can be letter or  box small or big or other
    int status;
    String description;// sender's description of contents or notes

    public Parcel(int type, String description) {
        this.type = type;
        this.description = description;
        this.status = NOT_DELIVERED;
    }
    public Parcel( String description) {
        this.type = BOX;
        this.description = description;
        this.status = NOT_DELIVERED;
    }

    public Parcel() {
    }

    public int getType() {
        return type;
    }

    public String getTypeString() {
        if (type == BOX){
            return "BOX";
        }else if (type == LETTER){
            return "LETTER";
        }else {
            return "OTHER size";
        }

    }

    public int getDELIVERED() {
        return DELIVERED;
    }

    public int getNOT_DELIVERED() {
        return NOT_DELIVERED;
    }

    public int getBOX() {
        return BOX;
    }

    public int getLETTER() {
        return LETTER;
    }

    public int getOTHER() {
        return OTHER;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "type='" + type + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
