<?php

use Paack\Config\Mixes;
use Paack\Init;
use Paack\Label\Logic\LabelSchema;
use Paack\Order\Logic\AddressSchema;
use Paack\Order\Logic\CustomerSchema;
use Paack\Order\Logic\ExtraDetail;
use Paack\Order\Logic\MoneySchema;
use Paack\Order\Logic\OrderSchema;
use Paack\Order\Logic\ParcelSchema;
use Paack\Order\Logic\TimeSlot;
use PHPUnit\Framework\TestCase;
use Paack\Label\Interfaces\LabelFormat;

class TestTracking extends TestCase {

    protected array $obj_array = [];
    private Init $init;

    public function setUp(): void
    {
        $this->init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), 'staging');
    }

    public function testOrderStatusGet() {
        $asset = $this->init->tracking->orderStatusGet('200000V30090');
        $this->assertNull(NULL);
    }

    public function testeventTranslationGet() {
        $assets = $this->init->tracking->eventTranslationGet("en");
        $this->assertNull(NULL);
	}

	public function testOrderStatusList() {
        $order_list = [];
        $start = new DateTime('2022-12-14 08:00:00');
        $end = new DateTime('2023-01-16 10:00:00');
        $assets = $this->init->tracking->orderStatusList($order_list,$start, $end, 10 );
        $this->assertNull(NULL);
    }

}

