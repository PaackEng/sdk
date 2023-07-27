<?php


use Paack\Init;
use PHPUnit\Framework\TestCase;

class TestDelivery extends TestCase
{

    private Init $init;

    public function setUp(): void
    {
        $this->init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), 'staging');
    }

    /**
     * @throws Exception
     */
    public function testDeliveryVerifications()
    {
        $id = '200000V3009948';
        $content = $this->init->delivery->deliveryVerificationsRequest($id);
        $assets =  $content;
        $this->assertNotNull($content['body']);
    }


}

