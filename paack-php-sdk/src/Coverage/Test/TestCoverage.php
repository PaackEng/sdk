<?php

use Paack\Init;
use PHPUnit\Framework\TestCase;

class TestCoverage extends TestCase {

    protected array $obj_array = [];
    private Init $init;

    public function setUp(): void {
        $this->init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), 'staging');
    }

    public function testCheckCoverage() {
        $assets = $this->init->coverage->checkCoverage();
        $this->assertNotNull($assets);
    }

    /**
     * @throws Exception
     */
    public function testCheckCoveragePostalCode() {
        $assets = $this->init->coverage->checkCoveragePostalCode("FF", "38290");
        $this->assertNotNull($assets);
    }

    /**
     * @throws Exception
     */
    public function testCheckCoveragePostalCodeFail() {
        $this->init->coverage->checkCoveragePostalCode("abc", "06430");
        $this->assertNull(NULL);
    }

    /**
     * @throws Exception
     */
    public function testCheckCoverageZone() {
        $assets = $this->init->coverage->checkCoverageZone("pt", "2680");
        $this->assertNotNull($assets);
    }

    /**
     * @throws Exception
     */
    public function testCheckCoverageZoneFail() {
        try {
            $assets = $this->init->coverage->checkCoverageZone("abc", "2680");
        }
        catch (Exception $exception) {
            $this->assertNull(NULL);
        }
    }

}

