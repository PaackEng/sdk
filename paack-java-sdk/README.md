Common functionality

Supported versions
This library supports 1.8 and above java implementations:


First Step
To start using the Paack SDK, you first must install it using the dependency management solution you employ. Make sure to use the latest version and keep checking periodically for new versions.

Once installed, the first step is to instantiate an object of type Paack. This is the entry point for all methods that allow you to interact with our API. The methods are grouped based on their area of usage (e.g. order, label, tracking etc.). If needed, you can create more instances of the Paack object, but be aware that each will have its own caches and related class instances.

Make sure you use the same instance of the Paack object and not create a new one each time. By reusing the instance, the SDK will take care to minimize the number of requests where possible. For example retrieving the configuration and authentication token only once.
API Credentials
When constructing the Paack object to utilize the SDK you must pass your Paack credentials via the constructor. Those credentials are obtained from Paack once you have engaged with a Paack Account Manager. Those are specific to your account with Paack and are uniquely identifying your company, so keep them safe. The clientId and clientSecret credentials must match the environment you plan to use the SDK with, as each environment has their own pair of credentials. Using the “domain” argument you must specify either the Staging or Production environment.
Authentication
The SDK handles the authentication with Paack APIs automatically. You don’t need to worry about it.
Using the credentials provided it will generate a token with the required audience for each API you plan to use. The token is generated lazy, when you first call that API.
Once the token was generated, it will cache it for future use. Every time you call an API using the SDK methods, it will automatically be added in the headers of the request.
In case the API request will respond that the token is expired, the SDK will automatically refresh the token and re-send the request using the new token.
Configuration
The SDK uses 2 sources of configuration values: locally from the SDK, remote from Paack servers.
When the Paack object is initialized, it will first use the local configuration. This helps the SDK know where to connect for authentication and retrieving the remote configuration as well as serve as a baseline for configuration values.

Once the local configuration is loaded successfully, it will retrieve additional configuration values from Paack servers. Once retrieved, they are merged with the local configuration properties. This allows Paack to do over-the-air updates to certain parts of the SDK without requiring you to update the version of the SDK. It also enables us to tailor the behavior of certain SDK methods to better fit your use-cases.
