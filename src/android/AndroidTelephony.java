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
        result.put("ERROR", "TelephonyManager not available");
        return result;
      }

      List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();

      if (cellInfoList == null || cellInfoList.isEmpty()) {
        result.put("CELL_INFO_LIST", new JSONArray());
        return result;
      }

      JSONArray cellInfoJsonArray = new JSONArray();

      for (CellInfo cellInfo : cellInfoList) {
        JSONObject cellInfoJson = new JSONObject();

        cellInfoJson.put("REGISTERED", cellInfo.isRegistered());
        cellInfoJson.put("TIMESTAMP", cellInfo.getTimeStamp());

        if (cellInfo instanceof CellInfoGsm) {
          CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;

          CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
          CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();

          cellInfoJson.put("TYPE", "GSM");
          cellIdentityToJson(cellIdentityGsm, cellInfoJson);
          cellSignalStrengthToJson(cellSignalStrengthGsm, cellInfoJson);

        } else if (cellInfo instanceof CellInfoLte) {
          CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;

          CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
          CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();

          cellInfoJson.put("TYPE", "LTE");
          cellIdentityToJson(cellIdentityLte, cellInfoJson);
          cellSignalStrengthToJson(cellSignalStrengthLte, cellInfoJson);

        } else if (cellInfo instanceof CellInfoWcdma) {
          CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;

          CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
          CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();

          cellInfoJson.put("TYPE", "WCDMA");
          cellIdentityToJson(cellIdentityWcdma, cellInfoJson);
          cellSignalStrengthToJson(cellSignalStrengthWcdma, cellInfoJson);

        } else if (cellInfo instanceof CellInfoCdma) {
          CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;

          CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
          CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();

          cellInfoJson.put("TYPE", "CDMA");
          cellIdentityToJson(cellIdentityCdma, cellInfoJson);
          cellSignalStrengthToJson(cellSignalStrengthCdma, cellInfoJson);

        } else if (cellInfo instanceof CellInfoNr) {
          CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;

          CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
          CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr) cellInfoNr
              .getCellSignalStrength();

          cellInfoJson.put("TYPE", "NR");
          cellIdentityToJson(cellIdentityNr, cellInfoJson);
          cellSignalStrengthToJson(cellSignalStrengthNr, cellInfoJson);

        } else if (cellInfo instanceof CellInfoTdscdma) {
          CellInfoTdscdma cellInfoTdscdma = (CellInfoTdscdma) cellInfo;

          CellIdentityTdscdma cellIdentityTdscdma = cellInfoTdscdma.getCellIdentity();
          CellSignalStrengthTdscdma cellSignalStrengthTdscdma = cellInfoTdscdma.getCellSignalStrength();

          cellInfoJson.put("TYPE", "TDSCDMA");
          cellIdentityToJson(cellIdentityTdscdma, cellInfoJson);
          cellSignalStrengthToJson(cellSignalStrengthTdscdma, cellInfoJson);

        } else {
          cellInfoJson.put("TYPE", "Unknown");
        }

        cellInfoJsonArray.put(cellInfoJson);
      }

      result.put("CELL_INFO_LIST", cellInfoJsonArray);

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

  private static void cellIdentityToJson(CellIdentity cellIdentity, JSONObject cellinfo) throws JSONException {
    if (cellIdentity instanceof CellIdentityGsm) {
      CellIdentityGsm cellIdentityGsm = (CellIdentityGsm) cellIdentity;
      cellinfo.put("CELL_IDENTITY_MCC", cellIdentityGsm.getMccString());
      cellinfo.put("CELL_IDENTITY_MNC", cellIdentityGsm.getMncString());
      cellinfo.put("CELL_IDENTITY_LAC", cellIdentityGsm.getLac());
      cellinfo.put("CELL_IDENTITY_CID", cellIdentityGsm.getCid());
      cellinfo.put("CELL_IDENTITY_ARFCN", cellIdentityGsm.getArfcn());
      cellinfo.put("CELL_IDENTITY_BSIC", cellIdentityGsm.getBsic());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_LONG", cellIdentityGsm.getOperatorAlphaLong());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_SHORT", cellIdentityGsm.getOperatorAlphaShort());

    } else if (cellIdentity instanceof CellIdentityLte) {
      CellIdentityLte cellIdentityLte = (CellIdentityLte) cellIdentity;
      cellinfo.put("CELL_IDENTITY_MCC", cellIdentityLte.getMccString());
      cellinfo.put("CELL_IDENTITY_MNC", cellIdentityLte.getMncString());
      cellinfo.put("CELL_IDENTITY_CI", cellIdentityLte.getCi());
      cellinfo.put("CELL_IDENTITY_PCI", cellIdentityLte.getPci());
      cellinfo.put("CELL_IDENTITY_TAC", cellIdentityLte.getTac());
      cellinfo.put("CELL_IDENTITY_EARFCN", cellIdentityLte.getEarfcn());
      cellinfo.put("CELL_IDENTITY_BANDWIDTH", cellIdentityLte.getBandwidth());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_LONG", cellIdentityLte.getOperatorAlphaLong());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_SHORT", cellIdentityLte.getOperatorAlphaShort());
    } else if (cellIdentity instanceof CellIdentityWcdma) {
      CellIdentityWcdma cellIdentityWcdma = (CellIdentityWcdma) cellIdentity;
      cellinfo.put("CELL_IDENTITY_MCC", cellIdentityWcdma.getMccString());
      cellinfo.put("CELL_IDENTITY_MNC", cellIdentityWcdma.getMncString());
      cellinfo.put("CELL_IDENTITY_LAC", cellIdentityWcdma.getLac());
      cellinfo.put("CELL_IDENTITY_CID", cellIdentityWcdma.getCid());
      cellinfo.put("CELL_IDENTITY_PSC", cellIdentityWcdma.getPsc());
      cellinfo.put("CELL_IDENTITY_UARFCN", cellIdentityWcdma.getUarfcn());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_LONG", cellIdentityWcdma.getOperatorAlphaLong());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_SHORT", cellIdentityWcdma.getOperatorAlphaShort());
    } else if (cellIdentity instanceof CellIdentityCdma) {
      CellIdentityCdma cellIdentityCdma = (CellIdentityCdma) cellIdentity;
      cellinfo.put("CELL_IDENTITY_NETWORK_ID", cellIdentityCdma.getNetworkId());
      cellinfo.put("CELL_IDENTITY_SYSTEM_ID", cellIdentityCdma.getSystemId());
      cellinfo.put("CELL_IDENTITY_BASE_STATION_ID", cellIdentityCdma.getBasestationId());
      cellinfo.put("CELL_IDENTITY_LONGITUDE", cellIdentityCdma.getLongitude());
      cellinfo.put("CELL_IDENTITY_LATITUDE", cellIdentityCdma.getLatitude());
    } else if (cellIdentity instanceof CellIdentityNr) {
      CellIdentityNr cellIdentityNr = (CellIdentityNr) cellIdentity;
      cellinfo.put("CELL_IDENTITY_MCC", cellIdentityNr.getMccString());
      cellinfo.put("CELL_IDENTITY_MNC", cellIdentityNr.getMncString());
      cellinfo.put("CELL_IDENTITY_NCI", cellIdentityNr.getNci());
      cellinfo.put("CELL_IDENTITY_PCI", cellIdentityNr.getPci());
      cellinfo.put("CELL_IDENTITY_TAC", cellIdentityNr.getTac());
      cellinfo.put("CELL_IDENTITY_NRARFCN", cellIdentityNr.getNrarfcn());
      cellinfo.put("CELL_IDENTITY_BANDS", cellIdentityNr.getBands().toString());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_LONG", cellIdentityNr.getOperatorAlphaLong());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_SHORT", cellIdentityNr.getOperatorAlphaShort());
    } else if (cellIdentity instanceof CellIdentityTdscdma) {
      CellIdentityTdscdma cellIdentityTdscdma = (CellIdentityTdscdma) cellIdentity;
      cellinfo.put("CELL_IDENTITY_MCC", cellIdentityTdscdma.getMccString());
      cellinfo.put("CELL_IDENTITY_MNC", cellIdentityTdscdma.getMncString());
      cellinfo.put("CELL_IDENTITY_LAC", cellIdentityTdscdma.getLac());
      cellinfo.put("CELL_IDENTITY_CID", cellIdentityTdscdma.getCid());
      cellinfo.put("CELL_IDENTITY_CPID", cellIdentityTdscdma.getCpid());
      cellinfo.put("CELL_IDENTITY_UARFCN", cellIdentityTdscdma.getUarfcn());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_LONG", cellIdentityTdscdma.getOperatorAlphaLong());
      cellinfo.put("CELL_IDENTITY_OPERATOR_ALPHA_SHORT", cellIdentityTdscdma.getOperatorAlphaShort());

    } else {
      cellinfo.put("CELL_IDENTITY_ERROR", "Unknown CellIdentity type");
    }
  }

  private static void cellSignalStrengthToJson(CellSignalStrength signalStrength, JSONObject cellinfo)
      throws JSONException {

    if (signalStrength instanceof CellSignalStrengthGsm) {
      CellSignalStrengthGsm signalStrengthGsm = (CellSignalStrengthGsm) signalStrength;
      cellinfo.put("CELL_SIGNAL_STRENGTH_DBM", signalStrengthGsm.getDbm());
      cellinfo.put("CELL_SIGNAL_STRENGTH_LEVEL", signalStrengthGsm.getLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_ASU_LEVEL", signalStrengthGsm.getAsuLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_TIMING_ADVANCE", signalStrengthGsm.getTimingAdvance());

    } else if (signalStrength instanceof CellSignalStrengthLte) {
      CellSignalStrengthLte signalStrengthLte = (CellSignalStrengthLte) signalStrength;
      cellinfo.put("CELL_SIGNAL_STRENGTH_DBM", signalStrengthLte.getDbm());
      cellinfo.put("CELL_SIGNAL_STRENGTH_LEVEL", signalStrengthLte.getLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_ASU_LEVEL", signalStrengthLte.getAsuLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_RSRP", signalStrengthLte.getRsrp());
      cellinfo.put("CELL_SIGNAL_STRENGTH_RSRQ", signalStrengthLte.getRsrq());
      cellinfo.put("CELL_SIGNAL_STRENGTH_RSSNR", signalStrengthLte.getRssnr());
      cellinfo.put("CELL_SIGNAL_STRENGTH_CQI", signalStrengthLte.getCqi());
      cellinfo.put("CELL_SIGNAL_STRENGTH_TIMING_ADVANCE", signalStrengthLte.getTimingAdvance());

    } else if (signalStrength instanceof CellSignalStrengthWcdma) {
      CellSignalStrengthWcdma signalStrengthWcdma = (CellSignalStrengthWcdma) signalStrength;
      cellinfo.put("CELL_SIGNAL_STRENGTH_DBM", signalStrengthWcdma.getDbm());
      cellinfo.put("CELL_SIGNAL_STRENGTH_LEVEL", signalStrengthWcdma.getLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_ASU_LEVEL", signalStrengthWcdma.getAsuLevel());

    } else if (signalStrength instanceof CellSignalStrengthCdma) {
      CellSignalStrengthCdma signalStrengthCdma = (CellSignalStrengthCdma) signalStrength;
      cellinfo.put("CELL_SIGNAL_STRENGTH_DBM", signalStrengthCdma.getDbm());
      cellinfo.put("CELL_SIGNAL_STRENGTH_LEVEL", signalStrengthCdma.getLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_ASU_LEVEL", signalStrengthCdma.getAsuLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_CDMA_DBM", signalStrengthCdma.getCdmaDbm());
      cellinfo.put("CELL_SIGNAL_STRENGTH_CDMA_ECIO", signalStrengthCdma.getCdmaEcio());
      cellinfo.put("CELL_SIGNAL_STRENGTH_EVDO_DBM", signalStrengthCdma.getEvdoDbm());
      cellinfo.put("CELL_SIGNAL_STRENGTH_EVDO_ECIO", signalStrengthCdma.getEvdoEcio());
      cellinfo.put("CELL_SIGNAL_STRENGTH_EVDO_SNR", signalStrengthCdma.getEvdoSnr());
    } else if (signalStrength instanceof CellSignalStrengthNr) {
      CellSignalStrengthNr signalStrengthNr = (CellSignalStrengthNr) signalStrength;
      cellinfo.put("CELL_SIGNAL_STRENGTH_DBM", signalStrengthNr.getDbm());
      cellinfo.put("CELL_SIGNAL_STRENGTH_LEVEL", signalStrengthNr.getLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_SS_RSRP", signalStrengthNr.getSsRsrp());
      cellinfo.put("CELL_SIGNAL_STRENGTH_SS_RSRQ", signalStrengthNr.getSsRsrq());
      cellinfo.put("CELL_SIGNAL_STRENGTH_SS_SINR", signalStrengthNr.getSsSinr());
      cellinfo.put("CELL_SIGNAL_STRENGTH_CSI_RSRP", signalStrengthNr.getCsiRsrp());
      cellinfo.put("CELL_SIGNAL_STRENGTH_CSI_RSRQ", signalStrengthNr.getCsiRsrq());
      cellinfo.put("CELL_SIGNAL_STRENGTH_CSI_SINR", signalStrengthNr.getCsiSinr());

    } else if (signalStrength instanceof CellSignalStrengthTdscdma) {
      CellSignalStrengthTdscdma signalStrengthTdscdma = (CellSignalStrengthTdscdma) signalStrength;
      cellinfo.put("CELL_SIGNAL_STRENGTH_DBM", signalStrengthTdscdma.getDbm());
      cellinfo.put("CELL_SIGNAL_STRENGTH_LEVEL", signalStrengthTdscdma.getLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_ASU_LEVEL", signalStrengthTdscdma.getAsuLevel());
      cellinfo.put("CELL_SIGNAL_STRENGTH_RSCP", signalStrengthTdscdma.getRscp());

    } else {
      cellinfo.put("CELL_SIGNAL_STRENGTH_ERROR", "Unknown CellSignalStrength type");
    }
  }
}
