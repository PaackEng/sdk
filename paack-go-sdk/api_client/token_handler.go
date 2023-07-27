package api_client

import (
	"encoding/json"
	"fmt"
	"github.com/PaackEng/paack-go-sdk/logger"
	"io"
	"paack-go-sdk/resources"
	"time"
)

var Cache = newLocalCache(time.Hour * time.Duration(resources.CacheCleanupHoursInterval))

/*
		Authenticator class. Create and manages token.
	    Create a token and store it in memory using RLock.
	    Args:
	        clientSecret (string): Retailers secret
	        clientId (string): Retailer's Id
	        audience (string): audience/environment to connect
	        oauth2_url (string): auth url
*/
type TokenHandler struct {
	ClientSecret string
	Oauth2Url    string
	ClientId     string
	AudienceList []string
	logger       logger.Logger
}

// NewTokenHandler provides an initialized Header.
func NewTokenHandler(audienceList []string, clientId, clientSecret, oauth2Url string, logger logger.Logger) *TokenHandler {
	return &TokenHandler{
		ClientSecret: clientSecret,
		Oauth2Url:    oauth2Url,
		ClientId:     clientId,
		AudienceList: audienceList,
		logger:       logger,
	}
}

// RetrieveToken reads the token from cache. If the token is expired a new one is generated
func (t *TokenHandler) RetrieveToken(audience string) (result *CachedToken, err error) {

	credentials := Credentials{
		Audience: audience,
		ClientId: t.ClientId,
	}

	// read token from cache
	cachedToken := t.readToken(credentials)
	if nil == cachedToken || cachedToken.expireAtTimestamp <= time.Now().Add(5*time.Minute).Unix() {
		// token is invalid and a new one will be generated
		generatedToken, expiresIn, err := t.generateOAuthToken(audience)
		if err != nil {
			return result, err
		}

		// add token to cache
		t.addToken(generatedToken, time.Now().Unix()+expiresIn, credentials)

		return t.readToken(credentials), nil
	}

	return cachedToken, nil
}

func (t *TokenHandler) DeleteToken(audience string) {

	credentials := Credentials{
		Audience: audience,
		ClientId: t.ClientId,
	}

	// delete token from cache
	Cache.delete(credentials)
}

func (t *TokenHandler) readToken(credentials Credentials) *CachedToken {
	return Cache.Read(credentials)
}

func (t *TokenHandler) addToken(token string, expireAtTimestamp int64, credentials Credentials) {
	Cache.update(Token{
		authToken:   token,
		credentials: credentials,
	}, expireAtTimestamp)
}

func (t *TokenHandler) getOAuthPayload(audience string) string {
	return fmt.Sprintf(
		`{"client_id":"%s","client_secret": "%s","audience":"%s","grant_type":"client_credentials"}`,
		t.ClientId, t.ClientSecret, audience,
	)
}

func (t *TokenHandler) generateOAuthToken(audience string) (accessToken string, expiresIn int64, err error) {
	apiClient := NewApiClient(t, 0, 0, t.logger)

	resp, err := apiClient.Post(t.Oauth2Url, []byte(t.getOAuthPayload(audience)), false, audience)
	if err != nil {
		return accessToken, expiresIn, fmt.Errorf("error making the request to oauth2: %s", err)
	}

	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return accessToken, expiresIn, fmt.Errorf("token was not generated: %s", resp.Status)
	}

	jwt := struct {
		AccessToken string `json:"access_token"`
		ExpiresIn   int64  `json:"expires_in"`
	}{}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return accessToken, expiresIn, err
	}

	err = json.Unmarshal(body, &jwt)
	if err != nil {
		return accessToken, expiresIn, err
	}

	accessToken = jwt.AccessToken
	expiresIn = jwt.ExpiresIn

	return
}
