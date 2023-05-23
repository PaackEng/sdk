<?php

namespace Paack\Label\Logic;

use Exception;
use Paack\Api\Interfaces\BaseApi;
use Paack\Config\Mixes;
use Paack\Config\PaackConfig;
use Paack\Label\Interfaces\LabelFormat;
use Paack\Label\Interfaces\LabelSchemaInterface;

class LabelApi  {

    protected const ENTITY_TYPE = 'label';
    private PaackConfig $configuration;
    private BaseApi $apiClient;


    /**
     * LabelApi constructor.
     * @param BaseApi $api
     */
    public function __construct(BaseApi $api)
    {
        $this->apiClient = $api;
        $this->configuration = $api->configuration;
    }

    /**
     * @param LabelSchemaInterface $Label
     * @param LabelFormat $label_format
     * @return array|mixed
     * @throws Exception
     */
    public function LabelCreate(LabelSchemaInterface $Label, LabelFormat $label_format)
    {
        $data = $this->createToNormalizationLabel($Label);

        if ($label_format::SINGLEZPLE == LabelFormat::SINGLEZPLE) {
            $data['template_id'] = Mixes::SINGLE_ZP_LABEL;
        }
        elseif ($label_format::MULTIZPL == LabelFormat::MULTIZPL) {
            $data['template_id'] = Mixes::MULTI_ZP_LABEL;
        }
        else {
            unset($data['template_id']);
        }
        unset($data['template_id']);


       $response = $this->apiClient->post($this->getPath(), $data);
       return $this->LabelCreatePdf($response['body']);
       
        switch ($label_format) {
            case LabelFormat::SINGLEZPLE:
            case LabelFormat::MULTIZPL:
                return $this->LabelCreateZpl($response['body']);

            case LabelFormat::PDF:
                return $this->LabelCreatePdf($response['body']);
        }

        return throw new Exception($response['body'] . "response" . $response['status']);

    }

    /**
     * @throws Exception
     */
    public function LabelCreateByParcel(LabelSchemaInterface $label, LabelFormat $label_format)
    {
        if (is_array($label->order->parcels) && count($label->order->parcels) == 1) {
            return  $this->LabelCreate($label, $label_format);
        }
        elseif (is_array($label->order->parcels) && count($label->order->parcels) > 1) {
            $content = [];
            foreach ($label->order->parcels as $value) {
                unset($label->order->parcels);
                $label->order->parcels[] = $value;
                $content[] = $this->LabelCreate($label, $label_format);
            }
            return $content;
        }
        else {
            return  throw new Exception('Error this class');
        }

    }

    protected function LabelCreateZpl($content) {
        return [
            'IsZpl' => TRUE,
            'Label' => $content
        ];
    }

    protected function LabelCreatePdf($content) {
        return [
            'IsZpl' => FALSE,
            'Label' => base64_encode($content)
        ];
    }

    /**
     * @param object $object
     * @return false|mixed
     * @throws Exception
     */
    protected function createToNormalizationLabel(object $object) {

            $output = json_decode(json_encode( (array) $object ), TRUE);
            return reset($output);

    }

    /**
     * @return string
     * @throws \Exception
     */
    private function getPath() {
        return $this->configuration->getConfigLabel() . $this->configuration->getConfigResources(self::ENTITY_TYPE);
    }


}
