<?php

namespace Paack;

use Paack\Config\PaackConfig;
use Paack\Api\Interfaces\TokenHandlerInterfaces;
use Paack\Api\Logic\APIClient;
use Paack\Api\Logic\TokenHandler;
use Paack\Coverage\Logic\CoverageApi;
use Paack\Delivery\Logic\DeliveryVerifications;
use Paack\Label\Logic\LabelApi;
use Paack\Order\Logic\OrderApi;
use Paack\Api\Logic\Header;
use Paack\Retailer\Logic\RetailerApi;
use Paack\Tracking\Logic\TrackingApi;

/**
 * Class Paack
 * @package Paack
 */
final class Init  {
    public APIClient $api;

    public string $clientId;

    public string $clientSecret;

    public string $domain;

    public OrderApi $order;

    public PaackConfig $configuration;

    public LabelApi $label;

    public TrackingApi $tracking;

    public DeliveryVerifications $delivery;

    public RetailerApi $retailer;

    public CoverageApi $coverage;


    public function __construct(string $client_id, string $client_secret, string $domain) {
        $this->clientId = $client_id;
        $this->clientSecret = $client_secret;
        $this->domain = $domain;
        // Load configuration.
        $this->configuration = new PaackConfig($this->domain);
        $token = $this->getToken()->retrieveToken();
        $this->api = $this->getApi($token);

        // It instance of order.
        $this->order = new OrderApi($this->api);
        // It instance of label.
        $this->label = new LabelApi($this->api);
        // It instance of tracking.
        $this->tracking = new TrackingApi($this->api);
        // It instance of delivery
        $this->delivery = new DeliveryVerifications($this->api);
        // It instance of retailer
        $this->retailer = new RetailerApi($this->api);
        // It instance of coverage.
        $this->coverage = new CoverageApi($this->api);
    }

    public function getApi($token): APIClient
    {
        $header = Header::defaultHeader($token);
        return new APIClient($header,  60.0, 60.4, $this->domain);
    }

    public function getToken(): TokenHandler
    {
        return new TokenHandler(
            $this->clientId,
            $this->clientSecret,
            $this->configuration->authenticationAudience(),
            $this->configuration->authenticationUrl()
        );
    }

}
