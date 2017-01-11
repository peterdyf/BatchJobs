package net.peter.batch.common.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.NonTransientFlatFileException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Intention:<br>
 * This class is mainly a source copy from FlatFileItemReader<br>
 * And replace original comments-skipped with patterns-skipped<br>
 * For doRead() could not been overridden easily.
 * 
 * @author Peter.DI
 *
 * @param <T>
 * @see org.springframework.batch.item.file.FlatFileItemReader
 */
class SimulatedFlatFileItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T>, InitializingBean {

	private final Logger log = LoggerFactory.getLogger(getClass());

	// default encoding for input files
	public static final String DEFAULT_CHARSET = Charset.defaultCharset().name();

	private RecordSeparatorPolicy recordSeparatorPolicy = new SimpleRecordSeparatorPolicy();

	private Resource resource;

	private BufferedReader reader;

	private int lineCount;

	private Pattern[] skippedPatterns;

	private boolean noInput;

	private String encoding = DEFAULT_CHARSET;

	private LineMapper<T> lineMapper;

	private int linesToSkip;

	private LineCallbackHandler skippedLinesCallback;

	private boolean strict = true;

	private BufferedReaderFactory bufferedReaderFactory = new DefaultBufferedReaderFactory();

	public SimulatedFlatFileItemReader() {
		super();
		setName(ClassUtils.getShortName(SimulatedFlatFileItemReader.class));
	}

	/**
	 * In strict mode the reader will throw an exception on
	 * {@link #open(org.springframework.batch.item.ExecutionContext)} if the
	 * input resource does not exist.
	 * 
	 * @param strict
	 *            <code>true</code> by default
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/**
	 * @param skippedLinesCallback
	 *            will be called for each one of the initial skipped lines
	 *            before any items are read.
	 */
	public void setSkippedLinesCallback(LineCallbackHandler skippedLinesCallback) {
		this.skippedLinesCallback = skippedLinesCallback;
	}

	/**
	 * Public setter for the number of lines to skip at the start of a file. Can
	 * be used if the file contains a header without useful (column name)
	 * information, and without a comment delimiter at the beginning of the
	 * lines.
	 * 
	 * @param linesToSkip
	 *            the number of lines to skip
	 */
	public void setLinesToSkip(int linesToSkip) {
		this.linesToSkip = linesToSkip;
	}

	/**
	 * Setter for line mapper. This property is required to be set.
	 * 
	 * @param lineMapper
	 *            maps line to item
	 */
	public void setLineMapper(LineMapper<T> lineMapper) {
		this.lineMapper = lineMapper;
	}

	/**
	 * Setter for the encoding for this input source. Default value is
	 * {@link #DEFAULT_CHARSET}.
	 * 
	 * @param encoding
	 *            a properties object which possibly contains the encoding for
	 *            this input file;
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Factory for the {@link BufferedReader} that will be used to extract lines
	 * from the file. The default is fine for plain text files, but this is a
	 * useful strategy for binary files where the standard BufferedReaader from
	 * java.io is limiting.
	 * 
	 * @param bufferedReaderFactory
	 *            the bufferedReaderFactory to set
	 */
	public void setBufferedReaderFactory(BufferedReaderFactory bufferedReaderFactory) {
		this.bufferedReaderFactory = bufferedReaderFactory;
	}

	/**
	 * Setter for skipped patterns. Can be used to ignore header lines as well
	 * 
	 * @param skippedPatterns
	 *            an array of skipped line Patterns.
	 */
	@SuppressWarnings("PMD.UseVarargs")
	public void setSkippedPatterns(String[] skippedPatterns) {
		this.skippedPatterns = Stream.of(skippedPatterns).map(Pattern::compile).toArray(Pattern[]::new);
	}

	/**
	 * Public setter for the input resource.
	 */
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Public setter for the recordSeparatorPolicy. Used to determine where the
	 * line endings are and do things like continue over a line ending if inside
	 * a quoted string.
	 * 
	 * @param recordSeparatorPolicy
	 *            the recordSeparatorPolicy to set
	 */
	public void setRecordSeparatorPolicy(RecordSeparatorPolicy recordSeparatorPolicy) {
		this.recordSeparatorPolicy = recordSeparatorPolicy;
	}

	/**
	 * @return string corresponding to logical record according to
	 *         {@link #setRecordSeparatorPolicy(RecordSeparatorPolicy)} (might
	 *         span multiple lines in file).
	 */
	@Override
	@SuppressWarnings("PMD.AvoidCatchingGenericException") // existing code
	protected T doRead() throws IOException {
		if (noInput) {
			return null;
		}

		String line = readLine();

		if (line == null) {
			return null;
		} else {
			try {
				return lineMapper.mapLine(line, lineCount);
			} catch (Exception ex) {
				throw new FlatFileParseException("Parsing error at line: " + lineCount + " in resource=[" + resource.getDescription() + "], input=[" + line + "]", ex, line, lineCount);
			}
		}
	}

	/**
	 * @return next line (skip comments).getCurrentResource
	 */
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis") // PMD bug
	private String readLine() {

		if (reader == null) {
			throw new ReaderNotOpenException("Reader must be open before it can be read.");
		}

		String line = null;

		try {
			line = this.reader.readLine();
			if (line == null) {
				return null;
			}
			lineCount++;
			while (isSkipped(line)) {
				line = reader.readLine();
				if (line == null) {
					return null;
				}
				lineCount++;
			}

			line = applyRecordSeparatorPolicy(line);
		} catch (IOException e) {
			// Prevent IOException from recurring indefinitely
			// if client keeps catching and re-calling
			noInput = true;
			throw new NonTransientFlatFileException("Unable to read from resource: [" + resource + "]", e, line, lineCount);
		}
		return line;
	}

	private boolean isSkipped(String line) {
		if (skippedPatterns == null) {
			return false;
		}
		for (Pattern pattern : skippedPatterns) {
			if (pattern.matcher(line).find()) {
				if (skippedLinesCallback != null) {
					skippedLinesCallback.handleLine(line);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected void doClose() throws IOException {
		lineCount = 0;
		if (reader != null) {
			reader.close();
		}
	}

	@Override
	protected void doOpen() throws IOException {
		Assert.notNull(resource, "Input resource must be set");
		Assert.notNull(recordSeparatorPolicy, "RecordSeparatorPolicy must be set");

		noInput = true;
		if (!resource.exists()) {
			if (strict) {
				throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + resource);
			}
			log.warn("Input resource does not exist {}", resource.getDescription());
			return;
		}

		if (!resource.isReadable()) {
			if (strict) {
				throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): " + resource);
			}
			log.warn("Input resource is not readable {}", resource.getDescription());
			return;
		}

		reader = bufferedReaderFactory.create(resource, encoding);
		for (int i = 0; i < linesToSkip; i++) {
			if (skippedLinesCallback != null) {
				skippedLinesCallback.handleLine(readLine());
			}
		}
		noInput = false;
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(lineMapper, "LineMapper is required");
	}

	@Override
	protected void jumpToItem(int itemIndex) {
		for (int i = 0; i < itemIndex; i++) {
			readLine();
		}
	}

	@SuppressWarnings("PMD.AvoidReassigningParameters") // legacy code
	private String applyRecordSeparatorPolicy(String line) throws IOException {

		String record = line;
		while (line != null && !recordSeparatorPolicy.isEndOfRecord(record)) {
			line = this.reader.readLine();
			if (line == null) {
				if (StringUtils.hasText(record)) {
					// A record was partially complete since it hasn't ended but
					// the line is null
					throw new FlatFileParseException("Unexpected end of file before record complete", record, lineCount);
				} else {
					// Record has no text but it might still be post processed
					// to something (skipping preProcess since that was already
					// done)
					break;
				}
			} else {
				lineCount++;
			}
			record = recordSeparatorPolicy.preProcess(record) + line;
		}

		return recordSeparatorPolicy.postProcess(record);

	}
}
