package resources

type Domain string
type Url string
type State bool
type LabelFormat int
type PostalCodePattern string
type OrderModel int

const (
	SdkVersion          string      = "1.0.0"
	PdfLabel            LabelFormat = 0
	ZplLabel            LabelFormat = 1
	SingleZplLabel      LabelFormat = 1
	MultiZplLabel       LabelFormat = 2
	WarehouseModel      OrderModel  = 0
	StoreModel          OrderModel  = 1
	Authenticated       State       = true
	Unauthenticated     State       = false
	StagingDomain       Domain      = "https://ggl-stg-gcp-gw"
	ProductionDomain    Domain      = "https://ggl-pro-gcp-gw"
	StagingOAuthUrl     Url         = "https://paack-hq-staging.eu.auth0.com/oauth/token"
	ProductionOAuthUrl  Url         = "https://paack-hq-production.eu.auth0.com/oauth/token"
	StagingConfigUrl    Url         = "https://retailers-config-3tyqi7b7ta-ew.a.run.app/configs/python-sdk"
	ProductionConfigUrl Url         = "https://retailers-config-3tyqi7b7ta-ew.a.run.app/configs/python-sdk"
	DateFormat          string      = "2006-01-02"
	TimeFormat          string      = "15:04:05"
	DateTimeFormat      string      = "2006-01-02 15:04:05"
	DateTimeISOFormat   string      = "2006-02-01T15:04:05Z"
	EmailPattern        string      = "^[-!#-\\'*+\\/-9=?^-~]+(?:\\.[-!#-\\'*+\\/-9=?^-~]+)*@[-!#-\\'*+\\/-9=?^-~]+(?:\\.[-!#-\\'*+\\/-9=?^-~]+)+$"
	PhonePattern        string      = "^(?:(?:\\(?(?:00|\\+)([1-4]\\d\\d|[1-9]\\d?)\\)?)?[\\-\\.\\ \\\\\\/]?)?((?:\\(?\\d{1,}\\)?[\\-\\.\\ \\\\\\/]?){0,})(?:[\\-\\.\\ \\\\\\/]?(?:#|ext\\.?|extension|x)[\\-\\.\\ \\\\\\/]?(\\d+))?$"
)

var CountryCode = []string{"ES", "FR", "GB", "PT", "IT"}
var CustomerLanguage = []string{"es", "pt", "en", "fr", "it"}
var WeightUnits = []string{"mg", "g", "kg"}
var LengthUnits = []string{"mm", "cm", "m"}

var PostalCodePatternPerCountry = map[string]string{
	"ES": "^(?:0[1-9]|[1-4]\\d|5[0-2])\\d{3}$",
	"GB": "^(?:0[1-9]|[1-4]\\d|5[0-2])\\d{3}$",
	"FR": "^(?:[0-8]\\d|9[0-8])\\d{3}$",
	"PT": "^[1-9][0-9]{3}-[0-9]{3}$",
	"IT": "^\\d{5}$",
}

var TimezonesPerCountry = map[string]string{
	"GB": "Europe/London",
	"FR": "Europe/Paris",
	"IT": "Europe/Rome",
	"PT": "Europe/Lisbon",
}

var TimezonesPerES = map[string]string{
	"peninsula":      "Europe/Madrid",
	"canary_islands": "Atlantic/Canary",
}
var CanaryIslandsPostcodePrefixes = []string{"35", "38"}

var CountryAbreviations = map[string]string{
	"ES":             "ES",
	"ESP":            "ES",
	"SPAIN":          "ES",
	"ESPANA":         "ES",
	"ESPAÑA":         "ES",
	"GIRONA":         "ES",
	"CATALUNYA":      "ES",
	"UK":             "GB",
	"GB":             "GB",
	"GBR":            "GB",
	"LONDON":         "GB",
	"ENGLAND":        "GB",
	"UNITEDKINGDOM":  "GB",
	"UNITED KINGDOM": "GB",
	"FR":             "FR",
	"FRA":            "FR",
	"PARIS":          "FR",
	"FRANCE":         "FR",
	"PT":             "PT",
	"PRT":            "PT",
	"LISBOA":         "PT",
	"LISBON":         "PT",
	"PORTUGAL":       "PT",
	"IT":             "IT",
	"ITA":            "IT",
	"ROMA":           "IT",
	"ITALY":          "IT",
	"ITALIA":         "IT",
	"ITALIAN":        "IT",
}
