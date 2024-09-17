
var exec = require('cordova/exec');

var PLUGIN_NAME = 'AndroidTelephony';

var AndroidTelephony = {
  getAllCellInfo: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, PLUGIN_NAME, 'getAllCellInfo', []);
  }
};

module.exports = AndroidTelephony;
