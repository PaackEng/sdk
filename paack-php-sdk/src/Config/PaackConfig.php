<?php

namespace Paack\Config;


use Exception;
use GuzzleHttp\Client;
use Paack\Cache\PaackCache;
use Paack\Config\Mixes;

class PaackConfig implements  Mixes {
    const PAACK_CONFIG = 'paack_config';
    /**
     * @var string
     */
    public string $environment;

    /**
     * @var PaackCache
     */
    protected PaackCache $cache;

    /**
     * PaackConfig constructor.
     * @param string $env
     */
    public function __construct(string $env = 'staging') {
        $this->environment = $env;
        $this->cache = new PaackCache();
    }

    /**
     * @return mixed
     * @throws Exception
     */
     public  function getConfigOrder(): mixed {
         $data = $this->config();
         if (!empty($data['result']['data_config']['domain']['order'][$this->environment])) {
             return $data['result']['data_config']['domain']['order'][$this->environment];
         }
         else {
             throw new Exception("There hasn't been a position to order" );
         }
     }

    /**
     * @return mixed
     * @throws Exception
     */
    public  function getConfigLabel(): mixed {
        $data = $this->config();

        if (!empty($data['result']['data_config']['domain']['label'][$this->environment])) {
            return $data['result']['data_config']['domain']['label'][$this->environment];
        }
        else {
            throw new Exception("There hasn't been a position to label" );
        }
    }

    /**
     * @return mixed
     * @throws Exception
     */
    public  function getConfigTracking(): mixed {
        $data = $this->config();
        if (!empty($data['result']['data_config']['domain']['tracking_pull'][$this->environment])) {
            return $data['result']['data_config']['domain']['tracking_pull'][$this->environment];
        }
        else {
            throw new Exception("There hasn't been a position to tracking" );
        }
    }

    /**
     * @return mixed
     * @throws Exception
     */
    public  function getConfigDelivery(): mixed {
        $data = $this->config();
        if (!empty($data['result']['data_config']['domain']['pod'][$this->environment])) {
            return $data['result']['data_config']['domain']['pod'][$this->environment];
        }
        else {
            throw new Exception("There hasn't been a position to tracking" );
        }
    }

    /**
     * @return mixed
     * @throws Exception
     */
    public  function getConfigCoverage(): mixed {
        $data = $this->config();
        if (!empty($data['result']['data_config']['domain']['coverage'][$this->environment])) {
            return $data['result']['data_config']['domain']['coverage'][$this->environment];
        }
        else {
            throw new Exception("There hasn't been a position to coverage" );
        }
    }

    /**
     * @param $type
     * @return mixed
     * @throws Exception
     */
    public  function getConfigResources(string $type): mixed {
        $data = $this->config();
        if (!empty($data['result']['data_config']['resources'][$type])) {
            return $data['result']['data_config']['resources'][$type];
        }
        else {
            throw new Exception("There hasn't been a position to resources" );
        }
    }

    /**
     * @return mixed
     */
     protected function config(): mixed {
         if (!$this->cache->existId(self::PAACK_CONFIG)) {
             $client = new Client();
             $content = $client->request('GET', Mixes::PAACK_CONFIG_URL);
             try {
                 $result = json_decode($content->getBody(), TRUE);
                 $this->cache->setCache(self::PAACK_CONFIG, $result, 0);
             }
             catch (\Exception $exception) {
                 $result = $exception->getMessage();
             }
         }
         else {
            $result =  $this->cache->getCache(self::PAACK_CONFIG);
         }

         return $result;
     }

    /**
     * @return string
     */
     public function authenticationUrl(): string {
         if ($this->environment == 'staging') {
             return self::STAGING_OAUTH_URL;
         }
         return self::PRODUCTION_OAUTH_URL;
     }

    /**
     * @return string
     */
    public function authenticationAudience(): string {
        if ($this->environment == 'staging') {
            return self::STAGING_DOMAIN;
        }
        return self::PRODUCTION_DOMAIN;
    }

    public function resourcesDomain(): string {
        if ($this->environment == 'staging') {
            return self::RETAILER_STAGING;
        }
        return self::RETAILER_PRODUCTION;
    }

    public function retailerEndpoint(): string {
        return self::RETAILER_ENDPOINT;
    }


}