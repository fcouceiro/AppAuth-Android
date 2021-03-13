/*
 * Copyright 2016 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openid.appauth;

import static net.openid.appauth.Preconditions.checkNotEmpty;
import static net.openid.appauth.Preconditions.checkNotNull;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An OpenID end session request.
 *
 * NOTE: That is a draft implementation
 *
 * @see "OpenID Connect Session Management 1.0 - draft 28, 5 RP-Initiated Logout
 * <https://openid.net/specs/openid-connect-session-1_0.html#RPLogout>"
 */
public class EndSessionRequest extends AuthorizationManagementRequest {

    private static final String PARAM_LOGOUT_URI_URI = "logout_uri";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String KEY_CONFIGURATION = "configuration";

    @VisibleForTesting
    static final String KEY_CLIENT_ID = "client_id";
    @VisibleForTesting
    static final String KEY_LOGOUT_URI = "logout_uri";

    /**
     * The service's {@link AuthorizationServiceConfiguration configuration}.
     * This configuration specifies how to connect to a particular OAuth provider.
     * Configurations may be
     * {@link
     * AuthorizationServiceConfiguration#AuthorizationServiceConfiguration(Uri, Uri, Uri, Uri)}
     * created manually}, or {@link AuthorizationServiceConfiguration#fetchFromUrl(Uri,
     * AuthorizationServiceConfiguration.RetrieveConfigurationCallback)} via an OpenID Connect
     * Discovery Document}.
     */
    @NonNull
    public final AuthorizationServiceConfiguration configuration;

    @NonNull
    public final String clientId;

    /**
     * The client's logout URI.
     *
     * @see "OpenID Connect Session Management 1.0 - draft 28, 5.1.  Redirection to RP After Logout
     * <https://openid.net/specs/openid-connect-session-1_0.html#RedirectionAfterLogout>"
     */
    @NonNull
    public final Uri logoutUri;

    /**
     * Creates instances of {@link EndSessionRequest}.
     */
    public static final class Builder {

        @NonNull
        private AuthorizationServiceConfiguration mConfiguration;

        @NonNull
        private String mClientId;

        @NonNull
        private Uri mLogoutUri;

        public Builder(
                @NonNull AuthorizationServiceConfiguration configuration,
                @NonNull String clientId,
                @NonNull Uri logoutUri) {
            setAuthorizationServiceConfiguration(configuration);
            setClientId(clientId);
            setLogoutUri(logoutUri);
        }

        /**
         * Specifies the service configuration to be used in dispatching this request.
         */
        public Builder setAuthorizationServiceConfiguration(
                @NonNull AuthorizationServiceConfiguration configuration) {
            mConfiguration = checkNotNull(configuration, "configuration cannot be null");
            return this;
        }

        public Builder setClientId(@NonNull String clientId) {
            mClientId = checkNotEmpty(clientId, "client id cannot be null or empty");
            return this;
        }

        public Builder setLogoutUri(@Nullable Uri logoutUri) {
            mLogoutUri = checkNotNull(logoutUri, "logout Uri cannot be null");
            return this;
        }

        /**
         * Constructs an end session request. All fields must be set.
         * Failure to specify any of these parameters will result in a runtime exception.
         */
        @NonNull
        public EndSessionRequest build() {
            return new EndSessionRequest(
                mConfiguration,
                mClientId,
                mLogoutUri);
        }
    }

    @VisibleForTesting
    private EndSessionRequest(
            @NonNull AuthorizationServiceConfiguration configuration,
            @NonNull String clientId,
            @NonNull Uri logoutUri) {
        this.configuration = configuration;
        this.clientId = clientId;
        this.logoutUri = logoutUri;
    }

    @Override
    public Uri toUri() {
        Uri.Builder uriBuilder = configuration.endSessionEndpoint.buildUpon()
                .appendQueryParameter(PARAM_LOGOUT_URI_URI, logoutUri.toString())
                .appendQueryParameter(PARAM_CLIENT_ID, clientId);
        return  uriBuilder.build();
    }

    /**
     * Produces a JSON representation of the end session request for persistent storage or local
     * transmission (e.g. between activities).
     */
    @Override
    public JSONObject jsonSerialize() {
        JSONObject json = new JSONObject();
        JsonUtil.put(json, KEY_CONFIGURATION, configuration.toJson());
        JsonUtil.put(json, KEY_CLIENT_ID, clientId);
        JsonUtil.put(json, KEY_LOGOUT_URI, logoutUri.toString());
        return json;
    }

    @Override
    public String getState() {
        return null; // AWS Cognito does not handle state
    }

    /**
     * Reads an authorization request from a JSON string representation produced by
     * {@link #jsonSerialize()}.
     * @throws JSONException if the provided JSON does not match the expected structure.
     */
    public static EndSessionRequest jsonDeserialize(@NonNull JSONObject jsonObject)
            throws JSONException {
        checkNotNull(jsonObject, "json cannot be null");
        return new EndSessionRequest(
            AuthorizationServiceConfiguration.fromJson(jsonObject.getJSONObject(KEY_CONFIGURATION)),
            JsonUtil.getString(jsonObject, KEY_CLIENT_ID),
            JsonUtil.getUri(jsonObject, KEY_LOGOUT_URI)
        );
    }

    /**
     * Reads an authorization request from a JSON string representation produced by
     * {@link #jsonSerializeString()}. This method is just a convenience wrapper for
     * {@link #jsonDeserialize(JSONObject)}, converting the JSON string to its JSON object form.
     * @throws JSONException if the provided JSON does not match the expected structure.
     */
    @NonNull
    public static EndSessionRequest jsonDeserialize(@NonNull String jsonStr)
            throws JSONException {
        checkNotNull(jsonStr, "json string cannot be null");
        return jsonDeserialize(new JSONObject(jsonStr));
    }

    static boolean isEndSessionRequest(JSONObject json) {
        return json.has(KEY_LOGOUT_URI);
    }

}
