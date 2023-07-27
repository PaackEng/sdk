<?php

namespace Paack\Delivery\Logic;

use Paack\Api\Interfaces\BaseApi;
use Paack\Config\PaackConfig;
use Paack\Delivery\Interfaces\DeliveryVerificationsInterface;

class DeliveryVerifications implements DeliveryVerificationsInterface {
    public const ENTITY_TYPE = 'pod';
    public PaackConfig $configuration;
    public BaseApi $apiClient;

    public function __construct(BaseApi $api)
    {
        $this->apiClient = $api;
        $this->configuration = $api->configuration;
    }

    /**
     * @throws DeliveryVerificationsException
     */
    public function deliveryVerificationsRequest(string $external_id)
    {
        if (strlen($external_id) > 128) {
            throw new DeliveryVerificationsException('External ID should have 128 characters');
        }
        $path = $this->getPath();
        $endpoint = str_replace("{external_id}", $external_id, $path);

        return $this->apiClient->get($endpoint, []);
    }

    /**
     * @throws \Exception
     */
    private function getPath(): string
    {
        $endpoint = $this->configuration->getConfigResources(self::ENTITY_TYPE);
        return $this->configuration->getConfigDelivery() . $endpoint;
    }

}
