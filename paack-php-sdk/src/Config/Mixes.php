<?php

namespace Paack\Config;

interface Mixes {

    public const PDP_LABEL = 0;
    public const SINGLE_ZP_LABEL = 1;
    public const MULTI_ZP_LABEL = 2;
    public const WarehouseModel = 0;
    public const STORE_MODEL  = 1;
    public const AUTHENTICATED  = true;
    public const UNAUTHENTICATED = false;
    public const STAGING_DOMAIN = "https://ggl-stg-gcp-gw";
    //public const STAGING_DOMAIN = "https://api.shm.staging.paack.app";
    public const PRODUCTION_DOMAIN  = "https://ggl-pro-gcp-gw";
    public const STAGING_OAUTH_URL = "https://paack-hq-staging.eu.auth0.com/oauth/token";
    public const PRODUCTION_OAUTH_URL = "https://paack-hq-production.eu.auth0.com/oauth/token";
    public const PAACK_CONFIG_URL = "https://retailers-config-3tyqi7b7ta-ew.a.run.app/configs/python-sdk";
    public const DATE_FORMAT = "Y-m-d";
    public const TIME_FORMAT = "H:i:s";
    public const DATE_TIME_FORMAT = "Y-m-d\TH:i:sP";
    public const RETAILER_ENDPOINT = "/rls";
    public const RETAILER_STAGING = "https://api.shm.staging.paack.app";
    public const RETAILER_PRODUCTION = "https://api.oms.production.paack.app";
    public const EMAIL_PATTERN = "^[-!#-\\'*+\\/-9=?^-~]+(?:\\.[-!#-\\'*+\\/-9=?^-~]+)*@[-!#-\\'*+\\/-9=?^-~]+(?:\\.[-!#-\\'*+\\/-9=?^-~]+)+$";
    public const PHONE_PATTERN = "^(?:(?:\\(?(?:00|\\+)([1-4]\\d\\d|[1-9]\\d?)\\)?)?[\\-\\.\\ \\\\\\/]?)?((?:\\(?\\d{1,}\\)?[\\-\\.\\ \\\\\\/]?){0,})(?:[\\-\\.\\ \\\\\\/]?(?:#|ext\\.?|extension|x)[\\-\\.\\ \\\\\\/]?(\\d+))?$";

}
