package com.jstarcraft.core.log;

import java.io.File;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.conversion.csv.CsvUtility;
import com.jstarcraft.core.log.exception.LogException;

/**
 * 日志文件测试
 * 
 * @author Birdy
 */
public class Log4Java1StorageTestCase {

	private void checkContent(File file, String csv) throws Exception {
		List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
		Assert.assertThat(lines.size(), CoreMatchers.is(2));
		for (String line : lines) {
			Assert.assertThat(line, CoreMatchers.is(csv));
		}
	}

	@Test
	public void testLogFile() throws Exception {
		String type = "type";
		String file = "test";
		File oldLog = new File("logs1/" + type + "/2017-01-01/" + file + ".00-00-00.log");
		File newLog = new File("logs1/" + type + "/2017-01-01/" + file + ".00-05-00.log");
		FileUtils.deleteQuietly(oldLog);
		FileUtils.deleteQuietly(newLog);

		CsvObject log = CsvObject.instanceOf(0, "birdy", "hong", 1, Instant.now(), CsvEnumeration.TERRAN);
		String csv = CsvUtility.object2String(log, CsvObject.class);

		// 获取记录器
		Logger logger = Logger.getLogger("Storage");
		ZonedDateTime dateTime = ZonedDateTime.of(2017, 1, 1, 0, 4, 0, 0, ZoneId.of("UTC"));
		logger.info(new Object[] { Instant.from(dateTime), log, type, file });
		logger.info(new Object[] { Instant.from(dateTime), log, type, file });
		dateTime = dateTime.plusMinutes(2);
		logger.info(new Object[] { Instant.from(dateTime), log, type, file });
		logger.info(new Object[] { Instant.from(dateTime), log, type, file });

		try {
			logger.info(new Object[] { Instant.from(dateTime), log, type });
			Assert.fail();
		} catch (LogException exception) {
		}
		try {
			logger.info(new Object[] { Instant.from(dateTime), log, type, type, type });
			Assert.fail();
		} catch (LogException exception) {
		}

		checkContent(oldLog, csv);
		checkContent(newLog, csv);
	}

}
