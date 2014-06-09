package mobi.eyeline.ips.properties;

import com.eyeline.utils.config.ConfigException;
import com.eyeline.utils.config.xml.XmlConfig;
import com.eyeline.utils.config.xml.XmlConfigSection;

import java.util.Properties;

public interface Config {

    public String getSmtpHost();
    public int getSmtpPort();
    public String getSmtpUsername();
    public String getSmtpPassword();
    public String getMailFrom();
    public String getLoginUrl();

    public Properties getDatabaseProperties();

    public boolean isMadvUpdateEnabled();
    public int getMadvUpdateDelayMinutes();
    public String getMadvUrl();
    public String getMadvUserLogin();
    public String getMadvUserPassword();

    public String getSadsPushUrl();
    public int getSadsMaxSessions();

    public String getDeliveryUssdPushUrl();
    public String getDeliveryNIPushUrl();

    public int getPushThreadsNumber();
    public int getMessageQueueBaseline();
    public int getStateUpdateBatchSize();
    public int getRetryAttempts();
    public long getExpirationDelaySeconds();

    public String getSkinDefault();

    public boolean getExposeJmxBeans();

    public static class XmlConfigImpl implements Config {

        private final String smtpHost;
        private final int smtpPort;

        private final String smtpUsername;
        private final String smtpPassword;

        private final String mailFrom;
        private final String loginUrl;

        private final Properties databaseProperties;

        private final boolean madvUpdateEnabled;
        private final int madvUpdateDelayMinutes;
        private final String madvUrl;
        private final String madvUserLogin;
        private final String madvUserPassword;

        private final String sadsPushUrl;
        private final int sadsMaxSessions;

        private final String skinDefault;

        private final String deliveryUssdPushUrl;
        private final String deliveryNIPushUrl;
        private final int pushThreadsNumber;
        private final int messageQueueBaseline;
        private final int stateUpdateBatchSize;
        private final int retryAttempts;
        private final long expirationDelaySeconds;

        private final boolean exposeJmxBeans;

        public XmlConfigImpl(XmlConfig xmlConfig) throws ConfigException {

            final XmlConfigSection mail = xmlConfig.getSection("mail");
            {
                smtpHost = mail.getString("smtp.host");
                smtpPort = mail.getInt("smtp.port");
                smtpUsername = mail.getString("smtp.username");
                smtpPassword = mail.getString("smtp.password");
                mailFrom = mail.getString("from");

                loginUrl = mail.getString("login.url");
            }

            final XmlConfigSection database = xmlConfig.getSection("database");
            databaseProperties = database.toProperties(null);

            final XmlConfigSection madv = xmlConfig.getSection("madv");
            {
                madvUpdateEnabled = madv.getBool("update.enabled", true);
                madvUpdateDelayMinutes = madv.getInt("update.delay.minutes");
                madvUrl = madv.getString("url");
                madvUserLogin = madv.getString("login");
                madvUserPassword = madv.getString("password");
            }

            final XmlConfigSection sads = xmlConfig.getSection("sads");
            {
                sadsPushUrl = sads.getString("push.url");
                sadsMaxSessions = sads.getInt("max.sessions", 4);
            }

            final XmlConfigSection ui = xmlConfig.getSection("ui");
            {
                skinDefault = ui.getString("skin.default");
            }

            final XmlConfigSection deliveries = xmlConfig.getSection("deliveries");
            {
                deliveryUssdPushUrl = deliveries.getString("push.ussd.url");
                deliveryNIPushUrl = deliveries.getString("push.nidialog.url");
                pushThreadsNumber = deliveries.getInt("push.threads.number");
                messageQueueBaseline = deliveries.getInt("push.message.queue");
                stateUpdateBatchSize = deliveries.getInt("push.update.batch.size");
                retryAttempts = deliveries.getInt("retry.attempts");
                expirationDelaySeconds = deliveries.getLong("expiration.delay.seconds");
            }

            final XmlConfigSection other = xmlConfig.getSection("other");
            {
                exposeJmxBeans = other.getBool("expose.jmx.beans");
            }
        }

        @Override
        public String getSmtpHost() {
            return smtpHost;
        }

        @Override
        public int getSmtpPort() {
            return smtpPort;
        }

        @Override
        public String getSmtpUsername() {
            return smtpUsername;
        }

        @Override
        public String getSmtpPassword() {
            return smtpPassword;
        }

        @Override
        public String getMailFrom() {
            return mailFrom;
        }

        @Override
        public String getLoginUrl() {
            return loginUrl;
        }

        @Override
        public Properties getDatabaseProperties() {
            return databaseProperties;
        }

        @Override
        public boolean isMadvUpdateEnabled() {
            return madvUpdateEnabled;
        }

        @Override
        public int getMadvUpdateDelayMinutes() {
            return madvUpdateDelayMinutes;
        }

        @Override
        public String getMadvUrl() {
            return madvUrl;
        }

        @Override
        public String getMadvUserLogin() {
            return madvUserLogin;
        }

        @Override
        public String getMadvUserPassword() {
            return madvUserPassword;
        }

        @Override
        public String getSadsPushUrl() {
            return sadsPushUrl;
        }

        @Override
        public String getDeliveryUssdPushUrl() {
            return deliveryUssdPushUrl;
        }

        @Override
        public String getDeliveryNIPushUrl() {
            return deliveryNIPushUrl;
        }

        @Override
        public int getSadsMaxSessions() {
            return sadsMaxSessions;
        }

        @Override
        public String getSkinDefault() {
            return skinDefault;
        }

        @Override
        public int getPushThreadsNumber() {
            return pushThreadsNumber;
        }

        @Override
        public int getMessageQueueBaseline() {
            return messageQueueBaseline;
        }

        @Override
        public int getStateUpdateBatchSize() {
            return stateUpdateBatchSize;
        }

        @Override
        public int getRetryAttempts() {
            return retryAttempts;
        }

        @Override
        public long getExpirationDelaySeconds() {
            return expirationDelaySeconds;
        }

        @Override
        public boolean getExposeJmxBeans() {
            return exposeJmxBeans;
        }
    }
}
