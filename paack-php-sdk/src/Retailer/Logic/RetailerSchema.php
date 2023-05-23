<?php

namespace Paack\Retailer\Logic;

use Paack\Order\Interfaces\AddressSchemaInterface;
use Paack\Retailer\Interfaces\RetailerSchemaInterface;

class RetailerSchema implements RetailerSchemaInterface {

    public string $retailer_name;
    public string $location_name;
    public AddressSchemaInterface $address;
    public string $alias;
    public string $type;
    public string $retailer_id;

    public function __construct(string $retailer_name,  string $location_name, AddressSchemaInterface $address, string $alias, string $type, string $retailer_id)
    {
        $this->retailer_name = $retailer_name;
        $this->location_name = $location_name;
        $this->address = $address;
        $this->alias = $alias;
        $this->type = $type;
        $this->retailer_id = $retailer_id;
    }
}
