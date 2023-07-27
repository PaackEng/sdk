<?php

namespace Paack\Order\Logic;

use Paack\Order\Interfaces\ExtraDetailInterface;

class ExtraDetail implements ExtraDetailInterface {
    public string $name;
    public string $type;
    public string $value;

    /**
     * ExtraDetail constructor.
     * @param string $name
     * @param string $type
     * @param string $value
     */
    public function __construct(string $name, string $type, string $value) {
        $this->name = $name;
        $this->type = $type;
        $this->value = $value;
    }
}