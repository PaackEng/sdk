<?php

namespace Paack\Order\Logic;

use DateTimeZone;
use Paack\Config\Mixes;
use Paack\Order\Interfaces\TimeSlotInterface;
use DateTime;

class TimeSlot implements TimeSlotInterface {
    public array $start;
    public array  $end;

    /**
     * TimeSlotInterface constructor.
     * @param \DateTime $start
     * @param \DateTime $end
     * @param bool $isUtc
     */
    public function __construct(DateTime $start, DateTime $end, bool $isUtc = TRUE)
    {
        // Set DateTime to UTC.
        if ($isUtc) {
            $start->setTimezone(new DateTimeZone("UTC"));
            $end->setTimezone(new DateTimeZone("UTC"));
        }

        $start_date =  $start->format($this->getConfigurationDate());
        $start_time =  $start->format($this->getConfigurationTime());

        $end_date =  $end->format($this->getConfigurationDate());
        $end_time =  $end->format($this->getConfigurationTime());

        $this->start =  ['date' => $start_date, 'time' => $start_time];
        $this->end =    ['date' => $end_date, 'time' => $end_time];

    }

    /**
     * @return string
     */
    public function getConfigurationDate(): string
    {
        return Mixes::DATE_FORMAT;
    }

    /**
     * @return string
     */
    public function getConfigurationTime(): string
    {
        return Mixes::TIME_FORMAT;
    }
}
