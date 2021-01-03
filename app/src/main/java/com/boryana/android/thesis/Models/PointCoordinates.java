package com.boryana.android.thesis.Models;

/**
 * Created by Boryana on 7/18/2016.
 */
public class PointCoordinates {

    private Double coord_x;
    private Double coord_y;
    private String rec_date;

    public PointCoordinates(Double coord_x, Double coord_y, String rec_date) {
        this.coord_x = coord_x;
        this.coord_y = coord_y;
        this.rec_date = rec_date;
    }

    public Double getCoord_x() { return coord_x; }

    public void setCoord_x(Double coord_x) {
        this.coord_x = coord_x;
    }

    public Double getCoord_y() {
        return coord_y;
    }

    public void setCoord_y(Double coord_y) {
        this.coord_y = coord_y;
    }

    public String getRec_date() { return rec_date; }

    public void setRec_date(String rec_date) { this.rec_date = rec_date; }

    public double distanceToMe(double lat2, double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - coord_x);
        Double lonDistance = Math.toRadians(lon2 - coord_y);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(coord_x)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        int distanceInMeters =  (int)Math.sqrt(distance);
        return distanceInMeters/1000.0;
    }
}
