package net.openid.appauth;

import static net.openid.appauth.TestValues.TEST_APP_REDIRECT_URI;
import static net.openid.appauth.TestValues.TEST_ID_TOKEN;
import static net.openid.appauth.TestValues.getTestServiceConfig;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 16)
public class EndSessionRequestTest {

    /* ********************************** Builder() ***********************************************/
    @Test(expected = NullPointerException.class)
    public void testBuilder_nullConfiguration() {
        new EndSessionRequest.Builder(
            null,
            TEST_ID_TOKEN,
            TEST_APP_REDIRECT_URI);
    }

    @Test(expected = NullPointerException.class)
    public void testBuilder_nullIdToken() {
        new EndSessionRequest.Builder(
            getTestServiceConfig(),
            null,
            TEST_APP_REDIRECT_URI);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_emptyIdToken() {
        new EndSessionRequest.Builder(
            getTestServiceConfig(),
            "",
            TEST_APP_REDIRECT_URI);
    }

    @Test(expected = NullPointerException.class)
    public void testBuilder_nullRedirectUri() {
        new EndSessionRequest.Builder(
            getTestServiceConfig(),
            TEST_ID_TOKEN,
            null);
    }

    @Test
    public void testToUri() {
        EndSessionRequest request = TestValues.getTestEndSessionRequest();
        Uri requestUri = request.toUri();

        assertThat(requestUri.getQueryParameter(EndSessionRequest.KEY_CLIENT_ID))
            .isEqualTo(request.clientId);
        assertThat(requestUri.getQueryParameter(EndSessionRequest.KEY_LOGOUT_URI))
            .isEqualTo(request.logoutUri.toString());

    }

    @Test
    public void testJsonSerialize() throws Exception {
        EndSessionRequest resquest = TestValues.getTestEndSessionRequest();
        EndSessionRequest copy = serializeDeserialize(resquest);
        assertThat(copy.logoutUri).isEqualTo(resquest.logoutUri);
    }

    @Test
    public void testIsEndSessionRequestSuccess() {
        JSONObject json = TestValues.getTestEndSessionRequest().jsonSerialize();
        assertTrue(EndSessionRequest.isEndSessionRequest(json));
    }

    @Test
    public void testIsEndSessionRequestFailure() {
        JSONObject json = TestValues.getTestAuthRequestBuilder().build().jsonSerialize();
        assertFalse(EndSessionRequest.isEndSessionRequest(json));
    }

    private EndSessionRequest serializeDeserialize(EndSessionRequest request)
        throws JSONException {
        return EndSessionRequest.jsonDeserialize(request.jsonSerializeString());
    }

}
