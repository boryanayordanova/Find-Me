package com.boryana.android.thesis.Models;

/**
 * Created by Boryana on 7/17/2016.
 */
public class UsersItem {
    private int userId;
    private String userName;
    private String coor_x;
    private String coor_y;
    private String rec_date;

    private String color;

    public UsersItem(int userId, String userName, String coor_x, String coor_y, String rec_date){
        this.userId = userId;
        this.userName = userName;
        this.coor_x = coor_x; // lat
        this.coor_y = coor_y; // lon
        this.rec_date = rec_date;
    }

    public String getUserName(){ return this.userName; };
    public int getUserId(){ return this.userId; };

    public void setColor(String color){this.color = color;}
    public String getColor() { return this.color;}


    public double distanceToMe(double lat2, double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - Double.parseDouble(coor_x));
        Double lonDistance = Math.toRadians(lon2 - Double.parseDouble(coor_y));
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(Double.parseDouble(coor_x))) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        int distanceInMeters =  (int)Math.sqrt(distance);
        return distanceInMeters/1000.0;
    }

    public Double getCoor_y() {
        if(coor_y.toLowerCase().equals("null")){
            return null;
        } else
            return Double.parseDouble(coor_y);
    }

    public Double getCoor_x() {
        if(coor_x.toLowerCase().equals("null")){
            return null;
        } else
            return Double.parseDouble(coor_x);
    }

    public String getRec_date() {
        return rec_date;
    }

    public void setRec_date(String rec_date) {
        this.rec_date = rec_date;
    }









    /*
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point
     * lat2, lon2 End point
     * el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
//    public double distanceToMe(double lat1, double lat2, double lon1,
//                                  double lon2, double el1, double el2) { // el1 and el0 will be 0
//
//        final int R = 6371; // Radius of the earth
//
//        Double latDistance = Math.toRadians(lat2 - lat1);
//        Double lonDistance = Math.toRadians(lon2 - lon1);
//        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = R * c * 1000; // convert to meters
//
//        double height = el1 - el2;
//
//        distance = Math.pow(distance, 2) + Math.pow(height, 2);
//
//        return Math.sqrt(distance);
//    }

//    public void setMakedState(boolean state){ this.isMarked = state;}
//    public boolean getMarkedState(){ return this.isMarked;}

//    @Override
//    public String toString(){
//        return ""+this.userId+";";
//    }
}
