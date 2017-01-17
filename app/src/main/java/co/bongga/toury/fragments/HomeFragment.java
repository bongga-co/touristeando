package co.bongga.toury.fragments;

import android.Manifest;
import android.app.ProgressDialog;
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
import co.bongga.toury.models.ChatMessage;
import co.bongga.toury.models.Event;
import co.bongga.toury.utils.Constants;
import co.bongga.toury.utils.UtilityManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AIListener, View.OnClickListener {
    private RecyclerView chatList;
    private ChatMessageAdapter chatMessageAdapter;
    private ArrayList<ChatMessage> chatItems = new ArrayList();

    private EditText rqText;
    private ImageButton btnSpeech;
    private ImageButton btnText;

    private AIService aiService;
    private AIRequest aiRequest;
    private AIDataService aiDataService;

    private ProgressDialog loader;

    private static final int REQUEST_RECORD_PERMISSION = 1;

    public HomeFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAPIAI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        loader = UtilityManager.showLoader(getActivity(), getString(R.string.loader_message));

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

        aiDataService = new AIDataService(getActivity(), config);
        aiRequest = new AIRequest();

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
            aiService.startListening();
        }
    }

    private void didSendQuery(){
        String query = rqText.getText().toString();
        aiRequest.setQuery(query);
        rqText.setText(null);

        addUserMessage(query);

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

            if(key.equals("places") && (!value.isEmpty() && value.equals("restaurantes"))){
                List<Event> listEvents = new ArrayList<>();
                Event event = new Event("http://cdn5.upsocl.com/wp-content/uploads/2014/05/awebic-restaurantes-7.jpg",
                        "Cuzco", "Medellin", "Colombia", "The most valuable experience", null, null, null, 5.8, value,
                        true, 4, null, "Medellin");
                listEvents.add(event);

                Event event2 = new Event("http://www.vacazionaviajes.com/blog/wp-content/uploads/2012/08/restaurante-principal.jpg",
                        "Paseo de los Corderos", "Medellin", "Colombia",
                        "The most valuable experience", null, null, null, 1.3,
                        value, true, 4, null, "Medellin");
                listEvents.add(event2);

                ChatMessage msg = new ChatMessage(listEvents, false, ChatMessage.EVENT_TYPE);
                chatItems.add(msg);

                notifyChange();
            }
        }
    }

    private void notifyChange(){
        chatMessageAdapter.notifyDataSetChanged();
        chatList.scrollToPosition(chatItems.size()-1);
    }

    private void didStoreMessage(ChatMessage msg){

    }

    private void didGetChatList(){

    }

    @Override
    public void onResult(AIResponse result) {
        addAgentMessage(result, true);
    }

    @Override
    public void onError(AIError error) {
        rqText.setHint(R.string.txt_placeholder_chat_input);
        btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        rqText.setHint(R.string.txt_placeholder_chat_listening);
        btnSpeech.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onListeningCanceled() {
        rqText.setHint(R.string.txt_placeholder_chat_input);
        btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onListeningFinished() {
        rqText.setHint(R.string.txt_placeholder_chat_input);
        btnSpeech.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
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
                    aiService.startListening();
                    break;
            }
        }
        else {
            UtilityManager.showMessage(rqText, getString(R.string.permission_denied));
        }
    }
}
