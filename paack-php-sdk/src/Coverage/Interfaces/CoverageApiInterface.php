<?php

namespace Paack\Coverage\Interfaces;

interface CoverageApiInterface {
    public function checkCoverage();
    public function checkCoveragePostalCode(string $country, string $coverage_code);
    public function checkCoverageZone(string $country, string $coverage_zone);
}
