package com.stefanlippl.hangover.locations.rating;


public class RatingCard implements Comparable<RatingCard> {

    private float rating;
    private String text;
    private String date;

    RatingCard(float rating, String text, String date) {
        this.rating = rating;
        this.text = text;
        this.date = date;
    }

    public float getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    /**
     * @param another ratingCard to comapre to
     * @return newest ratingCard first
     */
    @Override
    public int compareTo(RatingCard another) {
        char[] thisDate = date.toCharArray();
        char[] otherDate = another.getDate().toCharArray();

        String thisYear = String.format("%c%c%c%c", thisDate[6], thisDate[7], thisDate[8], thisDate[9]);
        String otherYear = String.format("%c%c%c%c", otherDate[6], otherDate[7], otherDate[8], otherDate[9]);

        String thisMonth = String.format("%c%c", thisDate[3], thisDate[4]);
        String otherMonth = String.format("%c%c", otherDate[3], otherDate[4]);

        String thisDay = String.format("%c%c", thisDate[0], thisDate[1]);
        String otherDay = String.format("%c%c", otherDate[0], otherDate[1]);

        if (!thisYear.equals(otherYear)) {
            return thisYear.compareTo(otherYear)*-1;
        } else if (!thisMonth.equals(otherMonth)) {
            return thisMonth.compareTo(otherMonth)*-1;
        } else {
            return thisDay.compareTo(otherDay)*-1;
        }
    }
}
