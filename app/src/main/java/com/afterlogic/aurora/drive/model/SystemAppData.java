package com.afterlogic.aurora.drive.model;

import com.afterlogic.aurora.drive.data.common.annotations.IgnoreField;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class SystemAppData {

    /**
     * Auth : true
     * User : {"IdUser":22,"MailsPerPage":20,"ContactsPerPage":20,"AutoCheckMailInterval":1,"DefaultEditor":1,"Layout":0,"LoginsCount":697,"CanLoginWithPassword":true,"DefaultTheme":"Default","DefaultLanguage":"English","DefaultLanguageShort":"en","DefaultDateFormat":"MM/DD/YYYY","DefaultTimeFormat":0,"DefaultTimeZone":"Europe/Moscow","AllowCompose":true,"AllowReply":true,"AllowForward":true,"AllowFetcher":true,"SaveMail":0,"ThreadsEnabled":true,"UseThreads":true,"SaveRepliedMessagesToCurrentFolder":true,"DesktopNotifications":false,"AllowChangeInputDirection":false,"EnableOpenPgp":false,"AllowAutosaveInDrafts":true,"AutosignOutgoingEmails":false,"EmailNotification":"test@afterlogic.com","OutlookSyncEnable":true,"MobileSyncEnable":true,"ShowPersonalContacts":true,"ShowGlobalContacts":true,"IsCollaborationSupported":true,"AllowFilesSharing":false,"IsFilesSupported":true,"IsHelpdeskSupported":true,"IsHelpdeskAgent":true,"AllowHelpdeskNotifications":false,"HelpdeskSignature":"","HelpdeskSignatureEnable":false,"LastLogin":0,"AllowVoice":true,"VoiceProvider":"twilio","SipRealm":"","SipWebsocketProxyUrl":"","SipOutboundProxyUrl":"","SipCallerID":"","TwilioNumber":"","TwilioEnable":true,"SipEnable":true,"SipImpi":"","SipPassword":"","FilesEnable":true,"AllowCalendar":true,"Calendar":{"ShowWeekEnds":false,"ShowWorkDay":true,"WorkDayStarts":9,"WorkDayEnds":18,"WeekStartsOn":1,"DefaultTab":3,"SyncLogin":"test@afterlogic.com","DavServerUrl":"https://p7-dav.afterlogic.com","DavPrincipalUrl":"https://p7-dav.afterlogic.com/principals/test@afterlogic.com","AllowReminders":true},"CalendarSharing":true,"CalendarAppointments":true,"IsDemo":false}
     * TenantHash :
     * IsMobile : -1
     * AllowMobile : true
     * IsMailsuite : false
     * HelpdeskSiteName :
     * HelpdeskIframeUrl :
     * HelpdeskRedirect : false
     * HelpdeskThreadId : 0
     * HelpdeskActivatedEmail :
     * HelpdeskForgotHash :
     * ClientDebug : false
     * MailExpandFolders : false
     * HtmlEditorDefaultFontName :
     * HtmlEditorDefaultFontSize :
     * AllowSaveAsPdf : true
     * LastErrorCode : 0
     * Token : 3e55fabeda74c940e37333c9eea06a40
     * ZipAttachments : true
     * AllowIdentities : true
     * SocialEmail :
     * SocialIsLoggedIn : false
     * Links : {"ImportingContacts":"http://www.afterlogic.com/wiki/Importing_contacts_(Aurora)","OutlookSyncPlugin32":"http://www.afterlogic.com/download/OutlookSyncAddIn.msi","OutlookSyncPlugin64":"http://www.afterlogic.com/download/OutlookSyncAddIn64.msi","OutlookSyncPluginReadMore":"http://www.afterlogic.com/wiki/Outlook_sync_(Aurora)"}
     * SocialGoogle : true
     * SocialGoogleId : 631514845250-lsi1lf1j7vqsb5rq18lh4t499glp7f8b.apps.googleusercontent.com
     * SocialGoogleScopes : auth filestorage
     * SocialDropbox : true
     * SocialDropboxId : fl0eoztbakx121p
     * SocialDropboxScopes : auth filestorage
     * SocialFacebook : true
     * SocialFacebookId : 1582615142003625
     * SocialFacebookScopes : auth
     * SocialTwitter : true
     * SocialTwitterId : 5dK6c7peT49vQXRi5JBR3HcRg
     * SocialTwitterScopes : auth
     * Plugins : {"ExternalServices":{"Connectors":[{"@Object":"Object/CTenantSocials","Id":"631514845250-lsi1lf1j7vqsb5rq18lh4t499glp7f8b.apps.googleusercontent.com","Name":"Google","LowerName":"google","Allow":true,"Scopes":["auth","filestorage"]},{"@Object":"Object/CTenantSocials","Id":"fl0eoztbakx121p","Name":"Dropbox","LowerName":"dropbox","Allow":true,"Scopes":["auth","filestorage"]},{"@Object":"Object/CTenantSocials","Id":"1582615142003625","Name":"Facebook","LowerName":"facebook","Allow":true,"Scopes":["auth"]},{"@Object":"Object/CTenantSocials","Id":"5dK6c7peT49vQXRi5JBR3HcRg","Name":"Twitter","LowerName":"twitter","Allow":true,"Scopes":["auth"]}]}}
     * AllowChangePassword : true
     * LoginStyleImage :
     * AppStyleImage : https://static.afterlogic.com/img/afterlogic-logo-color-simple.png
     * HelpdeskStyleImage :
     * HelpdeskThreadAction :
     * Default : 49
     * Accounts : [{"AccountID":49,"Email":"test@afterlogic.com","FriendlyName":"Alex Orlov","Signature":{"Signature":"<div data-crea=\"font-wrapper\" style=\"font-family: Tahoma; font-size: 16px; direction: ltr\">/ Alex<br><\/div>","Type":1,"Options":1},"IsPasswordSpecified":true,"AllowMail":true}]
     * App : {"AllowUsersChangeInterfaceSettings":true,"AllowUsersChangeEmailSettings":true,"AllowUsersAddNewAccounts":true,"AllowOpenPGP":true,"AllowWebMail":true,"DefaultTab":"mailbox","AllowIosProfile":true,"PasswordMinLength":0,"PasswordMustBeComplex":false,"AllowRegistration":false,"AllowPasswordReset":false,"RegistrationDomains":[],"RegistrationQuestions":[],"SiteName":"AfterLogic WebMail Pro","DefaultLanguage":"English","DefaultLanguageShort":"en","DefaultTheme":"Default","Languages":[{"name":"English","value":"English"},{"name":"فارسی","value":"Persian"},{"name":"Română","value":"Romanian"},{"name":"Português Brasileiro","value":"Portuguese-Brazil"},{"name":"Українська","value":"Ukrainian"},{"name":"eesti","value":"Estonian"},{"name":"Русский","value":"Russian"},{"name":"Lietuvių","value":"Lithuanian"},{"name":"tiếng Việt","value":"Vietnamese"},{"name":"Italiano","value":"Italian"},{"name":"Français","value":"French"},{"name":"العربية","value":"Arabic"},{"name":"Norsk","value":"Norwegian"},{"name":"中文(香港)","value":"Chinese-Traditional"},{"name":"Suomi","value":"Finnish"},{"name":"Nederlands","value":"Dutch"},{"name":"עברית","value":"Hebrew"},{"name":"ภาษาไทย","value":"Thai"},{"name":"Svenska","value":"Swedish"},{"name":"Slovenščina","value":"Slovenian"},{"name":"日本語","value":"Japanese"},{"name":"Magyar","value":"Hungarian"},{"name":"Español","value":"Spanish"},{"name":"Ελληνικά","value":"Greek"},{"name":"Български","value":"Bulgarian"},{"name":"Türkçe","value":"Turkish"},{"name":"Polski","value":"Polish"},{"name":"中文(简体)","value":"Chinese-Simplified"},{"name":"Deutsch","value":"German"},{"name":"Čeština","value":"Czech"},{"name":"Latviešu","value":"Latvian"},{"name":"Português","value":"Portuguese-Portuguese"},{"name":"한국어","value":"Korean"},{"name":"Srpski","value":"Serbian"},{"name":"Dansk","value":"Danish"}],"Themes":["Default","White","Blue","DeepForest","OpenWater","Autumn","BlueJeans","Ecloud","Funny"],"DateFormats":["MM/DD/YYYY","DD/MM/YYYY","DD Month YYYY"],"AttachmentSizeLimit":0,"ImageUploadSizeLimit":0,"FileSizeLimit":0,"AutoSave":true,"IdleSessionTimeout":0,"AllowInsertImage":true,"AllowBodySize":false,"MaxBodySize":600,"MaxSubjectSize":255,"JoinReplyPrefixes":true,"AllowAppRegisterMailto":true,"AllowPrefetch":true,"AllowLanguageOnLogin":true,"FlagsLangSelect":true,"LoginFormType":0,"LoginSignMeType":0,"LoginAtDomainValue":"","DemoWebMail":false,"DemoWebMailLogin":"","DemoWebMailPassword":"","GoogleAnalyticsAccount":"","CustomLoginUrl":"","CustomLogoutUrl":"","ShowQuotaBar":true,"ServerUseUrlRewrite":true,"ServerUrlRewriteBase":"https://p7.afterlogic.com/","IosDetectOnLogin":true,"AllowContactsSharing":true,"LoginDescription":""}
     */

    @SerializedName("Auth")
    private boolean mAuth;
    /**
     * IdUser : 22
     * MailsPerPage : 20
     * ContactsPerPage : 20
     * AutoCheckMailInterval : 1
     * DefaultEditor : 1
     * Layout : 0
     * LoginsCount : 697
     * CanLoginWithPassword : true
     * DefaultTheme : Default
     * DefaultLanguage : English
     * DefaultLanguageShort : en
     * DefaultDateFormat : MM/DD/YYYY
     * DefaultTimeFormat : 0
     * DefaultTimeZone : Europe/Moscow
     * AllowCompose : true
     * AllowReply : true
     * AllowForward : true
     * AllowFetcher : true
     * SaveMail : 0
     * ThreadsEnabled : true
     * UseThreads : true
     * SaveRepliedMessagesToCurrentFolder : true
     * DesktopNotifications : false
     * AllowChangeInputDirection : false
     * EnableOpenPgp : false
     * AllowAutosaveInDrafts : true
     * AutosignOutgoingEmails : false
     * EmailNotification : test@afterlogic.com
     * OutlookSyncEnable : true
     * MobileSyncEnable : true
     * ShowPersonalContacts : true
     * ShowGlobalContacts : true
     * IsCollaborationSupported : true
     * AllowFilesSharing : false
     * IsFilesSupported : true
     * IsHelpdeskSupported : true
     * IsHelpdeskAgent : true
     * AllowHelpdeskNotifications : false
     * HelpdeskSignature :
     * HelpdeskSignatureEnable : false
     * LastLogin : 0
     * AllowVoice : true
     * VoiceProvider : twilio
     * SipRealm :
     * SipWebsocketProxyUrl :
     * SipOutboundProxyUrl :
     * SipCallerID :
     * TwilioNumber :
     * TwilioEnable : true
     * SipEnable : true
     * SipImpi :
     * SipPassword :
     * FilesEnable : true
     * AllowCalendar : true
     * Calendar : {"ShowWeekEnds":false,"ShowWorkDay":true,"WorkDayStarts":9,"WorkDayEnds":18,"WeekStartsOn":1,"DefaultTab":3,"SyncLogin":"test@afterlogic.com","DavServerUrl":"https://p7-dav.afterlogic.com","DavPrincipalUrl":"https://p7-dav.afterlogic.com/principals/test@afterlogic.com","AllowReminders":true}
     * CalendarSharing : true
     * CalendarAppointments : true
     * IsDemo : false
     */

    @SerializedName("User")
    private User mUser;
    @SerializedName("TenantHash")
    private String mTenantHash;
    @SerializedName("IsMobile")
    private int mIsMobile;
    @SerializedName("AllowMobile")
    private boolean mAllowMobile;
    @SerializedName("IsMailsuite")
    private boolean mIsMailsuite;
    @SerializedName("HelpdeskSiteName")
    private String mHelpdeskSiteName;
    @SerializedName("HelpdeskIframeUrl")
    private String mHelpdeskIframeUrl;
    @SerializedName("HelpdeskRedirect")
    private boolean mHelpdeskRedirect;
    @SerializedName("HelpdeskThreadId")
    private int mHelpdeskThreadId;
    @SerializedName("HelpdeskActivatedEmail")
    private String mHelpdeskActivatedEmail;
    @SerializedName("HelpdeskForgotHash")
    private String mHelpdeskForgotHash;
    @SerializedName("ClientDebug")
    private boolean mClientDebug;
    @SerializedName("MailExpandFolders")
    private boolean mMailExpandFolders;
    @SerializedName("HtmlEditorDefaultFontName")
    private String mHtmlEditorDefaultFontName;
    @SerializedName("HtmlEditorDefaultFontSize")
    private String mHtmlEditorDefaultFontSize;
    @SerializedName("AllowSaveAsPdf")
    private boolean mAllowSaveAsPdf;
    @SerializedName("LastErrorCode")
    private int mLastErrorCode;
    @SerializedName("Token")
    private String mToken;
    @SerializedName("ZipAttachments")
    private boolean mZipAttachments;
    @SerializedName("AllowIdentities")
    private boolean mAllowIdentities;
    @SerializedName("SocialEmail")
    private String mSocialEmail;
    @SerializedName("SocialIsLoggedIn")
    private boolean mSocialIsLoggedIn;
    /**
     * ImportingContacts : http://www.afterlogic.com/wiki/Importing_contacts_(Aurora)
     * OutlookSyncPlugin32 : http://www.afterlogic.com/download/OutlookSyncAddIn.msi
     * OutlookSyncPlugin64 : http://www.afterlogic.com/download/OutlookSyncAddIn64.msi
     * OutlookSyncPluginReadMore : http://www.afterlogic.com/wiki/Outlook_sync_(Aurora)
     */

    @SerializedName("Links")
    private Links mLinks;
    @SerializedName("SocialGoogle")
    private boolean mSocialGoogle;
    @SerializedName("SocialGoogleId")
    private String mSocialGoogleId;
    @SerializedName("SocialGoogleScopes")
    private String mSocialGoogleScopes;
    @SerializedName("SocialDropbox")
    private boolean mSocialDropbox;
    @SerializedName("SocialDropboxId")
    private String mSocialDropboxId;
    @SerializedName("SocialDropboxScopes")
    private String mSocialDropboxScopes;
    @SerializedName("SocialFacebook")
    private boolean mSocialFacebook;
    @SerializedName("SocialFacebookId")
    private String mSocialFacebookId;
    @SerializedName("SocialFacebookScopes")
    private String mSocialFacebookScopes;
    @SerializedName("SocialTwitter")
    private boolean mSocialTwitter;
    @SerializedName("SocialTwitterId")
    private String mSocialTwitterId;
    @SerializedName("SocialTwitterScopes")
    private String mSocialTwitterScopes;
    /**
     * ExternalServices : {"Connectors":[{"@Object":"Object/CTenantSocials","Id":"631514845250-lsi1lf1j7vqsb5rq18lh4t499glp7f8b.apps.googleusercontent.com","Name":"Google","LowerName":"google","Allow":true,"Scopes":["auth","filestorage"]},{"@Object":"Object/CTenantSocials","Id":"fl0eoztbakx121p","Name":"Dropbox","LowerName":"dropbox","Allow":true,"Scopes":["auth","filestorage"]},{"@Object":"Object/CTenantSocials","Id":"1582615142003625","Name":"Facebook","LowerName":"facebook","Allow":true,"Scopes":["auth"]},{"@Object":"Object/CTenantSocials","Id":"5dK6c7peT49vQXRi5JBR3HcRg","Name":"Twitter","LowerName":"twitter","Allow":true,"Scopes":["auth"]}]}
     */

    // FIXME: Empty array ([] not object or null) received when its empty
    @SerializedName("Plugins")
    @IgnoreField
    private Plugins mPlugins;
    @SerializedName("AllowChangePassword")
    private boolean mAllowChangePassword;
    @SerializedName("LoginStyleImage")
    private String mLoginStyleImage;
    @SerializedName("AppStyleImage")
    private String mAppStyleImage;
    @SerializedName("HelpdeskStyleImage")
    private String mHelpdeskStyleImage;
    @SerializedName("HelpdeskThreadAction")
    private String mHelpdeskThreadAction;
    @SerializedName("Default")
    private int mDefault;
    /**
     * AllowUsersChangeInterfaceSettings : true
     * AllowUsersChangeEmailSettings : true
     * AllowUsersAddNewAccounts : true
     * AllowOpenPGP : true
     * AllowWebMail : true
     * DefaultTab : mailbox
     * AllowIosProfile : true
     * PasswordMinLength : 0
     * PasswordMustBeComplex : false
     * AllowRegistration : false
     * AllowPasswordReset : false
     * RegistrationDomains : []
     * RegistrationQuestions : []
     * SiteName : AfterLogic WebMail Pro
     * DefaultLanguage : English
     * DefaultLanguageShort : en
     * DefaultTheme : Default
     * Languages : [{"name":"English","value":"English"},{"name":"فارسی","value":"Persian"},{"name":"Română","value":"Romanian"},{"name":"Português Brasileiro","value":"Portuguese-Brazil"},{"name":"Українська","value":"Ukrainian"},{"name":"eesti","value":"Estonian"},{"name":"Русский","value":"Russian"},{"name":"Lietuvių","value":"Lithuanian"},{"name":"tiếng Việt","value":"Vietnamese"},{"name":"Italiano","value":"Italian"},{"name":"Français","value":"French"},{"name":"العربية","value":"Arabic"},{"name":"Norsk","value":"Norwegian"},{"name":"中文(香港)","value":"Chinese-Traditional"},{"name":"Suomi","value":"Finnish"},{"name":"Nederlands","value":"Dutch"},{"name":"עברית","value":"Hebrew"},{"name":"ภาษาไทย","value":"Thai"},{"name":"Svenska","value":"Swedish"},{"name":"Slovenščina","value":"Slovenian"},{"name":"日本語","value":"Japanese"},{"name":"Magyar","value":"Hungarian"},{"name":"Español","value":"Spanish"},{"name":"Ελληνικά","value":"Greek"},{"name":"Български","value":"Bulgarian"},{"name":"Türkçe","value":"Turkish"},{"name":"Polski","value":"Polish"},{"name":"中文(简体)","value":"Chinese-Simplified"},{"name":"Deutsch","value":"German"},{"name":"Čeština","value":"Czech"},{"name":"Latviešu","value":"Latvian"},{"name":"Português","value":"Portuguese-Portuguese"},{"name":"한국어","value":"Korean"},{"name":"Srpski","value":"Serbian"},{"name":"Dansk","value":"Danish"}]
     * Themes : ["Default","White","Blue","DeepForest","OpenWater","Autumn","BlueJeans","Ecloud","Funny"]
     * DateFormats : ["MM/DD/YYYY","DD/MM/YYYY","DD Month YYYY"]
     * AttachmentSizeLimit : 0
     * ImageUploadSizeLimit : 0
     * FileSizeLimit : 0
     * AutoSave : true
     * IdleSessionTimeout : 0
     * AllowInsertImage : true
     * AllowBodySize : false
     * MaxBodySize : 600
     * MaxSubjectSize : 255
     * JoinReplyPrefixes : true
     * AllowAppRegisterMailto : true
     * AllowPrefetch : true
     * AllowLanguageOnLogin : true
     * FlagsLangSelect : true
     * LoginFormType : 0
     * LoginSignMeType : 0
     * LoginAtDomainValue :
     * DemoWebMail : false
     * DemoWebMailLogin :
     * DemoWebMailPassword :
     * GoogleAnalyticsAccount :
     * CustomLoginUrl :
     * CustomLogoutUrl :
     * ShowQuotaBar : true
     * ServerUseUrlRewrite : true
     * ServerUrlRewriteBase : https://p7.afterlogic.com/
     * IosDetectOnLogin : true
     * AllowContactsSharing : true
     * LoginDescription :
     */

    @SerializedName("App")
    private App mApp;
    /**
     * AccountID : 49
     * Email : test@afterlogic.com
     * FriendlyName : Alex Orlov
     * Signature : {"Signature":"<div data-crea=\"font-wrapper\" style=\"font-family: Tahoma; font-size: 16px; direction: ltr\">/ Alex<br><\/div>","Type":1,"Options":1}
     * IsPasswordSpecified : true
     * AllowMail : true
     */

    @SerializedName("Accounts")
    private List<Accounts> mAccounts;

    public SystemAppData() {
    }

    public SystemAppData(String token) {
        mToken = token;
    }

    public boolean isAuthorized() {
        return mAuth;
    }

    public void setAuth(boolean auth) {
        mAuth = auth;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getTenantHash() {
        return mTenantHash;
    }

    public void setTenantHash(String tenantHash) {
        mTenantHash = tenantHash;
    }

    public int getIsMobile() {
        return mIsMobile;
    }

    public void setIsMobile(int isMobile) {
        mIsMobile = isMobile;
    }

    public boolean isAllowMobile() {
        return mAllowMobile;
    }

    public void setAllowMobile(boolean allowMobile) {
        mAllowMobile = allowMobile;
    }

    public boolean isIsMailsuite() {
        return mIsMailsuite;
    }

    public void setIsMailsuite(boolean isMailsuite) {
        mIsMailsuite = isMailsuite;
    }

    public String getHelpdeskSiteName() {
        return mHelpdeskSiteName;
    }

    public void setHelpdeskSiteName(String helpdeskSiteName) {
        mHelpdeskSiteName = helpdeskSiteName;
    }

    public String getHelpdeskIframeUrl() {
        return mHelpdeskIframeUrl;
    }

    public void setHelpdeskIframeUrl(String helpdeskIframeUrl) {
        mHelpdeskIframeUrl = helpdeskIframeUrl;
    }

    public boolean isHelpdeskRedirect() {
        return mHelpdeskRedirect;
    }

    public void setHelpdeskRedirect(boolean helpdeskRedirect) {
        mHelpdeskRedirect = helpdeskRedirect;
    }

    public int getHelpdeskThreadId() {
        return mHelpdeskThreadId;
    }

    public void setHelpdeskThreadId(int helpdeskThreadId) {
        mHelpdeskThreadId = helpdeskThreadId;
    }

    public String getHelpdeskActivatedEmail() {
        return mHelpdeskActivatedEmail;
    }

    public void setHelpdeskActivatedEmail(String helpdeskActivatedEmail) {
        mHelpdeskActivatedEmail = helpdeskActivatedEmail;
    }

    public String getHelpdeskForgotHash() {
        return mHelpdeskForgotHash;
    }

    public void setHelpdeskForgotHash(String helpdeskForgotHash) {
        mHelpdeskForgotHash = helpdeskForgotHash;
    }

    public boolean isClientDebug() {
        return mClientDebug;
    }

    public void setClientDebug(boolean clientDebug) {
        mClientDebug = clientDebug;
    }

    public boolean isMailExpandFolders() {
        return mMailExpandFolders;
    }

    public void setMailExpandFolders(boolean mailExpandFolders) {
        mMailExpandFolders = mailExpandFolders;
    }

    public String getHtmlEditorDefaultFontName() {
        return mHtmlEditorDefaultFontName;
    }

    public void setHtmlEditorDefaultFontName(String htmlEditorDefaultFontName) {
        mHtmlEditorDefaultFontName = htmlEditorDefaultFontName;
    }

    public String getHtmlEditorDefaultFontSize() {
        return mHtmlEditorDefaultFontSize;
    }

    public void setHtmlEditorDefaultFontSize(String htmlEditorDefaultFontSize) {
        mHtmlEditorDefaultFontSize = htmlEditorDefaultFontSize;
    }

    public boolean isAllowSaveAsPdf() {
        return mAllowSaveAsPdf;
    }

    public void setAllowSaveAsPdf(boolean allowSaveAsPdf) {
        mAllowSaveAsPdf = allowSaveAsPdf;
    }

    public int getLastErrorCode() {
        return mLastErrorCode;
    }

    public void setLastErrorCode(int lastErrorCode) {
        mLastErrorCode = lastErrorCode;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public boolean isZipAttachments() {
        return mZipAttachments;
    }

    public void setZipAttachments(boolean zipAttachments) {
        mZipAttachments = zipAttachments;
    }

    public boolean isAllowIdentities() {
        return mAllowIdentities;
    }

    public void setAllowIdentities(boolean allowIdentities) {
        mAllowIdentities = allowIdentities;
    }

    public String getSocialEmail() {
        return mSocialEmail;
    }

    public void setSocialEmail(String socialEmail) {
        mSocialEmail = socialEmail;
    }

    public boolean isSocialIsLoggedIn() {
        return mSocialIsLoggedIn;
    }

    public void setSocialIsLoggedIn(boolean socialIsLoggedIn) {
        mSocialIsLoggedIn = socialIsLoggedIn;
    }

    public Links getLinks() {
        return mLinks;
    }

    public void setLinks(Links links) {
        mLinks = links;
    }

    public boolean isSocialGoogle() {
        return mSocialGoogle;
    }

    public void setSocialGoogle(boolean socialGoogle) {
        mSocialGoogle = socialGoogle;
    }

    public String getSocialGoogleId() {
        return mSocialGoogleId;
    }

    public void setSocialGoogleId(String socialGoogleId) {
        mSocialGoogleId = socialGoogleId;
    }

    public String getSocialGoogleScopes() {
        return mSocialGoogleScopes;
    }

    public void setSocialGoogleScopes(String socialGoogleScopes) {
        mSocialGoogleScopes = socialGoogleScopes;
    }

    public boolean isSocialDropbox() {
        return mSocialDropbox;
    }

    public void setSocialDropbox(boolean socialDropbox) {
        mSocialDropbox = socialDropbox;
    }

    public String getSocialDropboxId() {
        return mSocialDropboxId;
    }

    public void setSocialDropboxId(String socialDropboxId) {
        mSocialDropboxId = socialDropboxId;
    }

    public String getSocialDropboxScopes() {
        return mSocialDropboxScopes;
    }

    public void setSocialDropboxScopes(String socialDropboxScopes) {
        mSocialDropboxScopes = socialDropboxScopes;
    }

    public boolean isSocialFacebook() {
        return mSocialFacebook;
    }

    public void setSocialFacebook(boolean socialFacebook) {
        mSocialFacebook = socialFacebook;
    }

    public String getSocialFacebookId() {
        return mSocialFacebookId;
    }

    public void setSocialFacebookId(String socialFacebookId) {
        mSocialFacebookId = socialFacebookId;
    }

    public String getSocialFacebookScopes() {
        return mSocialFacebookScopes;
    }

    public void setSocialFacebookScopes(String socialFacebookScopes) {
        mSocialFacebookScopes = socialFacebookScopes;
    }

    public boolean isSocialTwitter() {
        return mSocialTwitter;
    }

    public void setSocialTwitter(boolean socialTwitter) {
        mSocialTwitter = socialTwitter;
    }

    public String getSocialTwitterId() {
        return mSocialTwitterId;
    }

    public void setSocialTwitterId(String socialTwitterId) {
        mSocialTwitterId = socialTwitterId;
    }

    public String getSocialTwitterScopes() {
        return mSocialTwitterScopes;
    }

    public void setSocialTwitterScopes(String socialTwitterScopes) {
        mSocialTwitterScopes = socialTwitterScopes;
    }

    public Plugins getPlugins() {
        return mPlugins;
    }

    public void setPlugins(Plugins plugins) {
        mPlugins = plugins;
    }

    public boolean isAllowChangePassword() {
        return mAllowChangePassword;
    }

    public void setAllowChangePassword(boolean allowChangePassword) {
        mAllowChangePassword = allowChangePassword;
    }

    public String getLoginStyleImage() {
        return mLoginStyleImage;
    }

    public void setLoginStyleImage(String loginStyleImage) {
        mLoginStyleImage = loginStyleImage;
    }

    public String getAppStyleImage() {
        return mAppStyleImage;
    }

    public void setAppStyleImage(String appStyleImage) {
        mAppStyleImage = appStyleImage;
    }

    public String getHelpdeskStyleImage() {
        return mHelpdeskStyleImage;
    }

    public void setHelpdeskStyleImage(String helpdeskStyleImage) {
        mHelpdeskStyleImage = helpdeskStyleImage;
    }

    public String getHelpdeskThreadAction() {
        return mHelpdeskThreadAction;
    }

    public void setHelpdeskThreadAction(String helpdeskThreadAction) {
        mHelpdeskThreadAction = helpdeskThreadAction;
    }

    public int getDefault() {
        return mDefault;
    }

    public void setDefault(int def) {
        mDefault = def;
    }

    public App getApp() {
        return mApp;
    }

    public void setApp(App app) {
        mApp = app;
    }

    public List<Accounts> getAccounts() {
        return mAccounts;
    }

    public void setAccounts(List<Accounts> accounts) {
        mAccounts = accounts;
    }


    public static class User {
        @SerializedName("IdUser")
        private int mIdUser;
        @SerializedName("MailsPerPage")
        private int mMailsPerPage;
        @SerializedName("ContactsPerPage")
        private int mContactsPerPage;
        @SerializedName("AutoCheckMailInterval")
        private int mAutoCheckMailInterval;
        @SerializedName("DefaultEditor")
        private int mDefaultEditor;
        @SerializedName("Layout")
        private int mLayout;
        @SerializedName("LoginsCount")
        private int mLoginsCount;
        @SerializedName("CanLoginWithPassword")
        private boolean mCanLoginWithPassword;
        @SerializedName("DefaultTheme")
        private String mDefaultTheme;
        @SerializedName("DefaultLanguage")
        private String mDefaultLanguage;
        @SerializedName("DefaultLanguageShort")
        private String mDefaultLanguageShort;
        @SerializedName("DefaultDateFormat")
        private String mDefaultDateFormat;
        @SerializedName("DefaultTimeFormat")
        private int mDefaultTimeFormat;
        @SerializedName("DefaultTimeZone")
        private String mDefaultTimeZone;
        @SerializedName("AllowCompose")
        private boolean mAllowCompose;
        @SerializedName("AllowReply")
        private boolean mAllowReply;
        @SerializedName("AllowForward")
        private boolean mAllowForward;
        @SerializedName("AllowFetcher")
        private boolean mAllowFetcher;
        @SerializedName("SaveMail")
        private int mSaveMail;
        @SerializedName("ThreadsEnabled")
        private boolean mThreadsEnabled;
        @SerializedName("UseThreads")
        private boolean mUseThreads;
        @SerializedName("SaveRepliedMessagesToCurrentFolder")
        private boolean mSaveRepliedMessagesToCurrentFolder;
        @SerializedName("DesktopNotifications")
        private boolean mDesktopNotifications;
        @SerializedName("AllowChangeInputDirection")
        private boolean mAllowChangeInputDirection;
        @SerializedName("EnableOpenPgp")
        private boolean mEnableOpenPgp;
        @SerializedName("AllowAutosaveInDrafts")
        private boolean mAllowAutosaveInDrafts;
        @SerializedName("AutosignOutgoingEmails")
        private boolean mAutosignOutgoingEmails;
        @SerializedName("EmailNotification")
        private String mEmailNotification;
        @SerializedName("OutlookSyncEnable")
        private boolean mOutlookSyncEnable;
        @SerializedName("MobileSyncEnable")
        private boolean mMobileSyncEnable;
        @SerializedName("ShowPersonalContacts")
        private boolean mShowPersonalContacts;
        @SerializedName("ShowGlobalContacts")
        private boolean mShowGlobalContacts;
        @SerializedName("IsCollaborationSupported")
        private boolean mIsCollaborationSupported;
        @SerializedName("AllowFilesSharing")
        private boolean mAllowFilesSharing;
        @SerializedName("IsFilesSupported")
        private boolean mIsFilesSupported;
        @SerializedName("IsHelpdeskSupported")
        private boolean mIsHelpdeskSupported;
        @SerializedName("IsHelpdeskAgent")
        private boolean mIsHelpdeskAgent;
        @SerializedName("AllowHelpdeskNotifications")
        private boolean mAllowHelpdeskNotifications;
        @SerializedName("HelpdeskSignature")
        private String mHelpdeskSignature;
        @SerializedName("HelpdeskSignatureEnable")
        private boolean mHelpdeskSignatureEnable;
        @SerializedName("LastLogin")
        private int mLastLogin;
        @SerializedName("AllowVoice")
        private boolean mAllowVoice;
        @SerializedName("VoiceProvider")
        private String mVoiceProvider;
        @SerializedName("SipRealm")
        private String mSipRealm;
        @SerializedName("SipWebsocketProxyUrl")
        private String mSipWebsocketProxyUrl;
        @SerializedName("SipOutboundProxyUrl")
        private String mSipOutboundProxyUrl;
        @SerializedName("SipCallerID")
        private String mSipCallerID;
        @SerializedName("TwilioNumber")
        private String mTwilioNumber;
        @SerializedName("TwilioEnable")
        private boolean mTwilioEnable;
        @SerializedName("SipEnable")
        private boolean mSipEnable;
        @SerializedName("SipImpi")
        private String mSipImpi;
        @SerializedName("SipPassword")
        private String mSipPassword;
        @SerializedName("FilesEnable")
        private boolean mFilesEnable;
        @SerializedName("AllowCalendar")
        private boolean mAllowCalendar;
        /**
         * ShowWeekEnds : false
         * ShowWorkDay : true
         * WorkDayStarts : 9
         * WorkDayEnds : 18
         * WeekStartsOn : 1
         * DefaultTab : 3
         * SyncLogin : test@afterlogic.com
         * DavServerUrl : https://p7-dav.afterlogic.com
         * DavPrincipalUrl : https://p7-dav.afterlogic.com/principals/test@afterlogic.com
         * AllowReminders : true
         */

        @SerializedName("Calendar")
        private Calendar mCalendar;
        @SerializedName("CalendarSharing")
        private boolean mCalendarSharing;
        @SerializedName("CalendarAppointments")
        private boolean mCalendarAppointments;
        @SerializedName("IsDemo")
        private boolean mIsDemo;

        public int getIdUser() {
            return mIdUser;
        }

        public void setIdUser(int idUser) {
            mIdUser = idUser;
        }

        public int getMailsPerPage() {
            return mMailsPerPage;
        }

        public void setMailsPerPage(int mailsPerPage) {
            mMailsPerPage = mailsPerPage;
        }

        public int getContactsPerPage() {
            return mContactsPerPage;
        }

        public void setContactsPerPage(int contactsPerPage) {
            mContactsPerPage = contactsPerPage;
        }

        public int getAutoCheckMailInterval() {
            return mAutoCheckMailInterval;
        }

        public void setAutoCheckMailInterval(int autoCheckMailInterval) {
            mAutoCheckMailInterval = autoCheckMailInterval;
        }

        public int getDefaultEditor() {
            return mDefaultEditor;
        }

        public void setDefaultEditor(int defaultEditor) {
            mDefaultEditor = defaultEditor;
        }

        public int getLayout() {
            return mLayout;
        }

        public void setLayout(int layout) {
            mLayout = layout;
        }

        public int getLoginsCount() {
            return mLoginsCount;
        }

        public void setLoginsCount(int loginsCount) {
            mLoginsCount = loginsCount;
        }

        public boolean isCanLoginWithPassword() {
            return mCanLoginWithPassword;
        }

        public void setCanLoginWithPassword(boolean canLoginWithPassword) {
            mCanLoginWithPassword = canLoginWithPassword;
        }

        public String getDefaultTheme() {
            return mDefaultTheme;
        }

        public void setDefaultTheme(String defaultTheme) {
            mDefaultTheme = defaultTheme;
        }

        public String getDefaultLanguage() {
            return mDefaultLanguage;
        }

        public void setDefaultLanguage(String defaultLanguage) {
            mDefaultLanguage = defaultLanguage;
        }

        public String getDefaultLanguageShort() {
            return mDefaultLanguageShort;
        }

        public void setDefaultLanguageShort(String defaultLanguageShort) {
            mDefaultLanguageShort = defaultLanguageShort;
        }

        public String getDefaultDateFormat() {
            return mDefaultDateFormat;
        }

        public void setDefaultDateFormat(String defaultDateFormat) {
            mDefaultDateFormat = defaultDateFormat;
        }

        public int getDefaultTimeFormat() {
            return mDefaultTimeFormat;
        }

        public void setDefaultTimeFormat(int defaultTimeFormat) {
            mDefaultTimeFormat = defaultTimeFormat;
        }

        public String getDefaultTimeZone() {
            return mDefaultTimeZone;
        }

        public void setDefaultTimeZone(String defaultTimeZone) {
            mDefaultTimeZone = defaultTimeZone;
        }

        public boolean isAllowCompose() {
            return mAllowCompose;
        }

        public void setAllowCompose(boolean allowCompose) {
            mAllowCompose = allowCompose;
        }

        public boolean isAllowReply() {
            return mAllowReply;
        }

        public void setAllowReply(boolean allowReply) {
            mAllowReply = allowReply;
        }

        public boolean isAllowForward() {
            return mAllowForward;
        }

        public void setAllowForward(boolean allowForward) {
            mAllowForward = allowForward;
        }

        public boolean isAllowFetcher() {
            return mAllowFetcher;
        }

        public void setAllowFetcher(boolean allowFetcher) {
            mAllowFetcher = allowFetcher;
        }

        public int getSaveMail() {
            return mSaveMail;
        }

        public void setSaveMail(int saveMail) {
            mSaveMail = saveMail;
        }

        public boolean isThreadsEnabled() {
            return mThreadsEnabled;
        }

        public void setThreadsEnabled(boolean threadsEnabled) {
            mThreadsEnabled = threadsEnabled;
        }

        public boolean isUseThreads() {
            return mUseThreads;
        }

        public void setUseThreads(boolean useThreads) {
            mUseThreads = useThreads;
        }

        public boolean isSaveRepliedMessagesToCurrentFolder() {
            return mSaveRepliedMessagesToCurrentFolder;
        }

        public void setSaveRepliedMessagesToCurrentFolder(boolean saveRepliedMessagesToCurrentFolder) {
            mSaveRepliedMessagesToCurrentFolder = saveRepliedMessagesToCurrentFolder;
        }

        public boolean isDesktopNotifications() {
            return mDesktopNotifications;
        }

        public void setDesktopNotifications(boolean desktopNotifications) {
            mDesktopNotifications = desktopNotifications;
        }

        public boolean isAllowChangeInputDirection() {
            return mAllowChangeInputDirection;
        }

        public void setAllowChangeInputDirection(boolean allowChangeInputDirection) {
            mAllowChangeInputDirection = allowChangeInputDirection;
        }

        public boolean isEnableOpenPgp() {
            return mEnableOpenPgp;
        }

        public void setEnableOpenPgp(boolean enableOpenPgp) {
            mEnableOpenPgp = enableOpenPgp;
        }

        public boolean isAllowAutosaveInDrafts() {
            return mAllowAutosaveInDrafts;
        }

        public void setAllowAutosaveInDrafts(boolean allowAutosaveInDrafts) {
            mAllowAutosaveInDrafts = allowAutosaveInDrafts;
        }

        public boolean isAutosignOutgoingEmails() {
            return mAutosignOutgoingEmails;
        }

        public void setAutosignOutgoingEmails(boolean autosignOutgoingEmails) {
            mAutosignOutgoingEmails = autosignOutgoingEmails;
        }

        public String getEmailNotification() {
            return mEmailNotification;
        }

        public void setEmailNotification(String emailNotification) {
            mEmailNotification = emailNotification;
        }

        public boolean isOutlookSyncEnable() {
            return mOutlookSyncEnable;
        }

        public void setOutlookSyncEnable(boolean outlookSyncEnable) {
            mOutlookSyncEnable = outlookSyncEnable;
        }

        public boolean isMobileSyncEnable() {
            return mMobileSyncEnable;
        }

        public void setMobileSyncEnable(boolean mobileSyncEnable) {
            mMobileSyncEnable = mobileSyncEnable;
        }

        public boolean isShowPersonalContacts() {
            return mShowPersonalContacts;
        }

        public void setShowPersonalContacts(boolean showPersonalContacts) {
            mShowPersonalContacts = showPersonalContacts;
        }

        public boolean isShowGlobalContacts() {
            return mShowGlobalContacts;
        }

        public void setShowGlobalContacts(boolean showGlobalContacts) {
            mShowGlobalContacts = showGlobalContacts;
        }

        public boolean isIsCollaborationSupported() {
            return mIsCollaborationSupported;
        }

        public void setIsCollaborationSupported(boolean isCollaborationSupported) {
            mIsCollaborationSupported = isCollaborationSupported;
        }

        public boolean isAllowFilesSharing() {
            return mAllowFilesSharing;
        }

        public void setAllowFilesSharing(boolean allowFilesSharing) {
            mAllowFilesSharing = allowFilesSharing;
        }

        public boolean isIsFilesSupported() {
            return mIsFilesSupported;
        }

        public void setIsFilesSupported(boolean isFilesSupported) {
            mIsFilesSupported = isFilesSupported;
        }

        public boolean isIsHelpdeskSupported() {
            return mIsHelpdeskSupported;
        }

        public void setIsHelpdeskSupported(boolean isHelpdeskSupported) {
            mIsHelpdeskSupported = isHelpdeskSupported;
        }

        public boolean isIsHelpdeskAgent() {
            return mIsHelpdeskAgent;
        }

        public void setIsHelpdeskAgent(boolean isHelpdeskAgent) {
            mIsHelpdeskAgent = isHelpdeskAgent;
        }

        public boolean isAllowHelpdeskNotifications() {
            return mAllowHelpdeskNotifications;
        }

        public void setAllowHelpdeskNotifications(boolean allowHelpdeskNotifications) {
            mAllowHelpdeskNotifications = allowHelpdeskNotifications;
        }

        public String getHelpdeskSignature() {
            return mHelpdeskSignature;
        }

        public void setHelpdeskSignature(String helpdeskSignature) {
            mHelpdeskSignature = helpdeskSignature;
        }

        public boolean isHelpdeskSignatureEnable() {
            return mHelpdeskSignatureEnable;
        }

        public void setHelpdeskSignatureEnable(boolean helpdeskSignatureEnable) {
            mHelpdeskSignatureEnable = helpdeskSignatureEnable;
        }

        public int getLastLogin() {
            return mLastLogin;
        }

        public void setLastLogin(int lastLogin) {
            mLastLogin = lastLogin;
        }

        public boolean isAllowVoice() {
            return mAllowVoice;
        }

        public void setAllowVoice(boolean allowVoice) {
            mAllowVoice = allowVoice;
        }

        public String getVoiceProvider() {
            return mVoiceProvider;
        }

        public void setVoiceProvider(String voiceProvider) {
            mVoiceProvider = voiceProvider;
        }

        public String getSipRealm() {
            return mSipRealm;
        }

        public void setSipRealm(String sipRealm) {
            mSipRealm = sipRealm;
        }

        public String getSipWebsocketProxyUrl() {
            return mSipWebsocketProxyUrl;
        }

        public void setSipWebsocketProxyUrl(String sipWebsocketProxyUrl) {
            mSipWebsocketProxyUrl = sipWebsocketProxyUrl;
        }

        public String getSipOutboundProxyUrl() {
            return mSipOutboundProxyUrl;
        }

        public void setSipOutboundProxyUrl(String sipOutboundProxyUrl) {
            mSipOutboundProxyUrl = sipOutboundProxyUrl;
        }

        public String getSipCallerID() {
            return mSipCallerID;
        }

        public void setSipCallerID(String sipCallerID) {
            mSipCallerID = sipCallerID;
        }

        public String getTwilioNumber() {
            return mTwilioNumber;
        }

        public void setTwilioNumber(String twilioNumber) {
            mTwilioNumber = twilioNumber;
        }

        public boolean isTwilioEnable() {
            return mTwilioEnable;
        }

        public void setTwilioEnable(boolean twilioEnable) {
            mTwilioEnable = twilioEnable;
        }

        public boolean isSipEnable() {
            return mSipEnable;
        }

        public void setSipEnable(boolean sipEnable) {
            mSipEnable = sipEnable;
        }

        public String getSipImpi() {
            return mSipImpi;
        }

        public void setSipImpi(String sipImpi) {
            mSipImpi = sipImpi;
        }

        public String getSipPassword() {
            return mSipPassword;
        }

        public void setSipPassword(String sipPassword) {
            mSipPassword = sipPassword;
        }

        public boolean isFilesEnable() {
            return mFilesEnable;
        }

        public void setFilesEnable(boolean filesEnable) {
            mFilesEnable = filesEnable;
        }

        public boolean isAllowCalendar() {
            return mAllowCalendar;
        }

        public void setAllowCalendar(boolean allowCalendar) {
            mAllowCalendar = allowCalendar;
        }

        public Calendar getCalendar() {
            return mCalendar;
        }

        public void setCalendar(Calendar calendar) {
            mCalendar = calendar;
        }

        public boolean isCalendarSharing() {
            return mCalendarSharing;
        }

        public void setCalendarSharing(boolean calendarSharing) {
            mCalendarSharing = calendarSharing;
        }

        public boolean isCalendarAppointments() {
            return mCalendarAppointments;
        }

        public void setCalendarAppointments(boolean calendarAppointments) {
            mCalendarAppointments = calendarAppointments;
        }

        public boolean isIsDemo() {
            return mIsDemo;
        }

        public void setIsDemo(boolean isDemo) {
            mIsDemo = isDemo;
        }

        public static class Calendar {
            @SerializedName("ShowWeekEnds")
            private boolean mShowWeekEnds;
            @SerializedName("ShowWorkDay")
            private boolean mShowWorkDay;
            @SerializedName("WorkDayStarts")
            private int mWorkDayStarts;
            @SerializedName("WorkDayEnds")
            private int mWorkDayEnds;
            @SerializedName("WeekStartsOn")
            private int mWeekStartsOn;
            @SerializedName("DefaultTab")
            private int mDefaultTab;
            @SerializedName("SyncLogin")
            private String mSyncLogin;
            @SerializedName("DavServerUrl")
            private String mDavServerUrl;
            @SerializedName("DavPrincipalUrl")
            private String mDavPrincipalUrl;
            @SerializedName("AllowReminders")
            private boolean mAllowReminders;

            public boolean isShowWeekEnds() {
                return mShowWeekEnds;
            }

            public void setShowWeekEnds(boolean showWeekEnds) {
                mShowWeekEnds = showWeekEnds;
            }

            public boolean isShowWorkDay() {
                return mShowWorkDay;
            }

            public void setShowWorkDay(boolean showWorkDay) {
                mShowWorkDay = showWorkDay;
            }

            public int getWorkDayStarts() {
                return mWorkDayStarts;
            }

            public void setWorkDayStarts(int workDayStarts) {
                mWorkDayStarts = workDayStarts;
            }

            public int getWorkDayEnds() {
                return mWorkDayEnds;
            }

            public void setWorkDayEnds(int workDayEnds) {
                mWorkDayEnds = workDayEnds;
            }

            public int getWeekStartsOn() {
                return mWeekStartsOn;
            }

            public void setWeekStartsOn(int weekStartsOn) {
                mWeekStartsOn = weekStartsOn;
            }

            public int getDefaultTab() {
                return mDefaultTab;
            }

            public void setDefaultTab(int defaultTab) {
                mDefaultTab = defaultTab;
            }

            public String getSyncLogin() {
                return mSyncLogin;
            }

            public void setSyncLogin(String syncLogin) {
                mSyncLogin = syncLogin;
            }

            public String getDavServerUrl() {
                return mDavServerUrl;
            }

            public void setDavServerUrl(String davServerUrl) {
                mDavServerUrl = davServerUrl;
            }

            public String getDavPrincipalUrl() {
                return mDavPrincipalUrl;
            }

            public void setDavPrincipalUrl(String davPrincipalUrl) {
                mDavPrincipalUrl = davPrincipalUrl;
            }

            public boolean isAllowReminders() {
                return mAllowReminders;
            }

            public void setAllowReminders(boolean allowReminders) {
                mAllowReminders = allowReminders;
            }
        }
    }

    public static class Links {
        @SerializedName("ImportingContacts")
        private String mImportingContacts;
        @SerializedName("OutlookSyncPlugin32")
        private String mOutlookSyncPlugin32;
        @SerializedName("OutlookSyncPlugin64")
        private String mOutlookSyncPlugin64;
        @SerializedName("OutlookSyncPluginReadMore")
        private String mOutlookSyncPluginReadMore;

        public String getImportingContacts() {
            return mImportingContacts;
        }

        public void setImportingContacts(String importingContacts) {
            mImportingContacts = importingContacts;
        }

        public String getOutlookSyncPlugin32() {
            return mOutlookSyncPlugin32;
        }

        public void setOutlookSyncPlugin32(String outlookSyncPlugin32) {
            mOutlookSyncPlugin32 = outlookSyncPlugin32;
        }

        public String getOutlookSyncPlugin64() {
            return mOutlookSyncPlugin64;
        }

        public void setOutlookSyncPlugin64(String outlookSyncPlugin64) {
            mOutlookSyncPlugin64 = outlookSyncPlugin64;
        }

        public String getOutlookSyncPluginReadMore() {
            return mOutlookSyncPluginReadMore;
        }

        public void setOutlookSyncPluginReadMore(String outlookSyncPluginReadMore) {
            mOutlookSyncPluginReadMore = outlookSyncPluginReadMore;
        }
    }

    public static class Plugins {
        @SerializedName("ExternalServices")
        private ExternalServices mExternalServices;

        public ExternalServices getExternalServices() {
            return mExternalServices;
        }

        public void setExternalServices(ExternalServices externalServices) {
            mExternalServices = externalServices;
        }

        public static class ExternalServices {
            /**
             * @Object : Object/CTenantSocials
             * Id : 631514845250-lsi1lf1j7vqsb5rq18lh4t499glp7f8b.apps.googleusercontent.com
             * Name : Google
             * LowerName : google
             * Allow : true
             * Scopes : ["auth","filestorage"]
             */

            @SerializedName("Connectors")
            private List<Connectors> mConnectors;

            public List<Connectors> getConnectors() {
                return mConnectors;
            }

            public void setConnectors(List<Connectors> connectors) {
                mConnectors = connectors;
            }

            public static class Connectors {
                @SerializedName("Id")
                private String mId;
                @SerializedName("Name")
                private String mName;
                @SerializedName("LowerName")
                private String mLowerName;
                @SerializedName("Allow")
                private boolean mAllow;
                @SerializedName("Scopes")
                private List<String> mScopes;

                public String getId() {
                    return mId;
                }

                public void setId(String id) {
                    mId = id;
                }

                public String getName() {
                    return mName;
                }

                public void setName(String name) {
                    mName = name;
                }

                public String getLowerName() {
                    return mLowerName;
                }

                public void setLowerName(String lowerName) {
                    mLowerName = lowerName;
                }

                public boolean isAllow() {
                    return mAllow;
                }

                public void setAllow(boolean allow) {
                    mAllow = allow;
                }

                public List<String> getScopes() {
                    return mScopes;
                }

                public void setScopes(List<String> scopes) {
                    mScopes = scopes;
                }
            }
        }
    }

    public static class App {
        @SerializedName("AllowUsersChangeInterfaceSettings")
        private boolean mAllowUsersChangeInterfaceSettings;
        @SerializedName("AllowUsersChangeEmailSettings")
        private boolean mAllowUsersChangeEmailSettings;
        @SerializedName("AllowUsersAddNewAccounts")
        private boolean mAllowUsersAddNewAccounts;
        @SerializedName("AllowOpenPGP")
        private boolean mAllowOpenPGP;
        @SerializedName("AllowWebMail")
        private boolean mAllowWebMail;
        @SerializedName("DefaultTab")
        private String mDefaultTab;
        @SerializedName("AllowIosProfile")
        private boolean mAllowIosProfile;
        @SerializedName("PasswordMinLength")
        private int mPasswordMinLength;
        @SerializedName("PasswordMustBeComplex")
        private boolean mPasswordMustBeComplex;
        @SerializedName("AllowRegistration")
        private boolean mAllowRegistration;
        @SerializedName("AllowPasswordReset")
        private boolean mAllowPasswordReset;
        @SerializedName("SiteName")
        private String mSiteName;
        @SerializedName("DefaultLanguage")
        private String mDefaultLanguage;
        @SerializedName("DefaultLanguageShort")
        private String mDefaultLanguageShort;
        @SerializedName("DefaultTheme")
        private String mDefaultTheme;
        @SerializedName("AttachmentSizeLimit")
        private int mAttachmentSizeLimit;
        @SerializedName("ImageUploadSizeLimit")
        private int mImageUploadSizeLimit;
        @SerializedName("FileSizeLimit")
        private int mFileSizeLimit;
        @SerializedName("AutoSave")
        private boolean mAutoSave;
        @SerializedName("IdleSessionTimeout")
        private int mIdleSessionTimeout;
        @SerializedName("AllowInsertImage")
        private boolean mAllowInsertImage;
        @SerializedName("AllowBodySize")
        private boolean mAllowBodySize;
        @SerializedName("MaxBodySize")
        private int mMaxBodySize;
        @SerializedName("MaxSubjectSize")
        private int mMaxSubjectSize;
        @SerializedName("JoinReplyPrefixes")
        private boolean mJoinReplyPrefixes;
        @SerializedName("AllowAppRegisterMailto")
        private boolean mAllowAppRegisterMailto;
        @SerializedName("AllowPrefetch")
        private boolean mAllowPrefetch;
        @SerializedName("AllowLanguageOnLogin")
        private boolean mAllowLanguageOnLogin;
        @SerializedName("FlagsLangSelect")
        private boolean mFlagsLangSelect;
        @SerializedName("LoginFormType")
        private int mLoginFormType;
        @SerializedName("LoginSignMeType")
        private int mLoginSignMeType;
        @SerializedName("LoginAtDomainValue")
        private String mLoginAtDomainValue;
        @SerializedName("DemoWebMail")
        private boolean mDemoWebMail;
        @SerializedName("DemoWebMailLogin")
        private String mDemoWebMailLogin;
        @SerializedName("DemoWebMailPassword")
        private String mDemoWebMailPassword;
        @SerializedName("GoogleAnalyticsAccount")
        private String mGoogleAnalyticsAccount;
        @SerializedName("CustomLoginUrl")
        private String mCustomLoginUrl;
        @SerializedName("CustomLogoutUrl")
        private String mCustomLogoutUrl;
        @SerializedName("ShowQuotaBar")
        private boolean mShowQuotaBar;
        @SerializedName("ServerUseUrlRewrite")
        private boolean mServerUseUrlRewrite;
        @SerializedName("ServerUrlRewriteBase")
        private String mServerUrlRewriteBase;
        @SerializedName("IosDetectOnLogin")
        private boolean mIosDetectOnLogin;
        @SerializedName("AllowContactsSharing")
        private boolean mAllowContactsSharing;
        @SerializedName("LoginDescription")
        private String mLoginDescription;
        @SerializedName("RegistrationDomains")
        private List<?> mRegistrationDomains;
        @SerializedName("RegistrationQuestions")
        private List<?> mRegistrationQuestions;
        /**
         * name : English
         * value : English
         */

        @SerializedName("Languages")
        private List<Languages> mLanguages;
        @SerializedName("Themes")
        private List<String> mThemes;
        @SerializedName("DateFormats")
        private List<String> mDateFormats;

        public boolean isAllowUsersChangeInterfaceSettings() {
            return mAllowUsersChangeInterfaceSettings;
        }

        public void setAllowUsersChangeInterfaceSettings(boolean allowUsersChangeInterfaceSettings) {
            mAllowUsersChangeInterfaceSettings = allowUsersChangeInterfaceSettings;
        }

        public boolean isAllowUsersChangeEmailSettings() {
            return mAllowUsersChangeEmailSettings;
        }

        public void setAllowUsersChangeEmailSettings(boolean allowUsersChangeEmailSettings) {
            mAllowUsersChangeEmailSettings = allowUsersChangeEmailSettings;
        }

        public boolean isAllowUsersAddNewAccounts() {
            return mAllowUsersAddNewAccounts;
        }

        public void setAllowUsersAddNewAccounts(boolean allowUsersAddNewAccounts) {
            mAllowUsersAddNewAccounts = allowUsersAddNewAccounts;
        }

        public boolean isAllowOpenPGP() {
            return mAllowOpenPGP;
        }

        public void setAllowOpenPGP(boolean allowOpenPGP) {
            mAllowOpenPGP = allowOpenPGP;
        }

        public boolean isAllowWebMail() {
            return mAllowWebMail;
        }

        public void setAllowWebMail(boolean allowWebMail) {
            mAllowWebMail = allowWebMail;
        }

        public String getDefaultTab() {
            return mDefaultTab;
        }

        public void setDefaultTab(String defaultTab) {
            mDefaultTab = defaultTab;
        }

        public boolean isAllowIosProfile() {
            return mAllowIosProfile;
        }

        public void setAllowIosProfile(boolean allowIosProfile) {
            mAllowIosProfile = allowIosProfile;
        }

        public int getPasswordMinLength() {
            return mPasswordMinLength;
        }

        public void setPasswordMinLength(int passwordMinLength) {
            mPasswordMinLength = passwordMinLength;
        }

        public boolean isPasswordMustBeComplex() {
            return mPasswordMustBeComplex;
        }

        public void setPasswordMustBeComplex(boolean passwordMustBeComplex) {
            mPasswordMustBeComplex = passwordMustBeComplex;
        }

        public boolean isAllowRegistration() {
            return mAllowRegistration;
        }

        public void setAllowRegistration(boolean allowRegistration) {
            mAllowRegistration = allowRegistration;
        }

        public boolean isAllowPasswordReset() {
            return mAllowPasswordReset;
        }

        public void setAllowPasswordReset(boolean allowPasswordReset) {
            mAllowPasswordReset = allowPasswordReset;
        }

        public String getSiteName() {
            return mSiteName;
        }

        public void setSiteName(String siteName) {
            mSiteName = siteName;
        }

        public String getDefaultLanguage() {
            return mDefaultLanguage;
        }

        public void setDefaultLanguage(String defaultLanguage) {
            mDefaultLanguage = defaultLanguage;
        }

        public String getDefaultLanguageShort() {
            return mDefaultLanguageShort;
        }

        public void setDefaultLanguageShort(String defaultLanguageShort) {
            mDefaultLanguageShort = defaultLanguageShort;
        }

        public String getDefaultTheme() {
            return mDefaultTheme;
        }

        public void setDefaultTheme(String defaultTheme) {
            mDefaultTheme = defaultTheme;
        }

        public int getAttachmentSizeLimit() {
            return mAttachmentSizeLimit;
        }

        public void setAttachmentSizeLimit(int attachmentSizeLimit) {
            mAttachmentSizeLimit = attachmentSizeLimit;
        }

        public int getImageUploadSizeLimit() {
            return mImageUploadSizeLimit;
        }

        public void setImageUploadSizeLimit(int imageUploadSizeLimit) {
            mImageUploadSizeLimit = imageUploadSizeLimit;
        }

        public int getFileSizeLimit() {
            return mFileSizeLimit;
        }

        public void setFileSizeLimit(int fileSizeLimit) {
            mFileSizeLimit = fileSizeLimit;
        }

        public boolean isAutoSave() {
            return mAutoSave;
        }

        public void setAutoSave(boolean autoSave) {
            mAutoSave = autoSave;
        }

        public int getIdleSessionTimeout() {
            return mIdleSessionTimeout;
        }

        public void setIdleSessionTimeout(int idleSessionTimeout) {
            mIdleSessionTimeout = idleSessionTimeout;
        }

        public boolean isAllowInsertImage() {
            return mAllowInsertImage;
        }

        public void setAllowInsertImage(boolean allowInsertImage) {
            mAllowInsertImage = allowInsertImage;
        }

        public boolean isAllowBodySize() {
            return mAllowBodySize;
        }

        public void setAllowBodySize(boolean allowBodySize) {
            mAllowBodySize = allowBodySize;
        }

        public int getMaxBodySize() {
            return mMaxBodySize;
        }

        public void setMaxBodySize(int maxBodySize) {
            mMaxBodySize = maxBodySize;
        }

        public int getMaxSubjectSize() {
            return mMaxSubjectSize;
        }

        public void setMaxSubjectSize(int maxSubjectSize) {
            mMaxSubjectSize = maxSubjectSize;
        }

        public boolean isJoinReplyPrefixes() {
            return mJoinReplyPrefixes;
        }

        public void setJoinReplyPrefixes(boolean joinReplyPrefixes) {
            mJoinReplyPrefixes = joinReplyPrefixes;
        }

        public boolean isAllowAppRegisterMailto() {
            return mAllowAppRegisterMailto;
        }

        public void setAllowAppRegisterMailto(boolean allowAppRegisterMailto) {
            mAllowAppRegisterMailto = allowAppRegisterMailto;
        }

        public boolean isAllowPrefetch() {
            return mAllowPrefetch;
        }

        public void setAllowPrefetch(boolean allowPrefetch) {
            mAllowPrefetch = allowPrefetch;
        }

        public boolean isAllowLanguageOnLogin() {
            return mAllowLanguageOnLogin;
        }

        public void setAllowLanguageOnLogin(boolean allowLanguageOnLogin) {
            mAllowLanguageOnLogin = allowLanguageOnLogin;
        }

        public boolean isFlagsLangSelect() {
            return mFlagsLangSelect;
        }

        public void setFlagsLangSelect(boolean flagsLangSelect) {
            mFlagsLangSelect = flagsLangSelect;
        }

        public int getLoginFormType() {
            return mLoginFormType;
        }

        public void setLoginFormType(int loginFormType) {
            mLoginFormType = loginFormType;
        }

        public int getLoginSignMeType() {
            return mLoginSignMeType;
        }

        public void setLoginSignMeType(int loginSignMeType) {
            mLoginSignMeType = loginSignMeType;
        }

        public String getLoginAtDomainValue() {
            return mLoginAtDomainValue;
        }

        public void setLoginAtDomainValue(String loginAtDomainValue) {
            mLoginAtDomainValue = loginAtDomainValue;
        }

        public boolean isDemoWebMail() {
            return mDemoWebMail;
        }

        public void setDemoWebMail(boolean demoWebMail) {
            mDemoWebMail = demoWebMail;
        }

        public String getDemoWebMailLogin() {
            return mDemoWebMailLogin;
        }

        public void setDemoWebMailLogin(String demoWebMailLogin) {
            mDemoWebMailLogin = demoWebMailLogin;
        }

        public String getDemoWebMailPassword() {
            return mDemoWebMailPassword;
        }

        public void setDemoWebMailPassword(String demoWebMailPassword) {
            mDemoWebMailPassword = demoWebMailPassword;
        }

        public String getGoogleAnalyticsAccount() {
            return mGoogleAnalyticsAccount;
        }

        public void setGoogleAnalyticsAccount(String googleAnalyticsAccount) {
            mGoogleAnalyticsAccount = googleAnalyticsAccount;
        }

        public String getCustomLoginUrl() {
            return mCustomLoginUrl;
        }

        public void setCustomLoginUrl(String customLoginUrl) {
            mCustomLoginUrl = customLoginUrl;
        }

        public String getCustomLogoutUrl() {
            return mCustomLogoutUrl;
        }

        public void setCustomLogoutUrl(String customLogoutUrl) {
            mCustomLogoutUrl = customLogoutUrl;
        }

        public boolean isShowQuotaBar() {
            return mShowQuotaBar;
        }

        public void setShowQuotaBar(boolean showQuotaBar) {
            mShowQuotaBar = showQuotaBar;
        }

        public boolean isServerUseUrlRewrite() {
            return mServerUseUrlRewrite;
        }

        public void setServerUseUrlRewrite(boolean serverUseUrlRewrite) {
            mServerUseUrlRewrite = serverUseUrlRewrite;
        }

        public String getServerUrlRewriteBase() {
            return mServerUrlRewriteBase;
        }

        public void setServerUrlRewriteBase(String serverUrlRewriteBase) {
            mServerUrlRewriteBase = serverUrlRewriteBase;
        }

        public boolean isIosDetectOnLogin() {
            return mIosDetectOnLogin;
        }

        public void setIosDetectOnLogin(boolean iosDetectOnLogin) {
            mIosDetectOnLogin = iosDetectOnLogin;
        }

        public boolean isAllowContactsSharing() {
            return mAllowContactsSharing;
        }

        public void setAllowContactsSharing(boolean allowContactsSharing) {
            mAllowContactsSharing = allowContactsSharing;
        }

        public String getLoginDescription() {
            return mLoginDescription;
        }

        public void setLoginDescription(String loginDescription) {
            mLoginDescription = loginDescription;
        }

        public List<?> getRegistrationDomains() {
            return mRegistrationDomains;
        }

        public void setRegistrationDomains(List<?> registrationDomains) {
            mRegistrationDomains = registrationDomains;
        }

        public List<?> getRegistrationQuestions() {
            return mRegistrationQuestions;
        }

        public void setRegistrationQuestions(List<?> registrationQuestions) {
            mRegistrationQuestions = registrationQuestions;
        }

        public List<Languages> getLanguages() {
            return mLanguages;
        }

        public void setLanguages(List<Languages> languages) {
            mLanguages = languages;
        }

        public List<String> getThemes() {
            return mThemes;
        }

        public void setThemes(List<String> themes) {
            mThemes = themes;
        }

        public List<String> getDateFormats() {
            return mDateFormats;
        }

        public void setDateFormats(List<String> dateFormats) {
            mDateFormats = dateFormats;
        }

        public static class Languages {
            @SerializedName("name")
            private String mName;
            @SerializedName("value")
            private String mValue;

            public String getName() {
                return mName;
            }

            public void setName(String name) {
                mName = name;
            }

            public String getValue() {
                return mValue;
            }

            public void setValue(String value) {
                mValue = value;
            }
        }
    }

    public static class Accounts {
        @SerializedName("AccountID")
        private int mAccountID;
        @SerializedName("Email")
        private String mEmail;
        @SerializedName("FriendlyName")
        private String mFriendlyName;
        /**
         * Signature : <div data-crea="font-wrapper" style="font-family: Tahoma; font-size: 16px; direction: ltr">/ Alex<br></div>
         * Type : 1
         * Options : 1
         */

        @SerializedName("Signature")
        private Signature mSignature;
        @SerializedName("IsPasswordSpecified")
        private boolean mIsPasswordSpecified;
        @SerializedName("AllowMail")
        private boolean mAllowMail;

        public int getAccountID() {
            return mAccountID;
        }

        public void setAccountID(int accountID) {
            mAccountID = accountID;
        }

        public String getEmail() {
            return mEmail;
        }

        public void setEmail(String email) {
            mEmail = email;
        }

        public String getFriendlyName() {
            return mFriendlyName;
        }

        public void setFriendlyName(String friendlyName) {
            mFriendlyName = friendlyName;
        }

        public Signature getSignature() {
            return mSignature;
        }

        public void setSignature(Signature signature) {
            mSignature = signature;
        }

        public boolean isIsPasswordSpecified() {
            return mIsPasswordSpecified;
        }

        public void setIsPasswordSpecified(boolean isPasswordSpecified) {
            mIsPasswordSpecified = isPasswordSpecified;
        }

        public boolean isAllowMail() {
            return mAllowMail;
        }

        public void setAllowMail(boolean allowMail) {
            mAllowMail = allowMail;
        }

        public static class Signature {
            @SerializedName("Signature")
            private String mSignature;
            @SerializedName("Type")
            private int mType;
            @SerializedName("Options")
            private int mOptions;

            public String getSignature() {
                return mSignature;
            }

            public void setSignature(String signature) {
                mSignature = signature;
            }

            public int getType() {
                return mType;
            }

            public void setType(int type) {
                mType = type;
            }

            public int getOptions() {
                return mOptions;
            }

            public void setOptions(int options) {
                mOptions = options;
            }
        }
    }
}
