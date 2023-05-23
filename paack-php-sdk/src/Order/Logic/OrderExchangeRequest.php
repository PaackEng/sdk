<?php


namespace Paack\Order\Logic;


use Paack\Order\Interfaces\OrderExchangeRequestInterface;
use Paack\Order\Interfaces\OrderSchemaInterface;

class OrderExchangeRequest implements OrderExchangeRequestInterface
{
    public OrderSchemaInterface $direct;
    public OrderSchemaInterface $reverse;

    public function __construct(OrderSchemaInterface $direct_order, OrderSchemaInterface $reverse_order) {
        $this->direct = $direct_order;
        $this->reverse = $reverse_order;
    }
}
