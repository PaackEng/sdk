<?php

namespace Paack\Api\Logic;

use GuzzleHttp\Client;
use Paack\Api\Interfaces\BaseApi;
use Paack\Config\PaackConfig;

class APIClient implements BaseApi {
    public array $header;
    public float $connection_timeout;
    public float $read_timeout;
    public string $environment;

    public PaackConfig $configuration;
    public Client $client;

    protected ?array $client_config = [];

    public function __construct(
        array $header,
        float $connection_timeout = 60.05,
        float $read_timeout = 60.05,
        string $env = 'staging') {

        $this->header = $header;
        $this->connection_timeout = $connection_timeout;
        $this->read_timeout = $read_timeout;
        $this->environment = $env;

        $this->configuration = new PaackConfig($env);
        $this->client = new Client();

    }

    /**
     * @param string $url
     * @param array $params
     * @return mixed
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    public function get(string $url, array $params): mixed {
        return $this->request('GET', $url, $params);
    }

    /**
     * @param string $url
     * @param array $payload
     * @return object
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    public function post(string $url, array $payload)
    {
        return $this->request('POST', $url, $payload);
    }

    public function put(string $url, array $params)
    {
        return $this->request('put', $url, $params);
    }

    public function patch(string $url, array $params)
    {
        return $this->request('patch', $url, $params);
    }

    public function delete(string $url): mixed
    {
        return $this->request('DELETE', $url);
    }

    public function getConfiguration(): array
    {
        $default = [
            'track_redirects' => true
        ];

        return array_merge($default, $this->client_config);

    }

    public function setConfiguration(array $config): array
    {
        $this->client_config = $config;
        return $this->client_config;
    }

    /**
     * @param $method
     * @param $url
     * @param $payload
     * @return mixed
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    public function request($method, $url, $payload, $retry_count = 1): mixed
    {
        $config = [
           'body' => $payload ? json_encode($payload) : NULL,
           'headers' => $this->header,
        ];

        $config_merges = array_merge($config, $this->getConfiguration());
        $request = $this->client->request($method, $url, $config_merges);

        if ((500 <= $request->getStatusCode() || $request->getStatusCode() >= 600) && $retry_count <= 3) {
            $pos = 1 + $retry_count;
            self::request($method, $url, $payload, $pos++);
        }
        if ($request->getStatusCode() == 401) {
            $request = $this->post($_SESSION['oauthUrl'], $_SESSION['payload']);
         }

        try {
            $result = [
                'response' => $request->getStatusCode(),
                'body' => $request->getBody()->getContents()
            ];
        }
        catch (\Exception $exception) {
            $result = [
                'response' => $request->getStatusCode(),
                'body' => $exception->getMessage()
            ];
        }

        return $result;
    }
}
