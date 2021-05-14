package org.thoughtcrime.securesms.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.signal.core.util.logging.Log;
import org.thoughtcrime.securesms.BuildConfig;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.backup.BackupProtos;
import org.thoughtcrime.securesms.dependencies.ApplicationDependencies;
import org.thoughtcrime.securesms.keyvalue.SettingsValues;
import org.thoughtcrime.securesms.lock.RegistrationLockReminders;
import org.thoughtcrime.securesms.net.ProxyType;
import org.thoughtcrime.securesms.notifications.NotificationChannels;
import org.thoughtcrime.securesms.preferences.widgets.NotificationPrivacyPreference;
import org.thoughtcrime.securesms.preferences.widgets.PassphraseLockTriggerPreference;
import org.whispersystems.libsignal.util.Medium;
import org.whispersystems.signalservice.api.util.UuidUtil;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TextSecurePreferences {

  private static final String TAG = Log.tag(TextSecurePreferences.class);

  public  static final String IDENTITY_PREF                    = "pref_choose_identity";
  public  static final String CHANGE_PASSPHRASE_PREF           = "pref_change_passphrase";
  public  static final String THEME_PREF                       = "pref_theme";
  public  static final String LANGUAGE_PREF                    = "pref_language";

  private static final String LAST_VERSION_CODE_PREF           = "last_version_code";
  public  static final String RINGTONE_PREF                    = "pref_key_ringtone";
  public  static final String VIBRATE_PREF                     = "pref_key_vibrate";
  private static final String NOTIFICATION_PREF                = "pref_key_enable_notifications";
  public  static final String LED_COLOR_PREF                   = "pref_led_color";
  public  static final String LED_BLINK_PREF                   = "pref_led_blink";
  private static final String LED_BLINK_PREF_CUSTOM            = "pref_led_blink_custom";
  public  static final String SCREEN_SECURITY_PREF             = "pref_screen_security";
  private static final String ENTER_SENDS_PREF                 = "pref_enter_sends";
  private static final String ENTER_PRESENT_PREF               = "pref_enter_key";
  private static final String LOCAL_NUMBER_PREF                = "pref_local_number";
  private static final String LOCAL_UUID_PREF                  = "pref_local_uuid";
  private static final String LOCAL_USERNAME_PREF              = "pref_local_username";
  public  static final String REGISTERED_GCM_PREF              = "pref_gcm_registered";
  private static final String GCM_PASSWORD_PREF                = "pref_gcm_password";
  private static final String PROMPTED_PUSH_REGISTRATION_PREF  = "pref_prompted_push_registration";
  private static final String PROMPTED_OPTIMIZE_DOZE_PREF      = "pref_prompted_optimize_doze";
  public  static final String DIRECTORY_FRESH_TIME_PREF        = "pref_directory_refresh_time";
  public  static final String UPDATE_APK_ENABLED               = "pref_update_apk_enabled";
  private static final String UPDATE_APK_REFRESH_TIME_PREF     = "pref_update_apk_refresh_time";
  private static final String UPDATE_APK_DOWNLOAD_ID           = "pref_update_apk_download_id";
  private static final String UPDATE_APK_DIGEST                = "pref_update_apk_digest";
  private static final String SIGNED_PREKEY_ROTATION_TIME_PREF = "pref_signed_pre_key_rotation_time";

  private static final String IN_THREAD_NOTIFICATION_PREF      = "pref_key_inthread_notifications";
  public  static final String MESSAGE_BODY_TEXT_SIZE_PREF      = "pref_message_body_text_size";

  private static final String LOCAL_REGISTRATION_ID_PREF       = "pref_local_registration_id";
  private static final String SIGNED_PREKEY_REGISTERED_PREF    = "pref_signed_prekey_registered";

  private static final String GCM_DISABLED_PREF                = "pref_gcm_disabled";
  private static final String GCM_REGISTRATION_ID_PREF         = "pref_gcm_registration_id";
  private static final String GCM_REGISTRATION_ID_VERSION_PREF = "pref_gcm_registration_id_version";
  private static final String GCM_REGISTRATION_ID_TIME_PREF    = "pref_gcm_registration_id_last_set_time";
  private static final String WEBSOCKET_REGISTERED_PREF        = "pref_websocket_registered";
  private static final String SIGNED_PREKEY_FAILURE_COUNT_PREF = "pref_signed_prekey_failure_count";

  public  static final String REPEAT_ALERTS_PREF               = "pref_repeat_alerts";
  public  static final String NOTIFICATION_PRIVACY_PREF        = "pref_notification_privacy";
  public  static final String NOTIFICATION_PRIORITY_PREF       = "pref_notification_priority";
  public  static final String NEW_CONTACTS_NOTIFICATIONS       = "pref_enable_new_contacts_notifications";

  public  static final String MEDIA_DOWNLOAD_MOBILE_PREF       = "pref_media_download_mobile";
  public  static final String MEDIA_DOWNLOAD_WIFI_PREF         = "pref_media_download_wifi";
  public  static final String MEDIA_DOWNLOAD_ROAMING_PREF      = "pref_media_download_roaming";

  public  static final String CALL_BANDWIDTH_PREF              = "pref_data_call_bandwidth";

  public  static final String SYSTEM_EMOJI_PREF                = "pref_system_emoji";
  private static final String MULTI_DEVICE_PROVISIONED_PREF    = "pref_multi_device";
  public  static final String DIRECT_CAPTURE_CAMERA_ID         = "pref_direct_capture_camera_id";
  public  static final String ALWAYS_RELAY_CALLS_PREF          = "pref_turn_only";
  private static final String PROFILE_NAME_PREF                = "pref_profile_name";
  private static final String PROFILE_AVATAR_ID_PREF           = "pref_profile_avatar_id";
  public  static final String READ_RECEIPTS_PREF               = "pref_read_receipts";
  public  static final String INCOGNITO_KEYBORAD_PREF          = "pref_incognito_keyboard";
  private static final String UNAUTHORIZED_RECEIVED            = "pref_unauthorized_received";
  private static final String SUCCESSFUL_DIRECTORY_PREF        = "pref_successful_directory";

  private static final String DATABASE_ENCRYPTED_SECRET     = "pref_database_encrypted_secret";
  private static final String DATABASE_UNENCRYPTED_SECRET   = "pref_database_unencrypted_secret";
  private static final String ATTACHMENT_ENCRYPTED_SECRET   = "pref_attachment_encrypted_secret";
  private static final String ATTACHMENT_UNENCRYPTED_SECRET = "pref_attachment_unencrypted_secret";

  public static final String CALL_NOTIFICATIONS_PREF = "pref_call_notifications";
  public static final String CALL_RINGTONE_PREF      = "pref_call_ringtone";
  public static final String CALL_VIBRATE_PREF       = "pref_call_vibrate";

  private static final String NEXT_PRE_KEY_ID          = "pref_next_pre_key_id";
  private static final String ACTIVE_SIGNED_PRE_KEY_ID = "pref_active_signed_pre_key_id";
  private static final String NEXT_SIGNED_PRE_KEY_ID   = "pref_next_signed_pre_key_id";

  public  static final String BACKUP                      = "pref_backup";
  public  static final String BACKUP_ENABLED              = "pref_backup_enabled";
  private static final String BACKUP_PASSPHRASE           = "pref_backup_passphrase";
  private static final String ENCRYPTED_BACKUP_PASSPHRASE = "pref_encrypted_backup_passphrase";
  private static final String BACKUP_TIME                 = "pref_backup_next_time";

  public static final String TRANSFER = "pref_transfer";

  public  static final String PASSPHRASE_LOCK         = "pref_passphrase_lock";
  public  static final String PASSPHRASE_LOCK_TIMEOUT = "pref_passphrase_lock_timeout";
  public  static final String PASSPHRASE_LOCK_TRIGGER = "pref_passphrase_lock_trigger";

  public  static final String PROXY_TYPE       = "pref_proxy_type";
  public  static final String PROXY_SOCKS_HOST = "pref_proxy_socks_host";
  public  static final String PROXY_SOCKS_PORT = "pref_proxy_socks_port";

  @Deprecated
  public static final  String REGISTRATION_LOCK_PREF_V1                = "pref_registration_lock";
  @Deprecated
  private static final String REGISTRATION_LOCK_PIN_PREF_V1            = "pref_registration_lock_pin";

  public static final  String REGISTRATION_LOCK_PREF_V2                = "pref_registration_lock_v2";

  private static final String REGISTRATION_LOCK_LAST_REMINDER_TIME_POST_KBS = "pref_registration_lock_last_reminder_time_post_kbs";
  private static final String REGISTRATION_LOCK_NEXT_REMINDER_INTERVAL      = "pref_registration_lock_next_reminder_interval";

  public  static final String SIGNAL_PIN_CHANGE = "pref_kbs_change";

  private static final String SERVICE_OUTAGE         = "pref_service_outage";
  private static final String LAST_OUTAGE_CHECK_TIME = "pref_last_outage_check_time";

  private static final String LAST_FULL_CONTACT_SYNC_TIME = "pref_last_full_contact_sync_time";
  private static final String NEEDS_FULL_CONTACT_SYNC     = "pref_needs_full_contact_sync";

  public  static final String LOG_ENABLED            = "pref_log_enabled";
  private static final String LOG_ENCRYPTED_SECRET   = "pref_log_encrypted_secret";
  private static final String LOG_UNENCRYPTED_SECRET = "pref_log_unencrypted_secret";

  private static final String NOTIFICATION_CHANNEL_VERSION          = "pref_notification_channel_version";
  private static final String NOTIFICATION_MESSAGES_CHANNEL_VERSION = "pref_notification_messages_channel_version";

  private static final String NEEDS_MESSAGE_PULL = "pref_needs_message_pull";

  private static final String UNIDENTIFIED_ACCESS_CERTIFICATE_ROTATION_TIME_PREF = "pref_unidentified_access_certificate_rotation_time";
  public  static final String UNIVERSAL_UNIDENTIFIED_ACCESS                      = "pref_universal_unidentified_access";
  public  static final String SHOW_UNIDENTIFIED_DELIVERY_INDICATORS              = "pref_show_unidentifed_delivery_indicators";

  public static final String TYPING_INDICATORS = "pref_typing_indicators";

  private static final String BLOCK_UNKNOWN = "pref_block_unknown";

  public static final String LINK_PREVIEWS = "pref_link_previews";

  private static final String GIF_GRID_LAYOUT = "pref_gif_grid_layout";

  private static final String MEDIA_KEYBOARD_MODE = "pref_media_keyboard_mode";

  private static final String VIEW_ONCE_TOOLTIP_SEEN = "pref_revealable_message_tooltip_seen";

  private static final String SEEN_CAMERA_FIRST_TOOLTIP = "pref_seen_camera_first_tooltip";

  private static final String JOB_MANAGER_VERSION = "pref_job_manager_version";

  private static final String APP_MIGRATION_VERSION = "pref_app_migration_version";

  public  static final String FIRST_INSTALL_VERSION = "pref_first_install_version";

  private static final String HAS_SEEN_SWIPE_TO_REPLY = "pref_has_seen_swipe_to_reply";

  private static final String HAS_SEEN_VIDEO_RECORDING_TOOLTIP = "camerax.fragment.has.dismissed.video.recording.tooltip";

  private static final String STORAGE_MANIFEST_VERSION = "pref_storage_manifest_version";

  private static final String GOOGLE_MAP_TYPE = "pref_google_map_type";

  public static String getGoogleMapType(Context context) {
    return getStringPreference(context, GOOGLE_MAP_TYPE, "normal");
  }

  public static void setGoogleMapType(Context context, String value) {
    setStringPreference(context, GOOGLE_MAP_TYPE, value);
  }

  private static final String[] booleanPreferencesToBackup = {SCREEN_SECURITY_PREF,
                                                              INCOGNITO_KEYBORAD_PREF,
                                                              ALWAYS_RELAY_CALLS_PREF,
                                                              READ_RECEIPTS_PREF,
                                                              TYPING_INDICATORS,
                                                              SHOW_UNIDENTIFIED_DELIVERY_INDICATORS,
                                                              UNIVERSAL_UNIDENTIFIED_ACCESS,
                                                              NOTIFICATION_PREF,
                                                              VIBRATE_PREF,
                                                              IN_THREAD_NOTIFICATION_PREF,
                                                              CALL_NOTIFICATIONS_PREF,
                                                              CALL_VIBRATE_PREF,
                                                              NEW_CONTACTS_NOTIFICATIONS,
                                                              SYSTEM_EMOJI_PREF,
                                                              ENTER_SENDS_PREF};

  private static final String[] stringPreferencesToBackup = {LED_COLOR_PREF,
                                                             LED_BLINK_PREF,
                                                             REPEAT_ALERTS_PREF,
                                                             NOTIFICATION_PRIVACY_PREF,
                                                             THEME_PREF,
                                                             LANGUAGE_PREF,
                                                             MESSAGE_BODY_TEXT_SIZE_PREF};

  private static final String[] stringSetPreferencesToBackup = {MEDIA_DOWNLOAD_MOBILE_PREF,
                                                                MEDIA_DOWNLOAD_WIFI_PREF,
                                                                MEDIA_DOWNLOAD_ROAMING_PREF};

  private static final String[] booleanPreferencesToBackupMolly = {
      LOG_ENABLED,
      UPDATE_APK_ENABLED,
      BLOCK_UNKNOWN,
  };

  public static List<BackupProtos.SharedPreference> getPreferencesToSaveToBackup(@NonNull Context context) {
    SharedPreferences                   preferences  = SecurePreferenceManager.getSecurePreferences(context);
    List<BackupProtos.SharedPreference> backupProtos = new ArrayList<>();
    String                              defaultFile  = BuildConfig.SIGNAL_PACKAGE_NAME + "_preferences";

    for (String booleanPreference : booleanPreferencesToBackup) {
      if (preferences.contains(booleanPreference)) {
        backupProtos.add(BackupProtos.SharedPreference.newBuilder()
                                                      .setFile(defaultFile)
                                                      .setKey(booleanPreference)
                                                      .setBooleanValue(preferences.getBoolean(booleanPreference, false))
                                                      .build());
      }
    }

    for (String stringPreference : stringPreferencesToBackup) {
      if (preferences.contains(stringPreference)) {
        backupProtos.add(BackupProtos.SharedPreference.newBuilder()
                                                      .setFile(defaultFile)
                                                      .setKey(stringPreference)
                                                      .setValue(preferences.getString(stringPreference, null))
                                                      .build());
      }
    }

    for (String stringSetPreference : stringSetPreferencesToBackup) {
      if (preferences.contains(stringSetPreference)) {
        backupProtos.add(BackupProtos.SharedPreference.newBuilder()
                                                      .setFile(defaultFile)
                                                      .setKey(stringSetPreference)
                                                      .setIsStringSetValue(true)
                                                      .addAllStringSetValue(preferences.getStringSet(stringSetPreference, Collections.emptySet()))
                                                      .build());
      }
    }

    for (String booleanPreference : booleanPreferencesToBackupMolly) {
      if (preferences.contains(booleanPreference)) {
        backupProtos.add(BackupProtos.SharedPreference.newBuilder()
                                                      .setFile(SecurePreferenceManager.getSecurePreferencesName())
                                                      .setKey(booleanPreference)
                                                      .setBooleanValue(preferences.getBoolean(booleanPreference, false))
                                                      .build());
      }
    }

    return backupProtos;
  }

  public static void onPostBackupRestore(@NonNull Context context) {
    if (NotificationChannels.supported() && SecurePreferenceManager.getSecurePreferences(context).contains(VIBRATE_PREF)) {
      NotificationChannels.updateMessageVibrate(context, isNotificationVibrateEnabled(context));
    }
  }

  public static boolean isScreenLockEnabled(@NonNull Context context) {
    return isPassphraseLockEnabled(context);
  }

  public static boolean isPassphraseLockEnabled(@NonNull Context context) {
    return getBooleanPreference(context, PASSPHRASE_LOCK, true);
  }

  public static void setPassphraseLockEnabled(@NonNull Context context, boolean value) {
    setBooleanPreference(context, PASSPHRASE_LOCK, value);
  }

  public static PassphraseLockTriggerPreference getPassphraseLockTrigger(@NonNull Context context) {
    return new PassphraseLockTriggerPreference(getStringSetPreference(context,
            PASSPHRASE_LOCK_TRIGGER,
            new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.pref_passphrase_lock_trigger_default)))));
  }

  public static long getPassphraseLockTimeout(@NonNull Context context) {
    return getLongPreference(context, PASSPHRASE_LOCK_TIMEOUT, 0);
  }

  public static void setPassphraseLockTimeout(@NonNull Context context, long value) {
    setLongPreference(context, PASSPHRASE_LOCK_TIMEOUT, value);
  }

  public static ProxyType getProxyType(@NonNull Context context) {
    return ProxyType.fromCode(getStringPreference(context, PROXY_TYPE, null));
  }

  public static String getProxySocksHost(@NonNull Context context) {
    return getStringPreference(context, PROXY_SOCKS_HOST, "localhost");
  }

  public static int getProxySocksPort(@NonNull Context context) {
    return Integer.parseInt(getStringPreference(context, PROXY_SOCKS_PORT, "9050"));
  }

  public static boolean isV1RegistrationLockEnabled(@NonNull Context context) {
    //noinspection deprecation
    return getBooleanPreference(context, REGISTRATION_LOCK_PREF_V1, false);
  }

  /**
   * @deprecated Use only during re-reg where user had pinV1.
   */
  @Deprecated
  public static void setV1RegistrationLockEnabled(@NonNull Context context, boolean value) {
    //noinspection deprecation
    setBooleanPreference(context, REGISTRATION_LOCK_PREF_V1, value);
  }

  /**
   * @deprecated Use only for migrations to the Key Backup Store registration pinV2.
   */
  @Deprecated
  public static @Nullable String getDeprecatedV1RegistrationLockPin(@NonNull Context context) {
    //noinspection deprecation
    return getStringPreference(context, REGISTRATION_LOCK_PIN_PREF_V1, null);
  }

  public static void clearRegistrationLockV1(@NonNull Context context) {
    //noinspection deprecation
    SecurePreferenceManager.getSecurePreferences(context)
                     .edit()
                     .remove(REGISTRATION_LOCK_PIN_PREF_V1)
                     .apply();
  }

  /**
   * @deprecated Use only for migrations to the Key Backup Store registration pinV2.
   */
  @Deprecated
  public static void setV1RegistrationLockPin(@NonNull Context context, String pin) {
    //noinspection deprecation
    setStringPreference(context, REGISTRATION_LOCK_PIN_PREF_V1, pin);
  }

  public static long getRegistrationLockLastReminderTime(@NonNull Context context) {
    return getLongPreference(context, REGISTRATION_LOCK_LAST_REMINDER_TIME_POST_KBS, 0);
  }

  public static void setRegistrationLockLastReminderTime(@NonNull Context context, long time) {
    setLongPreference(context, REGISTRATION_LOCK_LAST_REMINDER_TIME_POST_KBS, time);
  }

  public static long getRegistrationLockNextReminderInterval(@NonNull Context context) {
    return getLongPreference(context, REGISTRATION_LOCK_NEXT_REMINDER_INTERVAL, RegistrationLockReminders.INITIAL_INTERVAL);
  }

  public static void setRegistrationLockNextReminderInterval(@NonNull Context context, long value) {
    setLongPreference(context, REGISTRATION_LOCK_NEXT_REMINDER_INTERVAL, value);
  }

  public static void setBackupPassphrase(@NonNull Context context, @Nullable String passphrase) {
    setStringPreference(context, BACKUP_PASSPHRASE, passphrase);
  }

  public static @Nullable String getBackupPassphrase(@NonNull Context context) {
    return getStringPreference(context, BACKUP_PASSPHRASE, null);
  }

  public static void setEncryptedBackupPassphrase(@NonNull Context context, @Nullable String encryptedPassphrase) {
    setStringPreference(context, ENCRYPTED_BACKUP_PASSPHRASE, encryptedPassphrase);
  }

  public static @Nullable String getEncryptedBackupPassphrase(@NonNull Context context) {
    return getStringPreference(context, ENCRYPTED_BACKUP_PASSPHRASE, null);
  }

  public static void setBackupEnabled(@NonNull Context context, boolean value) {
    setBooleanPreference(context, BACKUP_ENABLED, value);
  }

  public static boolean isBackupEnabled(@NonNull Context context) {
    return getBooleanPreference(context, BACKUP_ENABLED, false);
  }

  public static void setNextBackupTime(@NonNull Context context, long time) {
    setLongPreference(context, BACKUP_TIME, time);
  }

  public static long getNextBackupTime(@NonNull Context context) {
    return getLongPreference(context, BACKUP_TIME, -1);
  }

  public static int getNextPreKeyId(@NonNull Context context) {
    return getIntegerPreference(context, NEXT_PRE_KEY_ID, new SecureRandom().nextInt(Medium.MAX_VALUE));
  }

  public static void setNextPreKeyId(@NonNull Context context, int value) {
    setIntegerPrefrence(context, NEXT_PRE_KEY_ID, value);
  }

  public static int getNextSignedPreKeyId(@NonNull Context context) {
    return getIntegerPreference(context, NEXT_SIGNED_PRE_KEY_ID, new SecureRandom().nextInt(Medium.MAX_VALUE));
  }

  public static void setNextSignedPreKeyId(@NonNull Context context, int value) {
    setIntegerPrefrence(context, NEXT_SIGNED_PRE_KEY_ID, value);
  }

  public static int getActiveSignedPreKeyId(@NonNull Context context) {
    return getIntegerPreference(context, ACTIVE_SIGNED_PRE_KEY_ID, -1);
  }

  public static void setActiveSignedPreKeyId(@NonNull Context context, int value) {
    setIntegerPrefrence(context, ACTIVE_SIGNED_PRE_KEY_ID, value);;
  }

  public static void setAttachmentEncryptedSecret(@NonNull Context context, @NonNull String secret) {
    setStringPreference(context, ATTACHMENT_ENCRYPTED_SECRET, secret);
  }

  public static void setAttachmentUnencryptedSecret(@NonNull Context context, @Nullable String secret) {
    setStringPreference(context, ATTACHMENT_UNENCRYPTED_SECRET, secret);
  }

  public static @Nullable String getAttachmentEncryptedSecret(@NonNull Context context) {
    return getStringPreference(context, ATTACHMENT_ENCRYPTED_SECRET, null);
  }

  public static @Nullable String getAttachmentUnencryptedSecret(@NonNull Context context) {
    return getStringPreference(context, ATTACHMENT_UNENCRYPTED_SECRET, null);
  }

  public static void setDatabaseEncryptedSecret(@NonNull Context context, @NonNull String secret) {
    setStringPreference(context, DATABASE_ENCRYPTED_SECRET, secret);
  }

  public static void setDatabaseUnencryptedSecret(@NonNull Context context, @Nullable String secret) {
    setStringPreference(context, DATABASE_UNENCRYPTED_SECRET, secret);
  }

  public static @Nullable String getDatabaseUnencryptedSecret(@NonNull Context context) {
    return getStringPreference(context, DATABASE_UNENCRYPTED_SECRET, null);
  }

  public static @Nullable String getDatabaseEncryptedSecret(@NonNull Context context) {
    return getStringPreference(context, DATABASE_ENCRYPTED_SECRET, null);
  }

  public static void setHasSuccessfullyRetrievedDirectory(Context context, boolean value) {
    setBooleanPreference(context, SUCCESSFUL_DIRECTORY_PREF, value);
  }

  public static boolean hasSuccessfullyRetrievedDirectory(Context context) {
    return getBooleanPreference(context, SUCCESSFUL_DIRECTORY_PREF, false);
  }

  public static void setUnauthorizedReceived(Context context, boolean value) {
    setBooleanPreference(context, UNAUTHORIZED_RECEIVED, value);
  }

  public static boolean isUnauthorizedRecieved(Context context) {
    return getBooleanPreference(context, UNAUTHORIZED_RECEIVED, false);
  }

  public static boolean isIncognitoKeyboardEnabled(Context context) {
    return getBooleanPreference(context, INCOGNITO_KEYBORAD_PREF, true);
  }

  public static boolean isReadReceiptsEnabled(Context context) {
    return getBooleanPreference(context, READ_RECEIPTS_PREF, true);
  }

  public static void setReadReceiptsEnabled(Context context, boolean enabled) {
    setBooleanPreference(context, READ_RECEIPTS_PREF, enabled);
  }

  public static boolean isTypingIndicatorsEnabled(Context context) {
    return getBooleanPreference(context, TYPING_INDICATORS, true);
  }

  public static void setTypingIndicatorsEnabled(Context context, boolean enabled) {
    setBooleanPreference(context, TYPING_INDICATORS, enabled);
  }

  public static boolean isBlockUnknownEnabled(@NonNull Context context) {
    return getBooleanPreference(context, BLOCK_UNKNOWN, false);
  }

  public static void setBlockUnknownEnabled(@NonNull Context context, boolean value) {
    setBooleanPreference(context, BLOCK_UNKNOWN, value);
  }

  /**
   * Only kept so that we can avoid showing the megaphone for the new link previews setting
   * ({@link SettingsValues#isLinkPreviewsEnabled()}) when users upgrade. This can be removed after
   * we stop showing the link previews megaphone.
   */
  public static boolean wereLinkPreviewsEnabled(Context context) {
    return getBooleanPreference(context, LINK_PREVIEWS, true);
  }

  public static int getNotificationPriority(Context context) {
    return Integer.valueOf(getStringPreference(context, NOTIFICATION_PRIORITY_PREF, String.valueOf(NotificationCompat.PRIORITY_HIGH)));
  }

  public static int getMessageBodyTextSize(Context context) {
    return Integer.valueOf(getStringPreference(context, MESSAGE_BODY_TEXT_SIZE_PREF, "16"));
  }

  public static boolean isTurnOnly(Context context) {
    return getBooleanPreference(context, ALWAYS_RELAY_CALLS_PREF, false);
  }

  public static boolean isFcmDisabled(Context context) {
    return getBooleanPreference(context, GCM_DISABLED_PREF, false);
  }

  public static void setFcmDisabled(Context context, boolean disabled) {
    setBooleanPreference(context, GCM_DISABLED_PREF, disabled);
  }

  public static void setDirectCaptureCameraId(Context context, int value) {
    setIntegerPrefrence(context, DIRECT_CAPTURE_CAMERA_ID, value);
  }

  @SuppressWarnings("deprecation")
  public static int getDirectCaptureCameraId(Context context) {
    return getIntegerPreference(context, DIRECT_CAPTURE_CAMERA_ID, CameraInfo.CAMERA_FACING_FRONT);
  }

  public static void setMultiDevice(Context context, boolean value) {
    setBooleanPreference(context, MULTI_DEVICE_PROVISIONED_PREF, value);
  }

  public static boolean isMultiDevice(Context context) {
    return getBooleanPreference(context, MULTI_DEVICE_PROVISIONED_PREF, false);
  }

  public static void setSignedPreKeyFailureCount(Context context, int value) {
    setIntegerPrefrence(context, SIGNED_PREKEY_FAILURE_COUNT_PREF, value);
  }

  public static int getSignedPreKeyFailureCount(Context context) {
    return getIntegerPreference(context, SIGNED_PREKEY_FAILURE_COUNT_PREF, 0);
  }

  public static NotificationPrivacyPreference getNotificationPrivacy(Context context) {
    return new NotificationPrivacyPreference(getStringPreference(context, NOTIFICATION_PRIVACY_PREF, "none"));
  }

  public static void setNewContactsNotificationEnabled(Context context, boolean isEnabled) {
    setBooleanPreference(context, NEW_CONTACTS_NOTIFICATIONS, isEnabled);
  }

  public static boolean isNewContactsNotificationEnabled(Context context) {
    return getBooleanPreference(context, NEW_CONTACTS_NOTIFICATIONS, true);
  }

  public static boolean isWebsocketRegistered(Context context) {
    return getBooleanPreference(context, WEBSOCKET_REGISTERED_PREF, false);
  }

  public static void setWebsocketRegistered(Context context, boolean registered) {
    setBooleanPreference(context, WEBSOCKET_REGISTERED_PREF, registered);
  }

  public static int getRepeatAlertsCount(Context context) {
    try {
      return Integer.parseInt(getStringPreference(context, REPEAT_ALERTS_PREF, "0"));
    } catch (NumberFormatException e) {
      Log.w(TAG, e);
      return 0;
    }
  }

  public static void setRepeatAlertsCount(Context context, int count) {
    setStringPreference(context, REPEAT_ALERTS_PREF, String.valueOf(count));
  }

  public static boolean isSignedPreKeyRegistered(Context context) {
    return getBooleanPreference(context, SIGNED_PREKEY_REGISTERED_PREF, false);
  }

  public static void setSignedPreKeyRegistered(Context context, boolean value) {
    setBooleanPreference(context, SIGNED_PREKEY_REGISTERED_PREF, value);
  }

  public static void setFcmToken(Context context, String registrationId) {
    setStringPreference(context, GCM_REGISTRATION_ID_PREF, registrationId);
    setIntegerPrefrence(context, GCM_REGISTRATION_ID_VERSION_PREF, Util.getCanonicalVersionCode());
  }

  public static String getFcmToken(Context context) {
    int storedRegistrationIdVersion = getIntegerPreference(context, GCM_REGISTRATION_ID_VERSION_PREF, 0);

    if (storedRegistrationIdVersion != Util.getCanonicalVersionCode()) {
      return null;
    } else {
      return getStringPreference(context, GCM_REGISTRATION_ID_PREF, null);
    }
  }

  public static long getFcmTokenLastSetTime(Context context) {
    return getLongPreference(context, GCM_REGISTRATION_ID_TIME_PREF, 0);
  }

  public static void setFcmTokenLastSetTime(Context context, long timestamp) {
    setLongPreference(context, GCM_REGISTRATION_ID_TIME_PREF, timestamp);
  }

  public static boolean isSmsEnabled(Context context) {
    return Util.isDefaultSmsProvider(context);
  }

  public static int getLocalRegistrationId(Context context) {
    return getIntegerPreference(context, LOCAL_REGISTRATION_ID_PREF, 0);
  }

  public static void setLocalRegistrationId(Context context, int registrationId) {
    setIntegerPrefrence(context, LOCAL_REGISTRATION_ID_PREF, registrationId);
  }

  public static boolean isInThreadNotifications(Context context) {
    return getBooleanPreference(context, IN_THREAD_NOTIFICATION_PREF, true);
  }

  public static long getUnidentifiedAccessCertificateRotationTime(Context context) {
    return getLongPreference(context, UNIDENTIFIED_ACCESS_CERTIFICATE_ROTATION_TIME_PREF, 0L);
  }

  public static void setUnidentifiedAccessCertificateRotationTime(Context context, long value) {
    setLongPreference(context, UNIDENTIFIED_ACCESS_CERTIFICATE_ROTATION_TIME_PREF, value);
  }

  public static boolean isUniversalUnidentifiedAccess(Context context) {
    return getBooleanPreference(context, UNIVERSAL_UNIDENTIFIED_ACCESS, false);
  }

  public static void setShowUnidentifiedDeliveryIndicatorsEnabled(Context context, boolean enabled) {
    setBooleanPreference(context, SHOW_UNIDENTIFIED_DELIVERY_INDICATORS, enabled);
  }

  public static boolean isShowUnidentifiedDeliveryIndicatorsEnabled(Context context) {
    return getBooleanPreference(context, SHOW_UNIDENTIFIED_DELIVERY_INDICATORS, false);
  }

  public static long getSignedPreKeyRotationTime(Context context) {
    return getLongPreference(context, SIGNED_PREKEY_ROTATION_TIME_PREF, 0L);
  }

  public static void setSignedPreKeyRotationTime(Context context, long value) {
    setLongPreference(context, SIGNED_PREKEY_ROTATION_TIME_PREF, value);
  }

  public static long getDirectoryRefreshTime(Context context) {
    return getLongPreference(context, DIRECTORY_FRESH_TIME_PREF, 0L);
  }

  public static void setDirectoryRefreshTime(Context context, long value) {
    setLongPreference(context, DIRECTORY_FRESH_TIME_PREF, value);
  }

  public static void removeDirectoryRefreshTime(Context context) {
    removePreference(context, DIRECTORY_FRESH_TIME_PREF);
  }

  public static long getUpdateApkRefreshTime(Context context) {
    return getLongPreference(context, UPDATE_APK_REFRESH_TIME_PREF, 0L);
  }

  public static void setUpdateApkRefreshTime(Context context, long value) {
    setLongPreference(context, UPDATE_APK_REFRESH_TIME_PREF, value);
  }

  public static void setUpdateApkDownloadId(Context context, long value) {
    setLongPreference(context, UPDATE_APK_DOWNLOAD_ID, value);
  }

  public static long getUpdateApkDownloadId(Context context) {
    return getLongPreference(context, UPDATE_APK_DOWNLOAD_ID, -1);
  }

  public static void setUpdateApkDigest(Context context, String value) {
    setStringPreference(context, UPDATE_APK_DIGEST, value);
  }

  public static String getUpdateApkDigest(Context context) {
    return getStringPreference(context, UPDATE_APK_DIGEST, null);
  }

  public static boolean isUpdateApkEnabled(@NonNull Context context) {
    return getBooleanPreference(context, UPDATE_APK_ENABLED, false);
  }

  public static void setUpdateApkEnabled(@NonNull Context context, boolean value) {
    setBooleanPreference(context, UPDATE_APK_ENABLED, value);
  }

  public static String getLocalNumber(Context context) {
    return getStringPreference(context, LOCAL_NUMBER_PREF, null);
  }

  public static void setLocalNumber(Context context, String localNumber) {
    setStringPreference(context, LOCAL_NUMBER_PREF, localNumber);
  }

  public static UUID getLocalUuid(Context context) {
    return UuidUtil.parseOrNull(getStringPreference(context, LOCAL_UUID_PREF, null));
  }

  public static void setLocalUuid(Context context, UUID uuid) {
    setStringPreference(context, LOCAL_UUID_PREF, uuid.toString());
  }

  public static String getPushServerPassword(Context context) {
    return getStringPreference(context, GCM_PASSWORD_PREF, null);
  }

  public static void setPushServerPassword(Context context, String password) {
    setStringPreference(context, GCM_PASSWORD_PREF, password);
  }

  public static boolean isEnterImeKeyEnabled(Context context) {
    return getBooleanPreference(context, ENTER_PRESENT_PREF, false);
  }

  public static boolean isEnterSendsEnabled(Context context) {
    return getBooleanPreference(context, ENTER_SENDS_PREF, false);
  }

  public static String getIdentityContactUri(Context context) {
    return getStringPreference(context, IDENTITY_PREF, null);
  }

  public static void setIdentityContactUri(Context context, String identityUri) {
    setStringPreference(context, IDENTITY_PREF, identityUri);
  }

  public static void setScreenSecurityEnabled(Context context, boolean value) {
    setBooleanPreference(context, SCREEN_SECURITY_PREF, value);
  }

  public static boolean isScreenSecurityEnabled(Context context) {
    return getBooleanPreference(context, SCREEN_SECURITY_PREF, true);
  }

  public static int getLastVersionCode(Context context) {
    return getIntegerPreference(context, LAST_VERSION_CODE_PREF, Util.getCanonicalVersionCode());
  }

  public static void setLastVersionCode(Context context, int versionCode) throws IOException {
    if (!setIntegerPrefrenceBlocking(context, LAST_VERSION_CODE_PREF, versionCode)) {
      throw new IOException("couldn't write version code to sharedpreferences");
    }
  }

  public static String getTheme(Context context) {
    return getStringPreference(context, THEME_PREF, DynamicTheme.systemThemeAvailable() ? DynamicTheme.SYSTEM : DynamicTheme.LIGHT);
  }

  public static boolean isPushRegistered(Context context) {
    return getBooleanPreference(context, REGISTERED_GCM_PREF, false);
  }

  public static void setPushRegistered(Context context, boolean registered) {
    Log.i(TAG, "Setting push registered: " + registered);
    setBooleanPreference(context, REGISTERED_GCM_PREF, registered);
    ApplicationDependencies.getIncomingMessageObserver().notifyRegistrationChanged();
  }

  public static String getLanguage(Context context) {
    return getStringPreference(context, LANGUAGE_PREF, "zz");
  }

  public static void setLanguage(Context context, String language) {
    setStringPreference(context, LANGUAGE_PREF, language);
  }

  public static boolean hasPromptedPushRegistration(Context context) {
    return getBooleanPreference(context, PROMPTED_PUSH_REGISTRATION_PREF, false);
  }

  public static void setPromptedPushRegistration(Context context, boolean value) {
    setBooleanPreference(context, PROMPTED_PUSH_REGISTRATION_PREF, value);
  }

  public static void setPromptedOptimizeDoze(Context context, boolean value) {
    setBooleanPreference(context, PROMPTED_OPTIMIZE_DOZE_PREF, value);
  }

  public static boolean hasPromptedOptimizeDoze(Context context) {
    return getBooleanPreference(context, PROMPTED_OPTIMIZE_DOZE_PREF, false);
  }

  public static boolean isNotificationsEnabled(Context context) {
    return getBooleanPreference(context, NOTIFICATION_PREF, true);
  }

  public static void setCallNotificationsEnabled(Context context, boolean enabled) {
    setBooleanPreference(context, CALL_NOTIFICATIONS_PREF, enabled);
  }

  public static boolean isCallNotificationsEnabled(Context context) {
    return getBooleanPreference(context, CALL_NOTIFICATIONS_PREF, true);
  }

  public static @NonNull Uri getNotificationRingtone(Context context) {
    String result = getStringPreference(context, RINGTONE_PREF, Settings.System.DEFAULT_NOTIFICATION_URI.toString());

    if (result != null && result.startsWith("file:")) {
      result = Settings.System.DEFAULT_NOTIFICATION_URI.toString();
    }

    return Uri.parse(result);
  }

  public static @NonNull Uri getCallNotificationRingtone(Context context) {
    String result = getStringPreference(context, CALL_RINGTONE_PREF, Settings.System.DEFAULT_RINGTONE_URI.toString());

    if (result != null && result.startsWith("file:")) {
      result = Settings.System.DEFAULT_RINGTONE_URI.toString();
    }

    return Uri.parse(result);
  }

  public static void removeNotificationRingtone(Context context) {
    removePreference(context, RINGTONE_PREF);
  }

  public static void removeCallNotificationRingtone(Context context) {
    removePreference(context, CALL_RINGTONE_PREF);
  }

  public static void setNotificationRingtone(Context context, String ringtone) {
    setStringPreference(context, RINGTONE_PREF, ringtone);
  }

  public static void setCallNotificationRingtone(Context context, String ringtone) {
    setStringPreference(context, CALL_RINGTONE_PREF, ringtone);
  }

  public static void setNotificationVibrateEnabled(Context context, boolean enabled) {
    setBooleanPreference(context, VIBRATE_PREF, enabled);
  }

  public static boolean isNotificationVibrateEnabled(Context context) {
    return getBooleanPreference(context, VIBRATE_PREF, true);
  }

  public static boolean isCallNotificationVibrateEnabled(Context context) {
    boolean defaultValue = true;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      defaultValue = (Settings.System.getInt(context.getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING, 1) == 1);
    }

    return getBooleanPreference(context, CALL_VIBRATE_PREF, defaultValue);
  }

  public static String getNotificationLedColor(Context context) {
    return getStringPreference(context, LED_COLOR_PREF, "blue");
  }

  public static String getNotificationLedPattern(Context context) {
    return getStringPreference(context, LED_BLINK_PREF, "500,2000");
  }

  public static String getNotificationLedPatternCustom(Context context) {
    return getStringPreference(context, LED_BLINK_PREF_CUSTOM, "500,2000");
  }

  public static void setNotificationLedPatternCustom(Context context, String pattern) {
    setStringPreference(context, LED_BLINK_PREF_CUSTOM, pattern);
  }

  public static boolean isSystemEmojiPreferred(Context context) {
    return getBooleanPreference(context, SYSTEM_EMOJI_PREF, false);
  }

  public static @NonNull Set<String> getMobileMediaDownloadAllowed(Context context) {
    return getMediaDownloadAllowed(context, MEDIA_DOWNLOAD_MOBILE_PREF, R.array.pref_media_download_mobile_data_default);
  }

  public static @NonNull Set<String> getWifiMediaDownloadAllowed(Context context) {
    return getMediaDownloadAllowed(context, MEDIA_DOWNLOAD_WIFI_PREF, R.array.pref_media_download_wifi_default);
  }

  public static @NonNull Set<String> getRoamingMediaDownloadAllowed(Context context) {
    return getMediaDownloadAllowed(context, MEDIA_DOWNLOAD_ROAMING_PREF, R.array.pref_media_download_roaming_default);
  }

  private static @NonNull Set<String> getMediaDownloadAllowed(Context context, String key, @ArrayRes int defaultValuesRes) {
    return getStringSetPreference(context,
                                  key,
                                  new HashSet<>(Arrays.asList(context.getResources().getStringArray(defaultValuesRes))));
  }

  public static void setLastOutageCheckTime(Context context, long timestamp) {
    setLongPreference(context, LAST_OUTAGE_CHECK_TIME, timestamp);
  }

  public static long getLastOutageCheckTime(Context context) {
    return getLongPreference(context, LAST_OUTAGE_CHECK_TIME, 0);
  }

  public static void setServiceOutage(Context context, boolean isOutage) {
    setBooleanPreference(context, SERVICE_OUTAGE, isOutage);
  }

  public static boolean getServiceOutage(Context context) {
    return getBooleanPreference(context, SERVICE_OUTAGE, false);
  }

  public static long getLastFullContactSyncTime(Context context) {
    return getLongPreference(context, LAST_FULL_CONTACT_SYNC_TIME, 0);
  }

  public static void setLastFullContactSyncTime(Context context, long timestamp) {
    setLongPreference(context, LAST_FULL_CONTACT_SYNC_TIME, timestamp);
  }

  public static boolean needsFullContactSync(Context context) {
    return getBooleanPreference(context, NEEDS_FULL_CONTACT_SYNC, false);
  }

  public static void setNeedsFullContactSync(Context context, boolean needsSync) {
    setBooleanPreference(context, NEEDS_FULL_CONTACT_SYNC, needsSync);
  }

  public static void setLogEnabled(Context context, boolean enabled) {
    setBooleanPreference(context, LOG_ENABLED, enabled);
  }

  public static boolean isLogEnabled(Context context) {
    return getBooleanPreference(context, LOG_ENABLED, true);
  }

  public static void setLogEncryptedSecret(Context context, String base64Secret) {
    setStringPreference(context, LOG_ENCRYPTED_SECRET, base64Secret);
  }

  public static String getLogEncryptedSecret(Context context) {
    return getStringPreference(context, LOG_ENCRYPTED_SECRET, null);
  }

  public static void setLogUnencryptedSecret(Context context, String base64Secret) {
    setStringPreference(context, LOG_UNENCRYPTED_SECRET, base64Secret);
  }

  public static String getLogUnencryptedSecret(Context context) {
    return getStringPreference(context, LOG_UNENCRYPTED_SECRET, null);
  }

  public static int getNotificationChannelVersion(Context context) {
    return getIntegerPreference(context, NOTIFICATION_CHANNEL_VERSION, 1);
  }

  public static void setNotificationChannelVersion(Context context, int version) {
    setIntegerPrefrence(context, NOTIFICATION_CHANNEL_VERSION, version);
  }

  public static int getNotificationMessagesChannelVersion(Context context) {
    return getIntegerPreference(context, NOTIFICATION_MESSAGES_CHANNEL_VERSION, 1);
  }

  public static void setNotificationMessagesChannelVersion(Context context, int version) {
    setIntegerPrefrence(context, NOTIFICATION_MESSAGES_CHANNEL_VERSION, version);
  }

  public static boolean getNeedsMessagePull(Context context) {
    return getBooleanPreference(context, NEEDS_MESSAGE_PULL, false);
  }

  public static void setNeedsMessagePull(Context context, boolean needsMessagePull) {
    setBooleanPreference(context, NEEDS_MESSAGE_PULL, needsMessagePull);
  }

  public static boolean hasSeenStickerIntroTooltip(Context context) {
    return true;
  }

  public static void setHasSeenStickerIntroTooltip(Context context, boolean seenStickerTooltip) {}

  public static void setMediaKeyboardMode(Context context, MediaKeyboardMode mode) {
    setStringPreference(context, MEDIA_KEYBOARD_MODE, mode.name());
  }

  public static MediaKeyboardMode getMediaKeyboardMode(Context context) {
    String name = getStringPreference(context, MEDIA_KEYBOARD_MODE, MediaKeyboardMode.EMOJI.name());
    return MediaKeyboardMode.valueOf(name);
  }

  public static void setHasSeenViewOnceTooltip(Context context, boolean value) {
    setBooleanPreference(context, VIEW_ONCE_TOOLTIP_SEEN, value);
  }

  public static boolean hasSeenViewOnceTooltip(Context context) {
    return getBooleanPreference(context, VIEW_ONCE_TOOLTIP_SEEN, false);
  }

  public static void setHasSeenCameraFirstTooltip(Context context, boolean value) {
    setBooleanPreference(context, SEEN_CAMERA_FIRST_TOOLTIP, value);
  }

  public static boolean hasSeenCameraFirstTooltip(Context context) {
    return getBooleanPreference(context, SEEN_CAMERA_FIRST_TOOLTIP, false);
  }

  public static void setJobManagerVersion(Context context, int version) {
    setIntegerPrefrence(context, JOB_MANAGER_VERSION, version);
  }

  public static int getJobManagerVersion(Context contex) {
    return getIntegerPreference(contex, JOB_MANAGER_VERSION, 1);
  }

  public static void setAppMigrationVersion(Context context, int version) {
    setIntegerPrefrence(context, APP_MIGRATION_VERSION, version);
  }

  public static int getAppMigrationVersion(Context context) {
    return getIntegerPreference(context, APP_MIGRATION_VERSION, 1);
  }

  public static void setFirstInstallVersion(Context context, int version) {
    setIntegerPrefrence(context, FIRST_INSTALL_VERSION, version);
  }

  public static int getFirstInstallVersion(Context context) {
    return getIntegerPreference(context, FIRST_INSTALL_VERSION, -1);
  }

  public static boolean hasSeenSwipeToReplyTooltip(Context context) {
    return getBooleanPreference(context, HAS_SEEN_SWIPE_TO_REPLY, false);
  }

  public static void setHasSeenSwipeToReplyTooltip(Context context, boolean value) {
    setBooleanPreference(context, HAS_SEEN_SWIPE_TO_REPLY, value);
  }

  public static boolean hasSeenVideoRecordingTooltip(Context context) {
    return getBooleanPreference(context, HAS_SEEN_VIDEO_RECORDING_TOOLTIP, false);
  }

  public static void setHasSeenVideoRecordingTooltip(Context context, boolean value) {
    setBooleanPreference(context, HAS_SEEN_VIDEO_RECORDING_TOOLTIP, value);
  }

  public static void setStorageManifestVersion(Context context, long version) {
    setLongPreference(context, STORAGE_MANIFEST_VERSION, version);
  }

  public static void setBooleanPreference(Context context, String key, boolean value) {
    SecurePreferenceManager.getSecurePreferences(context).edit().putBoolean(key, value).apply();
  }

  public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
    return SecurePreferenceManager.getSecurePreferences(context).getBoolean(key, defaultValue);
  }

  public static void setStringPreference(Context context, String key, String value) {
    SecurePreferenceManager.getSecurePreferences(context).edit().putString(key, value).apply();
  }

  public static String getStringPreference(Context context, String key, String defaultValue) {
    return SecurePreferenceManager.getSecurePreferences(context).getString(key, defaultValue);
  }

  private static int getIntegerPreference(Context context, String key, int defaultValue) {
    return SecurePreferenceManager.getSecurePreferences(context).getInt(key, defaultValue);
  }

  private static void setIntegerPrefrence(Context context, String key, int value) {
    SecurePreferenceManager.getSecurePreferences(context).edit().putInt(key, value).apply();
  }

  private static boolean setIntegerPrefrenceBlocking(Context context, String key, int value) {
    return SecurePreferenceManager.getSecurePreferences(context).edit().putInt(key, value).commit();
  }

  private static long getLongPreference(Context context, String key, long defaultValue) {
    return SecurePreferenceManager.getSecurePreferences(context).getLong(key, defaultValue);
  }

  private static void setLongPreference(Context context, String key, long value) {
    SecurePreferenceManager.getSecurePreferences(context).edit().putLong(key, value).apply();
  }

  private static void removePreference(Context context, String key) {
    SecurePreferenceManager.getSecurePreferences(context).edit().remove(key).apply();
  }

  private static Set<String> getStringSetPreference(Context context, String key, Set<String> defaultValues) {
    final SharedPreferences prefs = SecurePreferenceManager.getSecurePreferences(context);
    if (prefs.contains(key)) {
      return prefs.getStringSet(key, Collections.<String>emptySet());
    } else {
      return defaultValues;
    }
  }

  // NEVER rename these -- they're persisted by name
  public enum MediaKeyboardMode {
    EMOJI, STICKER
  }
}
