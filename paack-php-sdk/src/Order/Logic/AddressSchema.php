<?php

namespace Paack\Order\Logic;

use Paack\Order\Interfaces\AddressSchemaInterface;

class AddressSchema implements AddressSchemaInterface {
    public string $country;
    public string $city;
    public string $line1;
    public string $post_code;
    public string $line2;
    public string $county;
    public string $instructions;


    public function __construct(
        string $city,
        string $country,
        string $line1,
        string $post_code,
        string $line2,
        string $county,
        string $instructions
    )
    {
        $this->city = $city;
        $this->country = $this->processCountry($country);
        $this->line1 = $line1;
        $this->line2 = $line2;
        $this->post_code = $post_code;
        $this->county = $county;
        $this->instructions = $instructions;

    }

    private function processCountry($string): string
    {
        return strtoupper(substr($string, 0, 2));
    }
}
