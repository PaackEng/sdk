<?php

namespace Paack\Order\Logic;

use Paack\Order\Interfaces\MoneySchemaInterface;

class MoneySchema implements MoneySchemaInterface {
    public float $amount;
    public string $currency;

    public function __construct(float $amount, string $currency)
    {
        $this->amount = $amount;
        $this->currency = $currency;
    }
}
