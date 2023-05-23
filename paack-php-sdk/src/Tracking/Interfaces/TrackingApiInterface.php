<?php

namespace Paack\Tracking\Interfaces;

use DateTime;

interface TrackingApiInterface {
    /**
     * @param array $listIds
     * @param DateTime $start
     * @param DateTime $end
     * @param int $count
     * @return mixed
     */
    public function orderStatusList(array $listIds, DateTime $start, DateTime $end, int $count = 50): array;

    /**
     * @param string $order_id
     * @return array
     */
    public function orderStatusGet(string $order_id): array;

    /**
     * @param $lang
     * @return array
     */
    public function eventTranslationGet($lang): array;

}