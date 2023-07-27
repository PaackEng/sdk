<?php

namespace Paack\Api\Logic;

use GuzzleHttp\Client;
use Paack\Api\Logic\APIClient;
use Paack\Cache\PaackCache;
use Paack\Cache\PaackCacheInterces;
use Paack\Config\PaackConfig;
use Paack\Api\Interfaces\TokenHandlerInterfaces;

class TokenHandler extends PaackConfig implements TokenHandlerInterfaces {

    /**
     * @var string
     */
    protected string $clientSecret;

    /**
     * @var string
     */
    protected string $clientId;

    /**
     * @var string
     */
    protected string $audience;

    /**
     * @var string
     */
    protected string $oauth2Url;
    /**
     * @var PaackCache
     */
    protected PaackCache $cache;

    const KEY_CACHE = 'paack_token';

    /**
     * TokenHandler constructor.
     * @param string $client_secret
     * @param string $client_id
     * @param string $audience
     * @param string $oauth2_url
     */
    public function __construct($client_id,  $client_secret,  $audience,  $oauth2_url) {
        $this->clientId = $client_id;
        $this->clientSecret = $client_secret;
        $this->audience = $audience;
        $this->oauth2Url = $oauth2_url;
        $this->cache = new PaackCache();
    }

    /**
     * @return mixed|void
     */
    public function retrieveToken()  {
        return $this->getOathToken();
    }

    protected function getOathToken() {
        $header = Header::defaultHeader();
        $post = new APIClient($header);

        if (!$this->cache->existId(self::KEY_CACHE)) {
            $_SESSION['payload'] = $this->getPayload();
            $_SESSION['oauthUrl'] = $this->oauth2Url;

            $response = $post->post($this->oauth2Url, $this->getPayload());
            $response = $this->normalizer($response['body']);
            $this->cache->setCache(self::KEY_CACHE, $response['access_token'], $response['expires_in']);
        }
        return $this->cache->getCache(self::KEY_CACHE);

    }

    /**
     * @return array
     */
    public function getPayload() : array {
        return  [
            'audience' => $this->audience,
            'client_id' =>  $this->clientId,
            'client_secret' => $this->clientSecret,
            'grant_type' => self::GRANT_TYPE,
        ];
    }

    protected function normalizer($content) {
        return json_decode( $content, TRUE );
    }


}
