<?php

namespace Paack\Retailer\Logic;

use Exception;
use Paack\Api\Interfaces\BaseApi;
use Paack\Config\PaackConfig;
use Paack\Retailer\Interfaces\RetailerApiInterface;
use Paack\Retailer\Interfaces\RetailerSchemaInterface;

class RetailerApi implements RetailerApiInterface {

    protected PaackConfig $configuration;
    protected BaseApi $apiClient;
    protected string $endpoint;


    public function __construct(BaseApi $api)
    {
        $this->apiClient = $api;
        $this->configuration = $api->configuration;
        $this->endpoint = $this->configuration->retailerEndpoint();
    }

    /**
     * @throws Exception
     */
    public function getRetailerLocation(array $alias)
    {
        $parameters = http_build_query($alias);
        $url = $this->getPath() .'?'. $parameters;
        return $this->apiClient->get($url, []);
    }

    /**
     * @throws Exception
     */
    public function createRetailerLocation(RetailerSchemaInterface $retailer)
    {
        $data = $this->createToNormalizationRetailer($retailer);
        $url = $this->getPath();
        return $this->apiClient->post($url, $data);
    }

    /**
     * @throws Exception
     */
    public function updateRetailerLocation(string $retailer_id, RetailerSchemaInterface $retailer)
    {
        $id = [
            'retailer_id' => $retailer_id,
        ];
        $content = $this->getRetailerLocation($id);

        $update = $this->createToNormalizationRetailer($retailer);
        $response = array_merge($content, $update);

        $data['address'] = $response['address'];
        $data['alias'] = $response['alias'];
        $data['type'] = $response['type'];


        $url = $this->getPath() . '/'. $retailer_id;
        return $this->apiClient->patch($url, $data);
    }

    public function deleteRetailerLocation(string $retailer_id)
    {
        $url = $this->getPath() . '/'. $retailer_id;
        return $this->apiClient->delete($url);
    }

    /**
     * @throws ErrorException
     * @throws Exception
     */
    protected function createToNormalizationRetailer(object $object) {
        if (is_object($object)) {
            return json_decode(json_encode( (array) $object ), TRUE);
        }
        throw new Exception("There isn't an RetailerSchemaInterface");
    }

    /**
     * @return string
     * @throws \Exception
     */
    private function getPath() {
        return $this->configuration->resourcesDomain() . $this->configuration->retailerEndpoint();
    }
}
