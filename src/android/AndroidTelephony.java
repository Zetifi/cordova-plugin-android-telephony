package com.zetifi.androidtelephony;

import org.apache.cordova.CordovaWebView;

import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.provider.Settings;

import android.util.Log;
import android.content.Context;
import android.telephony.TelephonyManager;

import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthTdscdma;
import android.telephony.TelephonyManager;
import android.telephony.ServiceState;
import android.util.Log;

public class AndroidTelephony extends CordovaPlugin {

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    if ("getCarrierInfo".equals(action)) {
      return this.getCarrierInfo(args, callbackContext);
    } else if ("getAllCellInfo".equals(action)) {
      JSONObject s = new JSONObject();
      s.put("allCellInfo", this.getAllCellInfo());

      callbackContext.success(s);
      return true;
    } else {
      return false;
    }
  }

  public JSONObject getAllCellInfo() {
    JSONObject result = new JSONObject();

    try {
      TelephonyManager telephonyManager = (TelephonyManager) this.cordova.getActivity()
          .getSystemService(Context.TELEPHONY_SERVICE);

      if (telephonyManager == null) {
        result.put("error", "TelephonyManager not available");
        return "{}";
      }

      List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();

      if (cellInfoList == null || cellInfoList.isEmpty()) {
        result.put("cellInfoList", new JSONArray());
        return "{}";
      }

      JSONArray cellInfoJsonArray = new JSONArray();

      for (CellInfo cellInfo : cellInfoList) {
        JSONObject cellInfoJson = new JSONObject();

        cellInfoJson.put("registered", cellInfo.isRegistered());
        cellInfoJson.put("timestamp", cellInfo.getTimeStamp());

        if (cellInfo instanceof CellInfoGsm) {
          CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;

          CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
          CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();

          cellInfoJson.put("type", "GSM");
          cellInfoJson.put("cellIdentity", cellIdentityToJson(cellIdentityGsm));
          cellInfoJson.put("cellSignalStrength", cellSignalStrengthToJson(cellSignalStrengthGsm));

        } else if (cellInfo instanceof CellInfoLte) {
          CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;

          CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
          CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();

          cellInfoJson.put("type", "LTE");
          cellInfoJson.put("cellIdentity", cellIdentityToJson(cellIdentityLte));
          cellInfoJson.put("cellSignalStrength", cellSignalStrengthToJson(cellSignalStrengthLte));

        } else if (cellInfo instanceof CellInfoWcdma) {
          CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;

          CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
          CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();

          cellInfoJson.put("type", "WCDMA");
          cellInfoJson.put("cellIdentity", cellIdentityToJson(cellIdentityWcdma));
          cellInfoJson.put("cellSignalStrength", cellSignalStrengthToJson(cellSignalStrengthWcdma));

        } else if (cellInfo instanceof CellInfoCdma) {
          CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;

          CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
          CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();

          cellInfoJson.put("type", "CDMA");
          cellInfoJson.put("cellIdentity", cellIdentityToJson(cellIdentityCdma));
          cellInfoJson.put("cellSignalStrength", cellSignalStrengthToJson(cellSignalStrengthCdma));

        } else if (cellInfo instanceof CellInfoNr) {
          CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;

          CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
          CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr) cellInfoNr
              .getCellSignalStrength();

          cellInfoJson.put("type", "NR");
          cellInfoJson.put("cellIdentity", cellIdentityToJson(cellIdentityNr));
          cellInfoJson.put("cellSignalStrength", cellSignalStrengthToJson(cellSignalStrengthNr));

        } else if (cellInfo instanceof CellInfoTdscdma) {
          CellInfoTdscdma cellInfoTdscdma = (CellInfoTdscdma) cellInfo;

          CellIdentityTdscdma cellIdentityTdscdma = cellInfoTdscdma.getCellIdentity();
          CellSignalStrengthTdscdma cellSignalStrengthTdscdma = cellInfoTdscdma.getCellSignalStrength();

          cellInfoJson.put("type", "TDSCDMA");
          cellInfoJson.put("cellIdentity", cellIdentityToJson(cellIdentityTdscdma));
          cellInfoJson.put("cellSignalStrength", cellSignalStrengthToJson(cellSignalStrengthTdscdma));

        } else {
          cellInfoJson.put("type", "Unknown");
        }

        cellInfoJsonArray.put(cellInfoJson);
      }

      result.put("cellInfoList", cellInfoJsonArray);

    } catch (Exception e) {
      try {
        result.put("error", e.toString());
      } catch (JSONException jsonException) {
        // Ignore
      }
    }

    return result;
  }

  private boolean getCarrierInfo(JSONArray args, CallbackContext callbackContext) throws JSONException {
    TelephonyManager tm = (TelephonyManager) this.cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);

    JSONObject r = new JSONObject();
    r.put("carrierName", tm.getNetworkOperatorName());
    r.put("isoCountryCode", tm.getNetworkCountryIso());
    r.put("mccmnc", tm.getNetworkOperator());
    callbackContext.success(r);
    return true;
  }

  private static JSONObject cellIdentityToJson(CellIdentity cellIdentity) throws JSONException {
    JSONObject json = new JSONObject();

    if (cellIdentity instanceof CellIdentityGsm) {
      CellIdentityGsm cellIdentityGsm = (CellIdentityGsm) cellIdentity;
      json.put("mcc", cellIdentityGsm.getMccString());
      json.put("mnc", cellIdentityGsm.getMncString());
      json.put("lac", cellIdentityGsm.getLac());
      json.put("cid", cellIdentityGsm.getCid());
      json.put("arfcn", cellIdentityGsm.getArfcn());
      json.put("bsic", cellIdentityGsm.getBsic());
      json.put("operatorAlphaLong", cellIdentityGsm.getOperatorAlphaLong());
      json.put("operatorAlphaShort", cellIdentityGsm.getOperatorAlphaShort());

    } else if (cellIdentity instanceof CellIdentityLte) {
      CellIdentityLte cellIdentityLte = (CellIdentityLte) cellIdentity;
      json.put("mcc", cellIdentityLte.getMccString());
      json.put("mnc", cellIdentityLte.getMncString());
      json.put("ci", cellIdentityLte.getCi());
      json.put("pci", cellIdentityLte.getPci());
      json.put("tac", cellIdentityLte.getTac());
      json.put("earfcn", cellIdentityLte.getEarfcn());
      json.put("bandwidth", cellIdentityLte.getBandwidth());
      json.put("operatorAlphaLong", cellIdentityLte.getOperatorAlphaLong());
      json.put("operatorAlphaShort", cellIdentityLte.getOperatorAlphaShort());

    } else if (cellIdentity instanceof CellIdentityWcdma) {
      CellIdentityWcdma cellIdentityWcdma = (CellIdentityWcdma) cellIdentity;
      json.put("mcc", cellIdentityWcdma.getMccString());
      json.put("mnc", cellIdentityWcdma.getMncString());
      json.put("lac", cellIdentityWcdma.getLac());
      json.put("cid", cellIdentityWcdma.getCid());
      json.put("psc", cellIdentityWcdma.getPsc());
      json.put("uarfcn", cellIdentityWcdma.getUarfcn());
      json.put("operatorAlphaLong", cellIdentityWcdma.getOperatorAlphaLong());
      json.put("operatorAlphaShort", cellIdentityWcdma.getOperatorAlphaShort());

    } else if (cellIdentity instanceof CellIdentityCdma) {
      CellIdentityCdma cellIdentityCdma = (CellIdentityCdma) cellIdentity;
      json.put("networkId", cellIdentityCdma.getNetworkId());
      json.put("systemId", cellIdentityCdma.getSystemId());
      json.put("basestationId", cellIdentityCdma.getBasestationId());
      json.put("latitude", cellIdentityCdma.getLatitude());
      json.put("longitude", cellIdentityCdma.getLongitude());

    } else if (cellIdentity instanceof CellIdentityNr) {
      CellIdentityNr cellIdentityNr = (CellIdentityNr) cellIdentity;
      json.put("mcc", cellIdentityNr.getMccString());
      json.put("mnc", cellIdentityNr.getMncString());
      json.put("nci", cellIdentityNr.getNci());
      json.put("pci", cellIdentityNr.getPci());
      json.put("tac", cellIdentityNr.getTac());
      json.put("nrarfcn", cellIdentityNr.getNrarfcn());
      json.put("bands", cellIdentityNr.getBands().toString());
      json.put("operatorAlphaLong", cellIdentityNr.getOperatorAlphaLong());
      json.put("operatorAlphaShort", cellIdentityNr.getOperatorAlphaShort());

    } else if (cellIdentity instanceof CellIdentityTdscdma) {
      CellIdentityTdscdma cellIdentityTdscdma = (CellIdentityTdscdma) cellIdentity;
      json.put("mcc", cellIdentityTdscdma.getMccString());
      json.put("mnc", cellIdentityTdscdma.getMncString());
      json.put("lac", cellIdentityTdscdma.getLac());
      json.put("cid", cellIdentityTdscdma.getCid());
      json.put("cpid", cellIdentityTdscdma.getCpid());
      json.put("uarfcn", cellIdentityTdscdma.getUarfcn());
      json.put("operatorAlphaLong", cellIdentityTdscdma.getOperatorAlphaLong());
      json.put("operatorAlphaShort", cellIdentityTdscdma.getOperatorAlphaShort());

    } else {
      json.put("error", "Unknown CellIdentity type");
    }

    return json;
  }

  private static JSONObject cellSignalStrengthToJson(CellSignalStrength signalStrength) throws JSONException {
    JSONObject json = new JSONObject();

    if (signalStrength instanceof CellSignalStrengthGsm) {
      CellSignalStrengthGsm signalStrengthGsm = (CellSignalStrengthGsm) signalStrength;
      json.put("dbm", signalStrengthGsm.getDbm());
      json.put("level", signalStrengthGsm.getLevel());
      json.put("asuLevel", signalStrengthGsm.getAsuLevel());
      json.put("timingAdvance", signalStrengthGsm.getTimingAdvance());

    } else if (signalStrength instanceof CellSignalStrengthLte) {
      CellSignalStrengthLte signalStrengthLte = (CellSignalStrengthLte) signalStrength;
      json.put("dbm", signalStrengthLte.getDbm());
      json.put("level", signalStrengthLte.getLevel());
      json.put("asuLevel", signalStrengthLte.getAsuLevel());
      json.put("rsrp", signalStrengthLte.getRsrp());
      json.put("rsrq", signalStrengthLte.getRsrq());
      json.put("rssnr", signalStrengthLte.getRssnr());
      json.put("cqi", signalStrengthLte.getCqi());
      json.put("timingAdvance", signalStrengthLte.getTimingAdvance());

    } else if (signalStrength instanceof CellSignalStrengthWcdma) {
      CellSignalStrengthWcdma signalStrengthWcdma = (CellSignalStrengthWcdma) signalStrength;
      json.put("dbm", signalStrengthWcdma.getDbm());
      json.put("level", signalStrengthWcdma.getLevel());
      json.put("asuLevel", signalStrengthWcdma.getAsuLevel());

    } else if (signalStrength instanceof CellSignalStrengthCdma) {
      CellSignalStrengthCdma signalStrengthCdma = (CellSignalStrengthCdma) signalStrength;
      json.put("dbm", signalStrengthCdma.getDbm());
      json.put("level", signalStrengthCdma.getLevel());
      json.put("asuLevel", signalStrengthCdma.getAsuLevel());
      json.put("cdmaDbm", signalStrengthCdma.getCdmaDbm());
      json.put("cdmaEcio", signalStrengthCdma.getCdmaEcio());
      json.put("evdoDbm", signalStrengthCdma.getEvdoDbm());
      json.put("evdoEcio", signalStrengthCdma.getEvdoEcio());
      json.put("evdoSnr", signalStrengthCdma.getEvdoSnr());

    } else if (signalStrength instanceof CellSignalStrengthNr) {
      CellSignalStrengthNr signalStrengthNr = (CellSignalStrengthNr) signalStrength;
      json.put("dbm", signalStrengthNr.getDbm());
      json.put("level", signalStrengthNr.getLevel());
      json.put("ssRsrp", signalStrengthNr.getSsRsrp());
      json.put("ssRsrq", signalStrengthNr.getSsRsrq());
      json.put("ssSinr", signalStrengthNr.getSsSinr());
      json.put("csiRsrp", signalStrengthNr.getCsiRsrp());
      json.put("csiRsrq", signalStrengthNr.getCsiRsrq());
      json.put("csiSinr", signalStrengthNr.getCsiSinr());

    } else if (signalStrength instanceof CellSignalStrengthTdscdma) {
      CellSignalStrengthTdscdma signalStrengthTdscdma = (CellSignalStrengthTdscdma) signalStrength;
      json.put("dbm", signalStrengthTdscdma.getDbm());
      json.put("level", signalStrengthTdscdma.getLevel());
      json.put("asuLevel", signalStrengthTdscdma.getAsuLevel());
      json.put("rscp", signalStrengthTdscdma.getRscp());

    } else {
      json.put("error", "Unknown CellSignalStrength type");
    }

    return json;
  }
}
