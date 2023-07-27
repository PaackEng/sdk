<?php

namespace Paack\Order\Logic;

use Paack\Order\Interfaces\CustomerSchemaInterface;

class CustomerSchema implements CustomerSchemaInterface {
    public string $first_name;
    public string $last_name;
    public string $email;
    public string $phone;
    public bool $has_gdpr_consent;
    public string $address;
    public string $language;

    public function __construct(
        string $first_name,
        string $last_name,
        string $email,
        string $phone,
        bool $has_gdpr_consent,
        string $county,
        string $language)
    {
        $this->first_name =  $first_name;
        $this->last_name = $last_name;
        $this->email = $email;
        $this->phone = $phone;
        $this->has_gdpr_consent = $has_gdpr_consent;
        $this->county = $county;
        $this->language = $language;
    }
}
