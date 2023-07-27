<?php

namespace Paack\Order\Logic;

use Exception;
use Paack\Order\Interfaces\ExtraDetailInterface;
use Paack\Order\Interfaces\ParcelSchemaInterface;

class ParcelSchema implements  ParcelSchemaInterface {
    public string $barcode;
    public float $height;
    public float $length;
    public float $width;
    public float $weight;
    public string $type;
    protected string $weight_unit;
    protected string $length_unit;
    protected string $volumetric_weight;
    private array|ExtraDetail $parcel_details;

    /**
     * ParcelSchema constructor.
     * @param string $barcode
     * @param float $height
     * @param float $length
     * @param float $width
     * @param float $weight
     * @param string $type
     * @param string $weight_unit
     * @param string $length_unit
     * @param float $volumetric_weight
     * @param array|ExtraDetailInterface $parcel_details
     * @throws Exception
     */
    public function __construct(
        string $barcode,
        float $height,
        float $length,
        float $width,
        float $weight,
        float $volumetric_weight,
        string $type = 'standard',
        string $weight_unit,
        string $length_unit,
        array|ExtraDetailInterface $parcel_details
        )
    {
        $this->barcode = $barcode;
        $this->height = $height;
        $this->length = $length;
        $this->width = $width;
        $this->weight = $weight;
        $this->volumetric_weight = $volumetric_weight;
        $this->type = $type;
        $this->weight_unit = strtolower($weight_unit);
        $this->length_unit = strtolower($length_unit);
        $this->parcel_details = $parcel_details;

        $this->normalizeToCm($this->length_unit);
        $this->normalizeToKg($this->weight_unit);

        if (strlen($this->height) < 0) {
            throw new Exception('Height must be greater or equal then zero');
        }

        if (strlen($this->width) < 0) {
            throw new Exception('Width must be greater or equal then zero');
        }
    }

    /**
     * @throws Exception
     */
    protected function NormalizeToKg(string $type) {
        if ($type == 'g') {
            $this->weight = $this->weight / 1000;
        }
        elseif ($type == 'mg') {
            $this->weight = $this->weight / 1000000;
        }
        elseif ($type == 'kg') {

        }
        else {
            throw new Exception("invalid unit to noramilize parcel's weight");
        }
    }

    /**
     * @throws Exception
     */
    protected function normalizeToCm(string $type) {
        // Change to TOCM
        if ($type == 'm') {
            $this->height = $this->height * 100;
            $this->length = $this->length * 100;
            $this->width = $this->width * 100;
        }
        elseif ($type == 'mm') {
            $this->height = $this->height / 100;
            $this->length = $this->length / 100;
            $this->width = $this->width / 100;
        }
        elseif ($type == 'cm') {

        }
        else {
            throw new Exception("invalid unit to noramilize parcel's dimensions");
        }
    }
}
