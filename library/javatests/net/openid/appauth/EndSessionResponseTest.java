package net.openid.appauth;

import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 16)
public class EndSessionResponseTest {

    private static final EndSessionRequest TEST_REQUEST =  TestValues.getTestEndSessionRequest();


    @Test(expected = NullPointerException.class)
    public void testBuilder_nullRequest(){
        new EndSessionResponse.Builder(null);
    }

    @Test
    public void testIntentSerializeDeserialize(){
        EndSessionResponse endSessionResponse =
            new EndSessionResponse.Builder(TEST_REQUEST)
                .build();

        Intent endSessionIntent = endSessionResponse.toIntent();

        EndSessionResponse deserializeResponse = EndSessionResponse.fromIntent(endSessionIntent);

        assertThat(deserializeResponse).isNotNull();
        assertThat(deserializeResponse.request.logoutUri)
            .isEqualTo(endSessionResponse.request.logoutUri);
        assertThat(deserializeResponse.request.configuration.endSessionEndpoint)
            .isEqualTo(endSessionResponse.request.configuration.endSessionEndpoint);
        assertThat(deserializeResponse.request.clientId)
            .isEqualTo(endSessionResponse.request.clientId);
    }

    @Test
    public void  testIntentSerializeNull(){
        EndSessionResponse deserializeResponse = EndSessionResponse.fromIntent(new Intent());
        assertThat(deserializeResponse).isNull();
    }

    @Test (expected = IllegalArgumentException.class)
    public void  testIntentDeserializeError(){
        Intent intent = new Intent();
        intent.putExtra(EndSessionResponse.EXTRA_RESPONSE, "");
        EndSessionResponse.fromIntent(intent);
    }

    @Test
    public void testJsonSerializeDeserialize() throws JSONException {
        EndSessionResponse endSessionResponse =
            new EndSessionResponse.Builder(TEST_REQUEST)
                .build();

        JSONObject endSessionResponseJson = endSessionResponse.jsonSerialize();

        EndSessionResponse deserializeResponse =
            EndSessionResponse.jsonDeserialize(endSessionResponseJson);

        assertThat(deserializeResponse).isNotNull();
        assertThat(deserializeResponse.request.logoutUri)
            .isEqualTo(endSessionResponse.request.logoutUri);
        assertThat(deserializeResponse.request.configuration.endSessionEndpoint)
            .isEqualTo(endSessionResponse.request.configuration.endSessionEndpoint);
        assertThat(deserializeResponse.request.clientId)
            .isEqualTo(endSessionResponse.request.clientId);
    }

    @Test
    public void testFromRequestAndUri_Success(){
        EndSessionResponse endSessionResponse =
            new EndSessionResponse.Builder(TEST_REQUEST)
                .build();

        Uri endSessionUri = new Uri.Builder()
            .build();

        EndSessionResponse endSessionResponseDeserialized =
            new EndSessionResponse.Builder(TEST_REQUEST)
                .fromUri(endSessionUri)
                .build();
        assertThat(endSessionResponseDeserialized).isNotNull();
        assertThat(endSessionResponseDeserialized.request.logoutUri)
            .isEqualTo(endSessionResponse.request.logoutUri);
        assertThat(endSessionResponseDeserialized.request.configuration.endSessionEndpoint)
            .isEqualTo(endSessionResponse.request.configuration.endSessionEndpoint);
        assertThat(endSessionResponseDeserialized.request.clientId)
            .isEqualTo(endSessionResponse.request.clientId);
    }

    @Test
    public void testIntent_containsEndSessionResponse_True() {
        EndSessionResponse endSessionResponse =
            new EndSessionResponse.Builder(TEST_REQUEST)
                .build();

        Intent endSessionIntent = endSessionResponse.toIntent();

        assertThat(EndSessionResponse.containsEndSessionResponse(endSessionIntent)).isTrue();
    }

    @Test
    public void testIntent_containsEndSessionResponse_False() {
        Intent intent = new Intent();
        assertThat(EndSessionResponse.containsEndSessionResponse(intent)).isFalse();
    }

}
