package co.bongga.toury.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.android.AIDataService;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import co.bongga.toury.R;
import co.bongga.toury.adapters.ChatMessageAdapter;
import co.bongga.toury.interfaces.DataCallback;
import co.bongga.toury.models.ChatMessage;
import co.bongga.toury.models.Event;
import co.bongga.toury.utils.Constants;
import co.bongga.toury.utils.DataManager;
import co.bongga.toury.utils.UtilityManager;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AIListener, View.OnClickListener {
    private RecyclerView chatList;
    private ChatMessageAdapter chatMessageAdapter;
    private ArrayList<ChatMessage> chatItems = new ArrayList();

    private LinearLayout chatBottomContainer;
    private EditText rqText;
    private ImageButton btnSpeech;
    private ImageButton btnText;
    private ProgressBar progressBar;
    private ImageView chatLeftAction;

    private AIService aiService;
    private AIRequest aiRequest;
    private AIDataService aiDataService;

    private Realm db;

    private static final int REQUEST_RECORD_PERMISSION = 1;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAPIAI();
        db = Realm.getDefaultInstance();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (aiService != null) {
            aiService.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (aiService != null) {
            aiService.resume();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        chatBottomContainer = (LinearLayout) view.findViewById(R.id.chat_bottom_container);
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
        btnText.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        btnText.setOnClickListener(this);

        chatList = (RecyclerView) view.findViewById(R.id.chatList);

        chatMessageAdapter = new ChatMessageAdapter(getActivity(), chatItems);

        chatList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        chatList.setLayoutManager(layoutManager);
        chatList.setItemAnimator(new DefaultItemAnimator());
        chatList.setAdapter(chatMessageAdapter);

        didGetChatList();

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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.RECORD_AUDIO
            }, REQUEST_RECORD_PERMISSION);
        }
        else {
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

        String query = rqText.getText().toString();
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

    private void addAgentMessage(AIResponse response, boolean fromMic){
        Result result = response.getResult();

        //TODO: Revisar respuesta para refinar validacion
        if(!result.getFulfillment().getSpeech().isEmpty()){
            if(fromMic){
                ChatMessage msg = new ChatMessage(result.getResolvedQuery(), true, ChatMessage.TEXT_TYPE);
                chatItems.add(msg);
                didStoreMessage(msg);
            }

            ChatMessage msg = new ChatMessage(result.getFulfillment().getSpeech(), false, ChatMessage.TEXT_TYPE);
            chatItems.add(msg);
            didStoreMessage(msg);

            notifyChange();

            if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                didRetrieveAgentQuery(result.getParameters());
            }
            else{
                didToggleLoader(true);
            }
        }
        else{
            ChatMessage msg = new ChatMessage(getString(R.string.generic_agent_no_content), false, ChatMessage.TEXT_TYPE);
            chatItems.add(msg);
            didStoreMessage(msg);

            notifyChange();
            didToggleLoader(true);
        }
    }

    private void addUserMessage(String query){
        ChatMessage msg = new ChatMessage(query, true, ChatMessage.TEXT_TYPE);
        chatItems.add(msg);
        didStoreMessage(msg);

        notifyChange();
    }

    private void didRetrieveAgentQuery(HashMap<String, JsonElement> params){
        for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();

            if(key.equals("places")){
                if(!value.isEmpty() && value.equals("restaurantes")){
                    RealmList<Event> listEvents = new RealmList<>();
                    Event event = new Event("http://cdn5.upsocl.com/wp-content/uploads/2014/05/awebic-restaurantes-7.jpg",
                            "Cuzco", "Medellin", "Colombia", "The most valuable experience", null, null, null, 5.8, value,
                            true, 4.8, null, "Medellin");
                    listEvents.add(event);

                    Event event2 = new Event("http://www.vacazionaviajes.com/blog/wp-content/uploads/2012/08/restaurante-principal.jpg",
                            "Paseo de los Corderos", "Medellin", "Colombia",
                            "The most valuable experience", null, null, null, 1.3,
                            value, true, 4.0, null, "Medellin");
                    listEvents.add(event2);

                    ChatMessage msg = new ChatMessage(listEvents, false, ChatMessage.EVENT_TYPE);
                    chatItems.add(msg);
                    didStoreMessage(msg);

                    notifyChange();
                }
                else  if(!value.isEmpty() && value.equals("cines")){
                    final RealmList<Event> listEvents = new RealmList<>();
                    /*Event event = new Event("http://www.puertadelnorte.com/images/puertadelnorte/almacenes/cinemas-procinal/cinemas-procinal-puerta-del-norte.jpg",
                            "Cinemas Procinal", "Medellin", "Colombia", "The most valuable experience", null, null, null, 5.8, value,
                            true, 4, null, "Medellin");
                    listEvents.add(event);

                    Event event2 = new Event("http://noticias.caracoltv.com/sites/default/files/cine-colombia-nuevas-salas.jpg",
                            "Cine Colombia", "Medellin", "Colombia",
                            "The most valuable experience", null, null, null, 1.3,
                            value, true, 4, null, "Medellin");
                    listEvents.add(event2);

                    ChatMessage msg = new ChatMessage(listEvents, false, ChatMessage.EVENT_TYPE);
                    chatItems.add(msg);
                    didStoreMessage(msg);

                    */

                    DataManager.willGetAllAttractions(new DataCallback() {
                        @Override
                        public void didReceiveData(List<Event> data) {
                            if(data != null){
                                for(Event event : data){
                                    listEvents.add(event);
                                }

                                ChatMessage msg = new ChatMessage(listEvents, false, ChatMessage.EVENT_TYPE);
                                chatItems.add(msg);
                                didStoreMessage(msg);

                                notifyChange();
                            }
                        }
                    });
                }
                else  if(!value.isEmpty() && value.equals("canchas de f\u00fatbol")){
                    RealmList<Event> listEvents = new RealmList<>();
                    Event event = new Event("http://elgolazo.co/wp-content/uploads/2015/08/IMG_0856-1180x720.jpg",
                            "El Golazo", "Medellin", "Colombia", "The most valuable experience", null, null, null, 5.8, value,
                            true, 4.0, null, "Medellin");
                    listEvents.add(event);

                    Event event2 = new Event("https://i.ytimg.com/vi/rmtBGWVmm2E/maxresdefault.jpg",
                            "Il Campo", "Medellin", "Colombia",
                            "The most valuable experience", null, null, null, 1.3,
                            value, true, 4.0, null, "Medellin");
                    listEvents.add(event2);

                    Event event3 = new Event("http://www.canchasfutbolmedellin.com/media/k2/items/cache/2fa67f482133f1c934235b73c2a03954_XL.jpg",
                            "Elite Futbol", "Medellin", "Colombia",
                            "The most valuable experience", null, null, null, 1.3,
                            value, true, 4.0, null, "Medellin");
                    listEvents.add(event3);

                    Event event4 = new Event("http://tuciudadenred.com/data/foto/gr_1401480138_714077533.jpg",
                            "El Parque de los Principes", "Medellin", "Colombia",
                            "The most valuable experience", null, null, null, 1.3,
                            value, true, 4.0, null, "Medellin");
                    listEvents.add(event4);

                    ChatMessage msg = new ChatMessage(listEvents, false, ChatMessage.EVENT_TYPE);
                    chatItems.add(msg);
                    didStoreMessage(msg);

                    notifyChange();
                }
            }
        }

        didToggleLoader(true);
    }

    private void notifyChange(){
        chatMessageAdapter.notifyDataSetChanged();
        chatList.scrollToPosition(chatItems.size()-1);
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
            chatItems.add(msg);
        }

        notifyChange();
    }

    private void didShowAgentError(){
        rqText.setHint(R.string.txt_placeholder_chat_input);
        btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);

        ChatMessage msg = new ChatMessage(getString(R.string.generic_agent_error), false, ChatMessage.TEXT_TYPE);
        chatItems.add(msg);
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

    public void didClearAllData(){
        db.beginTransaction();
        db.where(ChatMessage.class).findAll().deleteAllFromRealm();
        db.commitTransaction();

        chatItems.clear();
        chatMessageAdapter.notifyDataSetChanged();
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
            }
        }
        else {
            UtilityManager.showMessage(rqText, getString(R.string.permission_denied));
        }
    }
}
