package api_client

import (
	"fmt"
	"paack-go-sdk/resources"
)

/*
	Header object to be used on the Request when calling the API.
	It can be a default one, without the Token or we can have an Header
	containing the Token.
*/
func DefaultHeaders(cashedToken *CachedToken) (result map[string][]string) {
	result = make(map[string][]string)
	result["Content-Type"] = append(result["Content-Type"], "application/json")
	result["X-PAACK-SDK-VERSION"] = []string{fmt.Sprintf("Go/%s", resources.SdkVersion)}

	if cashedToken != nil && len(cashedToken.token.authToken) > 0 {
		result["Accept"] = []string{"*/*"}
		result["Authorization"] = []string{fmt.Sprintf("Bearer %s", cashedToken.token.authToken)}
	} else {
		result["Accept"] = []string{"application/json"}
	}

	return
}
