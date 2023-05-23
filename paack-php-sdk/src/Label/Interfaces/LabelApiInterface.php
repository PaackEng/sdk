<?php

namespace Paack\Label\Interfaces;

use Paack\Label\Interfaces\LabelSchemaInterface;
use Paack\Label\Interfaces\LabelFormat;


interface LabelApiInterface  {

    /**
     * @param \Paack\Label\Interfaces\LabelSchemaInterface $LabelSchemaInterface
     * @return mixed
     */
    public function LabelCreate(LabelSchemaInterface $LabelSchemaInterface, LabelFormat $label_format);

    /**
     * @param \Paack\Label\Interfaces\LabelSchemaInterface $LabelSchemaInterface
     * @return mixed
     */
    public function LabelCreateByParcel(LabelSchemaInterface $LabelSchemaInterface, LabelFormat $label_format);
}

