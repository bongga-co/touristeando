package co.bongga.touristeando.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ai.api.AIServiceException;
import ai.api.android.AIDataService;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import co.bongga.touristeando.R;
import co.bongga.touristeando.adapters.ChatMessageAdapter;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.models.ChatMessage;
import co.bongga.touristeando.models.Coordinate;
import co.bongga.touristeando.models.Help;
import co.bongga.touristeando.models.Place;
import co.bongga.touristeando.models.PublicWiFi;
import co.bongga.touristeando.models.Query;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.DataManager;
import co.bongga.touristeando.utils.Globals;
import co.bongga.touristeando.utils.LocManager;
import co.bongga.touristeando.utils.PreferencesManager;
import co.bongga.touristeando.utils.UtilityManager;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AIListener, View.OnClickListener {
    private RecyclerView chatList;
    private ChatMessageAdapter chatMessageAdapter;

    private EditText rqText;
    private ImageButton btnSpeech;
    private ImageButton btnText;
    private ProgressBar progressBar;
    private ImageView chatLeftAction;

    private AIService aiService;
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    private static HashMap<String, JsonElement> agentParams;

    private Realm db;
    private PreferencesManager preferencesManager;
    private FirebaseDatabase firebaseDb;
    private DatabaseReference databaseReference;

    private LocManager locManager;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupAPIAI();

        db = Realm.getDefaultInstance();
        preferencesManager = new PreferencesManager(getActivity());
        locManager = new LocManager(getActivity());

        firebaseDb = FirebaseDatabase.getInstance();
        databaseReference = firebaseDb.getReference("queries");

        chatMessageAdapter = new ChatMessageAdapter(Globals.chatItems);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (aiService != null) {
            aiService.resume();
        }

        chatMessageAdapter.notifyDataSetChanged();
        setEmptyChat();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (aiService != null) {
            aiService.pause();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.chat_response_progress);

        chatLeftAction = (ImageView) view.findViewById(R.id.chat_left_action);
        chatLeftAction.setColorFilter(ContextCompat.getColor(getActivity(), R.color.standar_second), PorterDuff.Mode.SRC_ATOP);
        chatLeftAction.setOnClickListener(this);

        rqText = (EditText) view.findViewById(R.id.rqText);
        rqText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length() > 0){
                    btnSpeech.setVisibility(View.GONE);
                    btnText.setVisibility(View.VISIBLE);
                }
                else {
                    btnSpeech.setVisibility(View.VISIBLE);
                    btnText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnSpeech = (ImageButton) view.findViewById(R.id.btnSpeech);
        btnSpeech.setOnClickListener(this);

        btnText = (ImageButton) view.findViewById(R.id.btnText);
        btnText.setColorFilter(ContextCompat.getColor(getActivity(), R.color.standar_primary));
        btnText.setOnClickListener(this);

        chatList = (RecyclerView) view.findViewById(R.id.chatList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        chatList.setLayoutManager(layoutManager);
        chatList.setHasFixedSize(true);
        chatList.setItemAnimator(new DefaultItemAnimator());
        chatList.setAdapter(chatMessageAdapter);

        if(preferencesManager.isShownHelpMessage()){
            setHelpMessage();
            preferencesManager.setHelpMessage(false);
        }
        else{
            didGetChatList();
        }

        return view;
    }

    /*
     * Welcome message
     */
    private void setHelpMessage(){
        Spanned chatText;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            chatText = Html.fromHtml(getString(R.string.agent_initial_message), Html.FROM_HTML_MODE_LEGACY);
        }
        else {
            chatText = Html.fromHtml(getString(R.string.agent_initial_message));
        }

        ChatMessage msg = new ChatMessage(chatText.toString(), false, ChatMessage.TEXT_TYPE);
        Globals.chatItems.add(msg);
        didStoreMessage(msg);

        notifyChange();
    }

    private void setEmptyChat(){
        if(Globals.chatItems.size() <= 0){
            setHelpMessage();
        }
    }

    /*
     * API.AI Initial setup
     */
    private void setupAPIAI(){
        final AIConfiguration config = new AIConfiguration(Constants.API_AI_KEY,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        //Needed for sending query text
        aiDataService = new AIDataService(getActivity(), config);
        aiRequest = new AIRequest();

        if (aiService != null) {
            aiService.cancel();
        }

        //Needed for sending query speech
        aiService = AIService.getService(getActivity(), config);
        aiService.setListener(HomeFragment.this);
    }

    /*
     * Request for permissions for getting location and audio
     */
    private void requestRecordPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.RECORD_AUDIO
                }, Constants.REQUEST_RECORD_PERMISSION);
            }
            else {
                didSpeechQuery();
            }
        }
        else{
            didSpeechQuery();
        }
    }

    private void requestForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, Constants.REQUEST_FINE_LOCATION_PERMISSION);
            }
            else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, Constants.REQUEST_COARSE_LOCATION_PERMISSION);
            }
            else {
                requestForLocationSettings();
            }
        }
        else{
            requestForLocationSettings();
        }
    }

    protected void requestForLocationSettings() {
        if(!locManager.setupLocation()){
            didToggleLoader(true);
            UtilityManager.showMessage(rqText, getString(R.string.no_location_services));

            locManager.deleteLocation();
        }
        else {
            PendingResult<LocationSettingsResult> result = locManager.buildLocationSetting();
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            prepareDataRetrievement();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(getActivity(), Constants.REQUEST_CHECK_SETTINGS);
                            }
                            catch (IntentSender.SendIntentException e) {
                                didShowAgentError();
                            }
                            break;
                        default:
                            didShowAgentError();
                            break;
                    }
                }
            });
        }
    }

    /*
     * Process query by audio
     */
    private void didSpeechQuery(){
        if(!UtilityManager.isConnected(getActivity())){
            UtilityManager.showMessage(rqText, getString(R.string.no_network_connection));
            return;
        }

        aiService.startListening();
    }

    /*
     * Process query by text
     */
    private void didSendQuery(boolean isHelp){
        String query;

        if(!UtilityManager.isConnected(getActivity())){
            UtilityManager.showMessage(rqText, getString(R.string.no_network_connection));
            return;
        }

        if(isHelp){
            query = Constants.HELP_ACTION;
        }
        else{
            query = rqText.getText().toString().trim();
            addUserMessage(query);
            rqText.setText(null);
        }

        aiRequest.setQuery(query);
        didToggleLoader(false);
        UtilityManager.hideKeyboard(getActivity());

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(request);
                    return response;
                }
                catch (AIServiceException e) {}

                return null;
            }
            @Override
            protected void onPostExecute(AIResponse response) {
                if (response != null) {
                    addAgentMessage(response, false);
                }
            }
        }.execute(aiRequest);
    }

    /*
     * Add messages from agent to chat room
     */
    private void addAgentMessage(AIResponse response, boolean fromMic){
        Result result = response.getResult();

        if(!result.getFulfillment().getSpeech().isEmpty()){
            if(fromMic){
                ChatMessage msg = new ChatMessage(result.getResolvedQuery(), true, ChatMessage.TEXT_TYPE);
                Globals.chatItems.add(msg);
                didStoreMessage(msg);
            }

            ChatMessage msg = new ChatMessage(result.getFulfillment().getSpeech(), false, ChatMessage.TEXT_TYPE);
            Globals.chatItems.add(msg);
            didStoreMessage(msg);

            notifyChange();

            if(result.getAction().equals(Constants.PLACES_ACTION)){
                agentParams = result.getParameters();

                String city;
                String thing;
                String sort = (agentParams.get("sorting") != null) ?
                        UtilityManager.removeAccents(agentParams.get("sorting").getAsString()) : null;

                if(agentParams.get("thing") != null){
                    thing = UtilityManager.removeAccents(agentParams.get("thing").getAsString());

                    if(agentParams.get("city") != null){
                        double lat = 0;
                        double lng = 0;

                        Coordinate location = preferencesManager.getCurrentLocation();

                        if(location != null){
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                        }

                        city = UtilityManager.removeAccents(agentParams.get("city").getAsString());
                        didRetrieveAgentQuery(city, lat, lng, thing, sort);
                    }
                    else{
                        requestForLocationPermission();
                    }
                }
                else {
                    didShowAgentError();
                }
            }
            else if(result.getAction().equals(Constants.HELP_ACTION)){
                didRetrieveHelpList();
            }
            else{
                didToggleLoader(true);
            }
        }
        else{
            didShowAgentError();
        }
    }

    /*
     * Add messages from user to chat room
     */
    private void addUserMessage(String query){
        ChatMessage msg = new ChatMessage(query, true, ChatMessage.TEXT_TYPE);
        Globals.chatItems.add(msg);
        didStoreMessage(msg);
        didSaveQuerySearch(msg);

        notifyChange();
    }

    /*
     * Getting wifi points from Medellin
     */
    private void didRetrieveWifiPoint(double latitude, double longitude){
        final RealmList<PublicWiFi> points = new RealmList<>();

        int distance = preferencesManager.getDistance();
        if(distance == 0){
            distance = getResources().getInteger(R.integer.default_distance);
        }
        int points_limit = Constants.WIFI_POINTS_LIMIT * distance;
        String params = String.format(Locale.getDefault(), "within_circle(latitud_y,%.3f,%.3f,%d)",
                latitude, longitude, points_limit);

        DataManager.willGetPublicWifiPoints(params, new DataCallback() {
            @Override
            public void didReceiveData(List<Object> response) {
                if(response != null){
                    List<PublicWiFi> data = UtilityManager.objectFilter(response, PublicWiFi.class);
                    if(data.size() > 0){
                        for(PublicWiFi point : data){
                            points.add(point);
                        }

                        ChatMessage msg = new ChatMessage(points, false, ChatMessage.GENERIC_TYPE, Constants.WIFI_FLAG, false);
                        Globals.chatItems.add(msg);
                        didStoreMessage(msg);

                        notifyChange();
                        didToggleLoader(true);
                    }
                    else{
                        showDefaultMessage();
                    }
                }
                else{
                    didShowAgentError();
                }
            }
        });
    }

    /*
     * Getting remote information
     */
    private void didRetrieveAgentQuery(String city, double latitude, double longitude, String thing, String sort){
        final RealmList<Place> listPlaces = new RealmList<>();

        int distance = preferencesManager.getDistance();
        if(distance == 0){
            distance = getResources().getInteger(R.integer.default_distance);
        }

        DataManager.willGetAllPlaces(city, latitude, longitude, thing, distance, sort, new DataCallback() {
            @Override
            public void didReceiveData(List<Object> response) {
                if(response != null){
                    List<Place> data = UtilityManager.objectFilter(response, Place.class);

                    if(data.size() > 0){
                        for(Place place : data){
                            if(place.isOutstanding()){
                                listPlaces.add(place);
                            }
                        }

                        ChatMessage msg = new ChatMessage(listPlaces, false, ChatMessage.PLACES_TYPE);
                        Globals.chatItems.add(msg);
                        didStoreMessage(msg);

                        notifyChange();
                        didToggleLoader(true);
                    }
                    else{
                        showDefaultMessage();
                    }
                }
                else{
                    didShowAgentError();
                }
            }
        });
    }

    /*
     * Getting help list
     */
    private void didRetrieveHelpList(){
        final RealmList<Help> helpList = new RealmList<>();

        DataManager.willShowHelp(new DataCallback() {
            @Override
            public void didReceiveData(List<Object> response) {
                if(response != null){
                    List<Help> data = UtilityManager.objectFilter(response, Help.class);
                    if(data.size() > 0){
                        for(Help help : data){
                            helpList.add(help);
                        }

                        ChatMessage msg = new ChatMessage(helpList, false, ChatMessage.GENERIC_TYPE, Constants.HELP_FLAG, false, false);
                        Globals.chatItems.add(msg);
                        didStoreMessage(msg);

                        notifyChange();
                        didToggleLoader(true);
                    }
                    else{
                        showDefaultMessage();
                    }
                }
                else{
                    didShowAgentError();
                }
            }
        });
    }

    private void notifyChange(){
        chatMessageAdapter.notifyDataSetChanged();
        chatList.scrollToPosition(Globals.chatItems.size()-1);

        setEmptyChat();
    }

    private void didStoreMessage(ChatMessage msg){
        db.beginTransaction();
        db.copyToRealm(msg);
        db.commitTransaction();
    }

    private void didGetChatList(){
        RealmResults<ChatMessage> messages = db.where(ChatMessage.class)
                .findAll();
        if(messages.size() > 0){
            for(ChatMessage msg : messages){
                Globals.chatItems.add(msg);
            }
        }
        else{
            setEmptyChat();
        }

        notifyChange();
    }

    private void didShowAgentError(){
        rqText.setHint(R.string.txt_placeholder_chat_input);
        btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);

        ChatMessage msg = new ChatMessage(getString(R.string.generic_agent_error), false, ChatMessage.TEXT_TYPE);
        Globals.chatItems.add(msg);
        didStoreMessage(msg);

        notifyChange();
        didToggleLoader(true);
    }

    private void didShowLocationError(){
        rqText.setHint(R.string.txt_placeholder_chat_input);
        btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);

        ChatMessage msg = new ChatMessage(getString(R.string.no_location_found), false, ChatMessage.TEXT_TYPE);
        Globals.chatItems.add(msg);
        didStoreMessage(msg);

        notifyChange();
        didToggleLoader(true);
    }

    private void didCancelLocation(){
        ChatMessage msg = new ChatMessage(getString(R.string.location_canceled_by_user), false, ChatMessage.TEXT_TYPE);
        Globals.chatItems.add(msg);
        didStoreMessage(msg);

        notifyChange();
        didToggleLoader(true);
    }

    private void didToggleLoader(boolean isHidden){
        if(isHidden){
            progressBar.setVisibility(View.GONE);
            chatLeftAction.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            chatLeftAction.setVisibility(View.GONE);
        }
    }

    private void prepareDataRetrievement(){
        final String thing = UtilityManager.removeAccents(agentParams.get("thing").getAsString());
        final String city = (agentParams.get("city") != null) ?
                UtilityManager.removeAccents(agentParams.get("city").getAsString()) : null;
        final String sort = (agentParams.get("sorting") != null) ?
                UtilityManager.removeAccents(agentParams.get("sorting").getAsString()) : null;
        final Coordinate location = preferencesManager.getCurrentLocation();

        if(location != null){
            if(thing.equals(Constants.WIFI_THING)){
                didRetrieveWifiPoint(location.getLatitude(), location.getLongitude());
            }
            else{
                didRetrieveAgentQuery(city, location.getLatitude(), location.getLongitude(), thing, sort);
                preferencesManager.setCurrentLocation(null);
            }
        }
        else{
            didShowLocationError();
        }
    }

    private void showDefaultMessage(){
        ChatMessage msg = new ChatMessage(getString(R.string.generic_agent_no_content), false, ChatMessage.TEXT_TYPE);
        Globals.chatItems.add(msg);
        didStoreMessage(msg);

        notifyChange();
        didToggleLoader(true);
    }

    private void didSaveQuerySearch(ChatMessage msg){
        Query q = new Query(msg.getMessage(), msg.getTimestamp(),
                Constants.BASE_OS.concat(": " + Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ")"));
        databaseReference.push().setValue(q);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSpeech:
                rqText.setText(null);
                rqText.clearFocus();

                UtilityManager.hideKeyboard(getActivity());
                requestRecordPermission();
                break;

            case R.id.btnText:
                didSendQuery(false);
                break;

            case R.id.chat_left_action:
                didSendQuery(true);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constants.REQUEST_RECORD_PERMISSION:
                    didSpeechQuery();
                    break;

                case Constants.REQUEST_COARSE_LOCATION_PERMISSION:
                case Constants.REQUEST_FINE_LOCATION_PERMISSION:
                    requestForLocationSettings();
                    break;
            }
        }
        else {
            if(requestCode == Constants.REQUEST_COARSE_LOCATION_PERMISSION ||
                    requestCode == Constants.REQUEST_FINE_LOCATION_PERMISSION){
                didCancelLocation();
            }
            else{
                didToggleLoader(true);
                UtilityManager.showMessage(rqText, getString(R.string.permission_denied));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                prepareDataRetrievement();
                            }
                        }, 3000);
                        break;
                    case Activity.RESULT_CANCELED:
                        didCancelLocation();
                        break;
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //API.AI Callbacks
    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rqText.setHint(R.string.txt_placeholder_chat_listening);
                rqText.setEnabled(false);

                btnSpeech.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                btnSpeech.setEnabled(false);

                didToggleLoader(false);
            }
        });
    }

    @Override
    public void onListeningCanceled() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rqText.setHint(R.string.txt_placeholder_chat_input);
                rqText.setEnabled(true);

                btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
                btnSpeech.setEnabled(true);

                aiService.cancel();
            }
        });
    }

    @Override
    public void onListeningFinished() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rqText.setHint(R.string.txt_placeholder_chat_input);
                rqText.setEnabled(true);

                btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
                btnSpeech.setEnabled(true);

                aiService.stopListening();
            }
        });
    }

    @Override
    public void onResult(final AIResponse result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addAgentMessage(result, true);
            }
        });
    }

    @Override
    public void onError(AIError error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                didShowAgentError();
            }
        });
    }
}
