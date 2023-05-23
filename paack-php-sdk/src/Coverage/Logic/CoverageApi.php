<?php

namespace Paack\Coverage\Logic;

use Exception;
use Paack\Api\Interfaces\BaseApi;
use Paack\Config\PaackConfig;
use Paack\Coverage\Interfaces\CoverageApiInterface;

class CoverageApi implements CoverageApiInterface {

    const ENTITY_TYPE = 'coverage';
    private BaseApi $apiClient;
    private PaackConfig $configuration;

    public function __construct(BaseApi $api)
    {
        $this->apiClient = $api;
        $this->configuration = $api->configuration;
    }


    public function checkCoverage()
    {
        return $this->apiClient->get($this->getPath(), []);
    }

    /**
     * @throws Exception
     */
    public function checkCoveragePostalCode(string $country, string $coverage_code)
    {
        if (!$this->validationCountry($country)) {
       $data = ['country' => $country, 'coverage_code' => $coverage_code];
            $query_string = $this->processDataString($data);
            $endpoint = $this->getPath() .'?'. $query_string;

            try {
                $content = $this->apiClient->get($endpoint, []);
                  if (isset($content) && $content['body'] == 200) {
                    return TRUE;
                }
            }
            catch (Exception $exception) {
                return FALSE;
            }
        }
        else{
            return 'There the format is wrong';
        }
    }

    /**
     * @throws Exception
     */
    public function checkCoverageZone(string $country, string $coverage_zone)
    {
        if ($this->validationCountry($country)) {
            $data = ['country' => $country, 'coverage_zone' => $coverage_zone];
            $query_string = $this->processDataString($data);
            $endpoint = $this->getPath() .'?'. $query_string;

            return $this->apiClient->get($endpoint, []);
        }
        else{
            return 'There the format is wrong';
        }
    }

    private function validationCountry(string $country): bool
    {
        return strlen($country) === 0;
    }

    private function getPath(): string
    {
        $endpoint = $this->configuration->getConfigResources(self::ENTITY_TYPE);
        return $this->configuration->getConfigCoverage() . $endpoint;
    }

    private function processDataString(array $data): string
    {
        return http_build_query($data);
    }
}
