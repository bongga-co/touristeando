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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.List;
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
import co.bongga.touristeando.activities.Main;
import co.bongga.touristeando.adapters.ChatMessageAdapter;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.models.ChatMessage;
import co.bongga.touristeando.models.Coordinate;
import co.bongga.touristeando.models.Event;
import co.bongga.touristeando.models.Place;
import co.bongga.touristeando.models.Query;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.DataManager;
import co.bongga.touristeando.utils.Globals;
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

    public static final int REQUEST_RECORD_PERMISSION = 1;
    public static final int REQUEST_COARSE_LOCATION_PERMISSION = 2;
    public static final int REQUEST_FINE_LOCATION_PERMISSION = 3;
    public static final int REQUEST_CHECK_SETTINGS = 4;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupAPIAI();

        db = Realm.getDefaultInstance();
        preferencesManager = new PreferencesManager(getActivity());

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

    private void setupAPIAI(){
        final AIConfiguration config = new AIConfiguration(Constants.API_AI_KEY,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        //Needed for sending query text
        aiDataService = new AIDataService(getActivity(), config);
        aiRequest = new AIRequest();

        if (aiService != null) {
            aiService.pause();
        }

        //Needed for sending query speech
        aiService = AIService.getService(getActivity(), config);
        aiService.setListener(this);
    }

    private void requestRecordPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_RECORD_PERMISSION);
            }
            else {
                didSpeechQuery();
            }
        }
        else{
            didSpeechQuery();
        }
    }

    private void didSpeechQuery(){
        if(!UtilityManager.isConnected(getActivity())){
            UtilityManager.showMessage(rqText, getString(R.string.no_network_connection));
            return;
        }

        aiService.startListening();
    }

    private void didSendQuery(){
        if(!UtilityManager.isConnected(getActivity())){
            UtilityManager.showMessage(rqText, getString(R.string.no_network_connection));
            return;
        }

        String query = rqText.getText().toString().trim();
        aiRequest.setQuery(query);
        rqText.setText(null);

        addUserMessage(query);
        didToggleLoader(false);

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

    private void setHelpMessage(){
        Spanned chatText = null;

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

            if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                agentParams = result.getParameters();

                if(result.getAction().equals(Constants.PLACES_ACTION)){
                    String city = null;
                    String thing = null;
                    String sort = (agentParams.get("sorting") != null) ?
                            UtilityManager.removeAccents(agentParams.get("sorting").getAsString()) : null;
                    String actionType = null;

                    if(agentParams.get("thing") != null){
                        thing = UtilityManager.removeAccents(agentParams.get("thing").getAsString());

                        if(agentParams.get("city") != null){
                            //TODO: Get location as well
                            city = UtilityManager.removeAccents(agentParams.get("city").getAsString());
                            didRetrieveAgentQuery(city, 0, 0, thing, sort);
                        }
                        else{
                            Coordinate location = preferencesManager.getCurrentLocation();
                            if(location != null){
                                didRetrieveAgentQuery(null, location.getLatitude(), location.getLongitude(), thing, sort);
                            }
                            else{
                                requestForLocationPermission();
                            }
                        }
                    }
                    else {
                        didShowAgentError();
                    }
                }
                else{
                    //Another action
                    didToggleLoader(true);
                }
            }
            else{
                didToggleLoader(true);
            }
        }
        else{
            didShowAgentError();
        }
    }

    private void addUserMessage(String query){
        ChatMessage msg = new ChatMessage(query, true, ChatMessage.TEXT_TYPE);
        Globals.chatItems.add(msg);
        didStoreMessage(msg);
        didSaveQuerySearch(msg);

        notifyChange();
    }

    private void didRetrieveAgentQuery(String city, double latitude, double longitude, String thing, String sort){
        final RealmList<Place> listPlaces = new RealmList<>();

        int distance = preferencesManager.getDistance();
        if(distance == 0){
            distance = getResources().getInteger(R.integer.default_distance);
        }

        DataManager.willGetAllPlaces(city, latitude, longitude, thing, distance, sort, new DataCallback() {
            @Override
            public void didReceiveEvent(List<Event> data) {

            }

            @Override
            public void didReceivePlace(List<Place> data) {
                if(data != null){
                    if(data.size() > 0){
                        for(Place place : data){
                            listPlaces.add(place);
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

    private void notifyChange(){
        chatMessageAdapter.notifyDataSetChanged();
        chatList.scrollToPosition(Globals.chatItems.size()-1);
    }

    private void didStoreMessage(ChatMessage msg){
        db.beginTransaction();
        db.copyToRealm(msg);
        db.commitTransaction();
    }

    private void didGetChatList(){
        RealmResults<ChatMessage> messages = db.where(ChatMessage.class)
                .findAll();
        for(ChatMessage msg : messages){
            Globals.chatItems.add(msg);
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

    protected void requestForLocationSettings() {
        LocationSettingsRequest locationSettingsRequest = ((Main)getActivity()).getLocationSetting();
        GoogleApiClient googleApiClient = ((Main)getActivity()).getGoogleAPIClient();

        if(googleApiClient != null){
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            googleApiClient,
                            locationSettingsRequest
                    );
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            startLocationUpdates();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
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
        else{
            didToggleLoader(true);
            UtilityManager.showMessage(rqText, getString(R.string.no_location_services));
        }
    }

    private void requestForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_FINE_LOCATION_PERMISSION);
            }
            else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, REQUEST_COARSE_LOCATION_PERMISSION);
            }
            else {
                requestForLocationSettings();
            }
        }
        else{
            requestForLocationSettings();
        }
    }

    private void startLocationUpdates(){
        final String thing = UtilityManager.removeAccents(agentParams.get("thing").getAsString());
        final String sort = UtilityManager.removeAccents(agentParams.get("sorting").getAsString());
        final Coordinate location = preferencesManager.getCurrentLocation();

        if(location != null){
            didRetrieveAgentQuery("", location.getLatitude(), location.getLongitude(), thing, sort);
        }
        else{
            preferencesManager.setCurrentLocation(null);
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

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rqText.setHint(R.string.txt_placeholder_chat_listening);
                btnSpeech.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

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
                btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    @Override
    public void onListeningFinished() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rqText.setHint(R.string.txt_placeholder_chat_input);
                btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
            }
        });
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
                didSendQuery();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_RECORD_PERMISSION:
                    didSpeechQuery();
                    break;

                case REQUEST_COARSE_LOCATION_PERMISSION:
                case REQUEST_FINE_LOCATION_PERMISSION:
                    requestForLocationSettings();
                    break;
            }
        }
        else {
            if(requestCode == REQUEST_COARSE_LOCATION_PERMISSION ||
                    requestCode == REQUEST_FINE_LOCATION_PERMISSION){
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
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        didCancelLocation();
                        break;
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
