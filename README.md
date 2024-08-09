# MeasurementProcessor

In IntelliJ:
1. run Main.java

REST API:
To use the application you need to
1. start the application by MeasurementApplication.java
2. execute the GET command e.g. via Postaman with the body
{
    "measurements": [
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:04:45Z", "value": 35.79},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:01:18Z", "value": 98.78},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:09:07Z", "value": 35.01},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:03:34Z", "value": 96.49},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:02:01Z", "value": 35.82},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:05:00Z", "value": 97.17},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:05:01Z", "value": 95.08},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:14:45Z", "value": 36.00},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:11:18Z", "value": 97.50},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:19:07Z", "value": 34.99},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:13:34Z", "value": 96.00},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:12:01Z", "value": 35.70},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:15:00Z", "value": 97.90},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:15:01Z", "value": 95.50},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:24:45Z", "value": 36.10},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:21:18Z", "value": 97.70},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:29:07Z", "value": 34.90},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:23:34Z", "value": 96.10},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:22:01Z", "value": 35.80},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:25:00Z", "value": 97.30},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:25:01Z", "value": 95.60},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:34:45Z", "value": 36.20},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:31:18Z", "value": 97.80},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:39:07Z", "value": 35.00},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:33:34Z", "value": 96.20},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:32:01Z", "value": 35.90},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:35:00Z", "value": 97.60},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:35:01Z", "value": 95.70},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:44:45Z", "value": 36.30},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:41:18Z", "value": 97.90},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:49:07Z", "value": 35.10},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:43:34Z", "value": 96.30},
    {"measurementType": "TEMP", "timestamp": "2017-01-03T10:42:01Z", "value": 36.00},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:45:00Z", "value": 97.40},
    {"measurementType": "SPO2", "timestamp": "2017-01-03T10:45:01Z", "value": 95.80}
    ],
    "startOfSampling": "2017-01-03T10:00:00Z"
}
