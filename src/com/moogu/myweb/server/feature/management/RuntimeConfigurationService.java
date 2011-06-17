package com.moogu.myweb.server.feature.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import com.moogu.myweb.shared.management.IlmsConfigConstants;

@Component
public class RuntimeConfigurationService {

    private static final Logger LOGGER = Logger.getLogger(RuntimeConfigurationService.class);

    public final static String CACHE_ENABLED_KEY = "server.cache.enabled";

    public static String[] KEYS = { IlmsConfigConstants.RESERVE_EUR_OVERVIEW_SCREEN_REFRESH_SECONDS };

    @Autowired
    private SimpleJdbcTemplate jdbcTemplate;

    public void deleteAllProperties() {
        jdbcTemplate.update("delete from TB_PROPERTIES");
    }

    public int getFiamsTimeoutMillis(String useCaseId, int defaultValue) {
        final String value = getProperty(useCaseId);
        if (value == null) {
            RuntimeConfigurationService.LOGGER.debug("No timeout set in database for " + useCaseId
                            + ". Use default value : "
                            + defaultValue);
            return defaultValue;
        } else {
            try {
                final int result = Integer.parseInt(value);
                RuntimeConfigurationService.LOGGER.debug("Timeout find in database for " + useCaseId + " is " + result);
                return result;
            } catch (final NumberFormatException e) {
                RuntimeConfigurationService.LOGGER.warn("Timeout configuration for " + useCaseId
                                + " is not parseable to integer. Will use defauly value : "
                                + defaultValue);
                return defaultValue;
            }
        }
    }

    public Map<String, String> getPropertes() {
        final Map<String, String> result = new HashMap<String, String>();
        final List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from TB_PROPERTIES");
        for (final Map<String, Object> row : list) {
            final String key = (String) row.get("key");
            final String value = (String) row.get("value");
            result.put(key, value);
        }
        return result;
    }

    public String getProperty(String key) {
        final List<Map<String, Object>> list = jdbcTemplate.queryForList(
                        "select value from TB_PROPERTIES where key = ?",
                        key);
        if (list.isEmpty()) {
            RuntimeConfigurationService.LOGGER.warn("Property for key = [" + key + "] not found");
            return null;
        } else {
            final String value = (String) list.get(0).get("value");
            if (RuntimeConfigurationService.LOGGER.isDebugEnabled()) {
                RuntimeConfigurationService.LOGGER.debug("Value for key = [" + key + "] = [" + value + "]");
            }
            return value;
        }
    }

    public void setProperty(String key, String value) {
        final int keyCount = jdbcTemplate.queryForInt("select count(*) from TB_PROPERTIES where key = ?", key);
        if (keyCount > 0) {
            jdbcTemplate.update("update TB_PROPERTIES set value = ? where key = ?", value, key);
        } else {
            jdbcTemplate.update("insert into TB_PROPERTIES values(?, ?)", key, value);
        }
    }
}
