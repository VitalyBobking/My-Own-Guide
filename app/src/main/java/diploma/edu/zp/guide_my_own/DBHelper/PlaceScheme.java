package diploma.edu.zp.guide_my_own.DBHelper;

/**
 * Created by Val on 2/17/2017.
 */

public enum PlaceScheme {
    _ID(0), ID(1), TITLE(2), DESCRIPTION(3), URL_PIC(4), LATITUDE(5), LONGITUDE(6), CITY(7);

    private int intValue;

    PlaceScheme(int i) {
        intValue = i;
    }

    public int getIndex() {
        return intValue;
    }
}
