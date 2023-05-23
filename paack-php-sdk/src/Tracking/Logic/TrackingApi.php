<?php

namespace Paack\Tracking\Logic;

use DateTime;
use Paack\Api\Interfaces\BaseApi;
use Paack\Config\Mixes;
use Paack\Config\PaackConfig;
use Paack\Tracking\Interfaces\TrackingApiInterface;

class TrackingApi implements TrackingApiInterface {

    protected const ENTITY_TYPE = 'tracking_pull';

    public PaackConfig $configuration;
    public BaseApi $apiClient;

    public function __construct(BaseApi $api) {
        $this->apiClient = $api;
        $this->configuration = $api->configuration;
    }

    public function orderStatusList(array $listIds, DateTime $start, DateTime $end, int $count = 50): array
    {
        $order_list = implode(',', $listIds);
        $path = $this->getPath('status_list');

        $data = [
            'externalIds' => $order_list,
            'start' => $start->format(Mixes::DATE_TIME_FORMAT),
            'end' => $start->format(Mixes::DATE_TIME_FORMAT),
            'count' => $count
        ];

        $endpoint = $path . $this->processDataString($data);

        return $this->apiClient->get($endpoint, []);
    }

    public function orderStatusGet(string $order_id): array
    {
        $path = $this->getPath('last_status');
        $endpoint = str_replace("{externalIds}",$order_id, $path);
        return $this->apiClient->get($endpoint, []);
    }

    public function eventTranslationGet($lang) : array
    {
        $path = $this->getPath('translation');
        $endpoint = str_replace("{lang}",$lang, $path);
        return $this->apiClient->get($endpoint, []);
    }

    private function getPath($type): string
    {
        $endpoint = $this->configuration->getConfigResources(self::ENTITY_TYPE);
        return $this->configuration->getConfigTracking() . $endpoint[$type];
    }

    private function processDataString(array $data): string
    {
        return http_build_query($data);
    }
}
