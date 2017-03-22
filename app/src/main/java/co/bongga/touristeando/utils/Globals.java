package co.bongga.touristeando.utils;

import java.util.ArrayList;

import co.bongga.touristeando.models.ChatMessage;
import co.bongga.touristeando.models.Event;
import co.bongga.touristeando.models.Place;
import co.bongga.touristeando.models.User;

/**
 * Created by bongga on 1/17/17.
 */

public class Globals {
    public static Event currentEvent;
    public static Place currentPlace;
    public static ArrayList<ChatMessage> chatItems = new ArrayList<>();
    public static User loggedUser = null;
}
