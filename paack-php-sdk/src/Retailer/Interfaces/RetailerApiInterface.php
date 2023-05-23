<?php

namespace Paack\Retailer\Interfaces;

interface RetailerApiInterface {
    public function getRetailerLocation(array $alias);
    public function createRetailerLocation(RetailerSchemaInterface $retailer);
    public function updateRetailerLocation(string $retailer_id, RetailerSchemaInterface $retailer);
    public function deleteRetailerLocation(string $retailer_id);
}