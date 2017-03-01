package co.bongga.touristeando.utils;

/**
 * Created by spval on 15/01/2017.
 */

public class Constants {
    //Generals
    public static final String BASE_OS = "Android SDK";
    public static final String API_AI_KEY = "416855e204a74ea4813328dab1cae292";
    public static final int DESCRIPTION_LENGHT = 150;

    public static final String API_BASE_URL = "https://touristeando.herokuapp.com/api/";
    public static final String API_SODA_URL = "https://www.datos.gov.co/resource/";
    public static final String DEFAULT_HEADER_IMAGE = "http://bongga.co/global_res/images/nav_menu_header_bg.jpg";
    public static final String DEFAULT_PROFILE_IMAGE = "http://bongga.co/global_res/images/logo.png";

    //Actions
    public static final String ATTRACTIONS_ACTION = "attractions";
    public static final String PLACES_ACTION = "places";
    public static final String HELP_ACTION = "help";

    //Action types
    public static final String SEARCH_ACTION_TYPE = "search";
    public static final String BOOKING_ACTION_TYPE = "book";

    //Notifications Channels
    public static final String GENERAL_TOPIC = "general";
    public static final String OFFER_TOPIC = "offer";
    public static final String TEST_TOPIC = "test";

    //Request codes
    public static final int REQUEST_RECORD_PERMISSION = 1;
    public static final int REQUEST_COARSE_LOCATION_PERMISSION = 2;
    public static final int REQUEST_FINE_LOCATION_PERMISSION = 3;
    public static final int REQUEST_CHECK_SETTINGS = 4;
    public static final int REQUEST_CALL_PHONE_PERMISSION = 5;

    //Things to do
    public static final String WIFI_THING = "conseguir wifi gratis";
    public static final int WIFI_POINTS_LIMIT = 1000;
}
